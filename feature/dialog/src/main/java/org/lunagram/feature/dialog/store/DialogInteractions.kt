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
import org.lunagram.core.models.canShowMessageViewers
import org.lunagram.core.models.canShowOutboxReadDate
import org.lunagram.core.models.geoPlace
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

internal fun DialogExecutor.toggleChecklist(messageId: Int, itemId: Int) {
    val current = snapshot().messages.firstOrNull { it.id.id == messageId } ?: return
    val list = current.checklist ?: return
    val item = list.items.firstOrNull { it.id == itemId } ?: return
    if (!current.outgoing && !list.othersCanComplete) return
    val done = !item.done
    val updated = current.copy(
        checklist = list.copy(
            items = list.items.map { entry ->
                if (entry.id == itemId) entry.copy(done = done) else entry
            },
        ),
    )
    emit(
        Msg.Messages(
            value = snapshot().messages.map { if (it.id.id == messageId) updated else it },
            fromCache = true,
            replace = true,
        ),
    )
    work.launch {
        val result = if (done) {
            client.toggleTodoCompleted(chatId, messageId, listOf(itemId), emptyList())
        } else {
            client.toggleTodoCompleted(chatId, messageId, emptyList(), listOf(itemId))
        }
        if (result is Outcome.Err) handleError(result.telegramError, false)
    }
}

internal fun DialogExecutor.votePoll(messageId: Int, options: List<ByteArray>) {
    if (options.isEmpty()) return
    work.launch {
        val result = client.sendPollVote(chatId, messageId, options)
        if (result is Outcome.Err) handleError(result.telegramError, false)
    }
}

internal fun DialogExecutor.appendChecklistItems(messageId: Int, firstId: Int, titles: List<String>) {
    val cleaned = titles.map { it.trim() }.filter { it.isNotEmpty() }
    if (cleaned.isEmpty()) return
    work.launch {
        val result = client.appendChecklistItems(chatId, messageId, firstId, cleaned)
        if (result is Outcome.Err) handleError(result.telegramError, false)
    }
}

internal fun DialogExecutor.sendLocation(latitude: Double, longitude: Double, livePeriodSeconds: Int) {
    val reply = snapshot().replyTo
    emit(Msg.ReplyTo(null))
    val randomId = pendingRandomId()
    val pending = pendingOutgoing(
        path = "",
        kind = "geo",
        caption = null,
        fileName = buildString {
            append("{\"lat\":")
            append(latitude)
            append(",\"long\":")
            append(longitude)
            if (livePeriodSeconds > 0) {
                append(",\"live\":true,\"p\":")
                append(livePeriodSeconds)
            }
            append('}')
        },
        fileSize = null,
        duration = 0,
        width = 0,
        height = 0,
        randomId = randomId,
        groupedId = null,
        reply = reply,
        entities = emptyList(),
    )
    emit(Msg.Append(pending))
    work.launch {
        warmup?.upsertMessages(listOf(pending))
        val (replyId, _) = ForumIo.sendReplyIds(threadTopMsgId, reply?.id?.id)
        val result = client.sendLocation(
            chatId,
            latitude,
            longitude,
            livePeriodSeconds,
            replyToMsgId = replyId.takeIf { it > 0 },
        )
        when (result) {
            is Outcome.Ok -> {
                val confirmed = snapshot().messages.firstOrNull { message ->
                    message.id.id != pending.id.id &&
                        !message.pending &&
                        message.mediaKind == "geo" &&
                        sameGeo(message, latitude, longitude)
                }
                if (confirmed != null) {
                    replaceOutgoing(pending.id.id, confirmed.copy(outgoing = true))
                }
            }
            is Outcome.Err -> failOrKeepOutgoing(pending.id.id, result.telegramError)
        }
    }
}

internal fun DialogExecutor.pressBotButton(messageId: Int, button: ReplyButton, fromKeyboard: Boolean) {
    when (button.type) {
        ReplyButtonType.Text -> {
            if (!snapshot().canSendPlain) return
            applyDraft(button.text)
            send()
            if (fromKeyboard) {
                val keyboard = ReplyMarkups.latestBotKeyboard(snapshot().messages)
                if (keyboard?.singleUse == true) {
                    emit(Msg.KeyboardDismissedKey(ReplyMarkups.serialize(keyboard)))
                }
            }
        }
        ReplyButtonType.Url -> emit(Msg.BotUrl(button.url))
        ReplyButtonType.Copy -> emit(Msg.CopyText(button.copyText ?: button.text))
        ReplyButtonType.SwitchInline -> {
            if (!button.samePeer) {
                emit(Msg.BotNotice("inline-switch"))
                return
            }
            val senderId = snapshot().messages
                .firstOrNull { it.id.id == messageId }
                ?.senderId
            val cachedUsername = senderId?.let { snapshot().senders[it]?.username }
            work.launch {
                val username = cachedUsername ?: senderId?.let { id ->
                    when (val profile = client.getProfile(id)) {
                        is Outcome.Ok -> {
                            sessionStore?.upsertProfile(profile.value)
                            profile.value.username
                        }
                        is Outcome.Err -> null
                    }
                }
                if (username.isNullOrBlank()) {
                    emit(Msg.BotNotice("inline-switch"))
                    return@launch
                }
                val query = button.query.orEmpty()
                applyDraft("@$username${query.takeIf { it.isNotBlank() }?.let { " $it" } ?: " "}")
            }
        }
        ReplyButtonType.User -> emit(Msg.BotUrl(button.url?.let { "tg://user?id=$it" } ?: button.url))
        ReplyButtonType.Disabled -> Unit
        ReplyButtonType.WebApp,
        ReplyButtonType.Login,
        ReplyButtonType.Game,
        ReplyButtonType.Pay,
        ReplyButtonType.RequestContact,
        ReplyButtonType.RequestLocation,
        ReplyButtonType.RequestPoll,
        ReplyButtonType.RequestPeer -> emit(Msg.BotNotice(button.type.name.lowercase()))
        ReplyButtonType.Callback -> {
            if (button.requiresPassword) {
                emit(Msg.BotNotice("callback-password"))
                return
            }
            val data = button.dataHex.orEmpty()
            if (data.isEmpty() || messageId <= 0) return
            work.launch {
                when (val result = client.getBotCallbackAnswer(chatId, messageId, data)) {
                    is Outcome.Ok -> {
                        result.value.url?.let { emit(Msg.BotUrl(it)) }
                        result.value.message?.let {
                            emit(Msg.BotNotice(it, alert = result.value.alert))
                        }
                    }
                    is Outcome.Err -> handleError(result.telegramError, false)
                }
            }
        }
        ReplyButtonType.Other -> Unit
    }
}

