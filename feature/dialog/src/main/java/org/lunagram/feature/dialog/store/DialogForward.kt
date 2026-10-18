package org.lunagram.feature.dialog.store

import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.lunagram.core.common.AppLog
import org.lunagram.core.common.Outcome
import org.lunagram.core.common.PerfLog
import org.lunagram.core.common.telegram.TelegramError
import org.lunagram.core.markup.forSend
import org.lunagram.core.models.Chat
import org.lunagram.core.models.ChatActionKind
import org.lunagram.core.models.ForumIo
import org.lunagram.core.models.ForumTopic
import org.lunagram.core.models.InlineBotResult
import org.lunagram.core.models.InlineBotResults
import org.lunagram.core.models.Message
import org.lunagram.core.models.MessageId
import org.lunagram.core.models.MessageViewers
import org.lunagram.core.models.OutboxReadState
import org.lunagram.core.models.PeerId
import org.lunagram.core.models.Profile
import org.lunagram.core.models.ReactionChoice
import org.lunagram.core.models.ReadReceiptConfig
import org.lunagram.core.models.ReplyButton
import org.lunagram.core.models.ReplyButtonType
import org.lunagram.core.models.ReplyMarkups
import org.lunagram.core.models.SavedGif
import org.lunagram.core.models.StickerPack
import org.lunagram.core.models.StyledText
import org.lunagram.core.models.TextEntities
import org.lunagram.core.models.TextEntity
import org.lunagram.core.models.TypingPresence
import org.lunagram.core.models.UploadItem
import org.lunagram.core.models.canForwardFrom
import org.lunagram.core.models.canSelectAsForwardRecipient
import org.lunagram.core.models.canShowMessageViewers
import org.lunagram.core.models.canShowOutboxReadDate
import org.lunagram.core.models.geoPlace
import org.lunagram.core.models.requiresForwardPhotoRight
import org.lunagram.core.models.isPlaceholderPeerTitle
import org.lunagram.core.models.peerAvatarCacheKey
import org.lunagram.core.models.playedMediaKind
import org.lunagram.core.models.preferredPeerTitle
import org.lunagram.feature.dialog.ComposerAt
import org.lunagram.feature.dialog.ComposerAtToken
import org.lunagram.feature.dialog.ComposerPanels
import org.lunagram.feature.dialog.DialogStore
import org.lunagram.feature.dialog.DraftMention
import org.lunagram.feature.dialog.InlineBotQuery
import org.lunagram.feature.dialog.MentionCandidate
import org.lunagram.feature.dialog.PinnedBarMemory
import org.lunagram.feature.dialog.SEARCH_DEBOUNCE_MS
import org.lunagram.feature.dialog.SavedGifMemory
import org.lunagram.feature.dialog.SenderTagMemory
import org.lunagram.feature.dialog.StickerCatalogMemory
import org.lunagram.feature.dialog.StickerPackMemory
import org.lunagram.feature.dialog.applyMessageEdit
import org.lunagram.feature.dialog.historyPagingAllowed
import org.lunagram.feature.dialog.isTransientSendFailure
import org.lunagram.feature.dialog.jumpNeedsFetch
import org.lunagram.feature.dialog.localMediaCacheKey
import org.lunagram.feature.dialog.localMediaPath
import org.lunagram.feature.dialog.mergeSenderTags
import org.lunagram.feature.dialog.nextRetryText
import org.lunagram.feature.dialog.parseUpdateMessageId
import org.lunagram.network.bridge.MtprotoClient
import org.lunagram.network.bridge.MtprotoUpdate
import kotlin.time.Duration.Companion.milliseconds

internal fun eligibleForwardTargets(
    chats: List<Chat>,
    requiresPhotos: Boolean,
    selfPeerId: PeerId?,
): List<Chat> = chats.filter {
    it.canSelectAsForwardRecipient(requiresPhotos, selfPeerId)
}

internal fun DialogExecutor.openForward(message: Message) {
    emit(Msg.ForwardHint(null))
    emit(Msg.ForwardMessage(message))
    emit(Msg.ForwardQuery(""))
    val needsPhotos = message.requiresForwardPhotoRight()
    work.launch {
        val selfId = sessionStore?.readAuthorizedUserId()
        var targets = eligibleForwardTargets(warmup?.chats().orEmpty(), needsPhotos, selfId)
        if (targets.isEmpty()) {
            when (val result = client.getChats()) {
                is Outcome.Ok -> {
                    warmup?.upsertChats(result.value)
                    targets = eligibleForwardTargets(result.value, needsPhotos, selfId)
                }
                is Outcome.Err -> handleError(result.telegramError, false)
            }
        }
        emit(Msg.ForwardTargets(targets))
    }
}

internal fun DialogExecutor.forwardTo(toChatId: PeerId) {
    val current = snapshot()
    val message = current.forwardMessage ?: return
    if (current.forwarding || !message.canForwardFrom(current.canForward)) return
    val dest = current.forwardTargets.firstOrNull { it.id == toChatId } ?: return
    emit(Msg.Forwarding(true))
    work.launch {
        when (
            val result = client.forwardMessages(
                fromChatId = chatId,
                messageIds = listOf(message.id.id),
                toChatId = dest.id,
                dropAuthor = false,
            )
        ) {
            is Outcome.Ok -> {
                if (toChatId == chatId) {
                    result.value.forEach { emit(Msg.Append(it)) }
                    warmup?.upsertMessages(result.value)
                }
                val title = snapshot().forwardTargets.firstOrNull { it.id == toChatId }?.title
                    ?: toChatId.value.toString()
                emit(Msg.ForwardHint(title))
                emit(Msg.ForwardMessage(null))
                emit(Msg.ForwardQuery(""))
            }
            is Outcome.Err -> handleError(result.telegramError, false)
        }
        emit(Msg.Forwarding(false))
    }
}
