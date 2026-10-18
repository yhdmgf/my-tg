package org.lunagram.mtproto

import uniffi.lunagram_mtproto.AuthCodeSent
import uniffi.lunagram_mtproto.AuthSignedIn
import uniffi.lunagram_mtproto.BotCallbackAnswerDto
import uniffi.lunagram_mtproto.ChatDto
import uniffi.lunagram_mtproto.ContactsSearchDto
import uniffi.lunagram_mtproto.DiscussionDto
import uniffi.lunagram_mtproto.FolderDto
import uniffi.lunagram_mtproto.ForumTopicsPageDto
import uniffi.lunagram_mtproto.GlobalMessageSearchDto
import uniffi.lunagram_mtproto.InitConnectionInfo
import uniffi.lunagram_mtproto.InlineBotResultsDto
import uniffi.lunagram_mtproto.InstantViewDto
import uniffi.lunagram_mtproto.MessageDto
import uniffi.lunagram_mtproto.MessagesPageDto
import uniffi.lunagram_mtproto.MtprotoException
import uniffi.lunagram_mtproto.NotifyExceptionDto
import uniffi.lunagram_mtproto.NotifySettingsDto
import uniffi.lunagram_mtproto.OutboxReadDto
import uniffi.lunagram_mtproto.PollVotersDto
import uniffi.lunagram_mtproto.ProfileDto
import uniffi.lunagram_mtproto.ReactionChoiceDto
import uniffi.lunagram_mtproto.ReactionPeersDto
import uniffi.lunagram_mtproto.ReadParticipantsDto
import uniffi.lunagram_mtproto.ReadReceiptConfigDto
import uniffi.lunagram_mtproto.ResolvedPeerDto
import uniffi.lunagram_mtproto.SavedGifDto
import uniffi.lunagram_mtproto.StickerCatalogDto
import uniffi.lunagram_mtproto.StickerListDto
import uniffi.lunagram_mtproto.StickerPackDto
import uniffi.lunagram_mtproto.UpdateBatchDto
import uniffi.lunagram_mtproto.UpdateDrainDto
import uniffi.lunagram_mtproto.UpdateEventDto
import uniffi.lunagram_mtproto.UpdatesStateDto
import uniffi.lunagram_mtproto.UploadItemDto
import uniffi.lunagram_mtproto.uniffiEnsureInitialized
import java.io.File
import uniffi.lunagram_mtproto.animatedEmojiMax as nativeAnimatedEmojiMax
import uniffi.lunagram_mtproto.appendTodoItems as nativeAppendTodoItems
import uniffi.lunagram_mtproto.checkPassword as nativeCheckPassword
import uniffi.lunagram_mtproto.clearActiveDialog as nativeClearActiveDialog
import uniffi.lunagram_mtproto.clearProxy as nativeClearProxy
import uniffi.lunagram_mtproto.clientApiId as nativeClientApiId
import uniffi.lunagram_mtproto.clientExists as nativeClientExists
import uniffi.lunagram_mtproto.clientUsesTestDc as nativeClientUsesTestDc
import uniffi.lunagram_mtproto.connect as nativeConnect
import uniffi.lunagram_mtproto.contactsSearch as nativeContactsSearch
import uniffi.lunagram_mtproto.createEncryptedClient as nativeCreateEncryptedClient
import uniffi.lunagram_mtproto.customEmojiIsFree as nativeCustomEmojiIsFree
import uniffi.lunagram_mtproto.decryptPushPayload as nativeDecryptPushPayload
import uniffi.lunagram_mtproto.deleteFolder as nativeDeleteFolder
import uniffi.lunagram_mtproto.deleteMessage as nativeDeleteMessage
import uniffi.lunagram_mtproto.destroyClient as nativeDestroyClient
import uniffi.lunagram_mtproto.downloadCustomEmoji as nativeDownloadCustomEmoji
import uniffi.lunagram_mtproto.downloadMessageMedia as nativeDownloadMessageMedia
import uniffi.lunagram_mtproto.downloadMessageThumb as nativeDownloadMessageThumb
import uniffi.lunagram_mtproto.acknowledgeUpdateBatch as nativeAcknowledgeUpdateBatch
import uniffi.lunagram_mtproto.drainUpdates as nativeDrainUpdates
import uniffi.lunagram_mtproto.pendingUpdateBatches as nativePendingUpdateBatches
import uniffi.lunagram_mtproto.editForumTopicHidden as nativeEditForumTopicHidden
import uniffi.lunagram_mtproto.editTextMessage as nativeEditTextMessage
import uniffi.lunagram_mtproto.forwardMessages as nativeForwardMessages
import uniffi.lunagram_mtproto.getAllStickers as nativeGetAllStickers
import uniffi.lunagram_mtproto.getBotCallbackAnswer as nativeGetBotCallbackAnswer
import uniffi.lunagram_mtproto.getChats as nativeGetChats
import uniffi.lunagram_mtproto.getCommonChats as nativeGetCommonChats
import uniffi.lunagram_mtproto.getDiscussionMessage as nativeGetDiscussionMessage
import uniffi.lunagram_mtproto.getEmojiStickers as nativeGetEmojiStickers
import uniffi.lunagram_mtproto.getFolders as nativeGetFolders
import uniffi.lunagram_mtproto.getForumTopics as nativeGetForumTopics
import uniffi.lunagram_mtproto.getForumTopicsById as nativeGetForumTopicsById
import uniffi.lunagram_mtproto.getGroupAdminTags as nativeGetGroupAdminTags
import uniffi.lunagram_mtproto.getHistory as nativeGetHistory
import uniffi.lunagram_mtproto.getHistoryPage as nativeGetHistoryPage
import uniffi.lunagram_mtproto.getInlineBotResults as nativeGetInlineBotResults
import uniffi.lunagram_mtproto.getMessageReactionsList as nativeGetMessageReactionsList
import uniffi.lunagram_mtproto.getMessageReadParticipants as nativeGetMessageReadParticipants
import uniffi.lunagram_mtproto.getNotifyExceptions as nativeGetNotifyExceptions
import uniffi.lunagram_mtproto.getNotifySettings as nativeGetNotifySettings
import uniffi.lunagram_mtproto.getOutboxReadDate as nativeGetOutboxReadDate
import uniffi.lunagram_mtproto.getParticipants as nativeGetParticipants
import uniffi.lunagram_mtproto.getPinnedMessages as nativeGetPinnedMessages
import uniffi.lunagram_mtproto.getPollVotes as nativeGetPollVotes
import uniffi.lunagram_mtproto.getProfile as nativeGetProfile
import uniffi.lunagram_mtproto.getReadReceiptConfig as nativeGetReadReceiptConfig
import uniffi.lunagram_mtproto.getRecentReactions as nativeGetRecentReactions
import uniffi.lunagram_mtproto.getReplies as nativeGetReplies
import uniffi.lunagram_mtproto.getSavedGifs as nativeGetSavedGifs
import uniffi.lunagram_mtproto.getSearchCounters as nativeGetSearchCounters
import uniffi.lunagram_mtproto.getStickerPack as nativeGetStickerPack
import uniffi.lunagram_mtproto.getStickerSet as nativeGetStickerSet
import uniffi.lunagram_mtproto.getStickers as nativeGetStickers
import uniffi.lunagram_mtproto.getUnreadMentions as nativeGetUnreadMentions
import uniffi.lunagram_mtproto.getUnreadReactions as nativeGetUnreadReactions
import uniffi.lunagram_mtproto.getUpdatesState as nativeGetUpdatesState
import uniffi.lunagram_mtproto.getWebPage as nativeGetWebPage
import uniffi.lunagram_mtproto.getWebPagePreview as nativeGetWebPagePreview
import uniffi.lunagram_mtproto.isAuthorized as nativeIsAuthorized
import uniffi.lunagram_mtproto.joinChat as nativeJoinChat
import uniffi.lunagram_mtproto.libraryVersion as nativeLibraryVersion
import uniffi.lunagram_mtproto.loadMoreChats as nativeLoadMoreChats
import uniffi.lunagram_mtproto.logout as nativeLogout
import uniffi.lunagram_mtproto.markDialogUnread as nativeMarkDialogUnread
import uniffi.lunagram_mtproto.peekMessageInlineThumb as nativePeekMessageInlineThumb
import uniffi.lunagram_mtproto.peekPeerInlineThumb as nativePeekPeerInlineThumb
import uniffi.lunagram_mtproto.pingProxy as nativePingProxy
import uniffi.lunagram_mtproto.readDiscussion as nativeReadDiscussion
import uniffi.lunagram_mtproto.readHistory as nativeReadHistory
import uniffi.lunagram_mtproto.readMentions as nativeReadMentions
import uniffi.lunagram_mtproto.readMessageContents as nativeReadMessageContents
import uniffi.lunagram_mtproto.readReactions as nativeReadReactions
import uniffi.lunagram_mtproto.registerDevice as nativeRegisterDevice
import uniffi.lunagram_mtproto.resendAuthCode as nativeResendAuthCode
import uniffi.lunagram_mtproto.resetNotifySettings as nativeResetNotifySettings
import uniffi.lunagram_mtproto.resolveUsername as nativeResolveUsername
import uniffi.lunagram_mtproto.searchGlobal as nativeSearchGlobal
import uniffi.lunagram_mtproto.searchMessages as nativeSearchMessages
import uniffi.lunagram_mtproto.searchMessagesFiltered as nativeSearchMessagesFiltered
import uniffi.lunagram_mtproto.searchMessagesPage as nativeSearchMessagesPage
import uniffi.lunagram_mtproto.sendAuthCode as nativeSendAuthCode
import uniffi.lunagram_mtproto.sendInlineBotResult as nativeSendInlineBotResult
import uniffi.lunagram_mtproto.sendLocation as nativeSendLocation
import uniffi.lunagram_mtproto.sendPhotoMessage as nativeSendPhotoMessage
import uniffi.lunagram_mtproto.sendPollVote as nativeSendPollVote
import uniffi.lunagram_mtproto.sendReaction as nativeSendReaction
import uniffi.lunagram_mtproto.sendSavedGif as nativeSendSavedGif
import uniffi.lunagram_mtproto.sendTextMessage as nativeSendTextMessage
import uniffi.lunagram_mtproto.sendUploadedAlbum as nativeSendUploadedAlbum
import uniffi.lunagram_mtproto.sendUploadedMedia as nativeSendUploadedMedia
import uniffi.lunagram_mtproto.setClientTestDc as nativeSetClientTestDc
import uniffi.lunagram_mtproto.setContactJoinedSilent as nativeSetContactJoinedSilent
import uniffi.lunagram_mtproto.setInitConnectionInfo as nativeSetInitConnectionInfo
import uniffi.lunagram_mtproto.setProxy as nativeSetProxy
import uniffi.lunagram_mtproto.setTransportMode as nativeSetTransportMode
import uniffi.lunagram_mtproto.setTyping as nativeSetTyping
import uniffi.lunagram_mtproto.signIn as nativeSignIn
import uniffi.lunagram_mtproto.startUpdates as nativeStartUpdates
import uniffi.lunagram_mtproto.toggleTodoCompleted as nativeToggleTodoCompleted
import uniffi.lunagram_mtproto.unblockUser as nativeUnblockUser
import uniffi.lunagram_mtproto.unregisterDevice as nativeUnregisterDevice
import uniffi.lunagram_mtproto.updateFolder as nativeUpdateFolder
import uniffi.lunagram_mtproto.updateFolderOrder as nativeUpdateFolderOrder
import uniffi.lunagram_mtproto.updateLazySyncConfig as nativeUpdateLazySyncConfig
import uniffi.lunagram_mtproto.updateNotifySettings as nativeUpdateNotifySettings
import uniffi.lunagram_mtproto.updateStatus as nativeUpdateStatus

object MtprotoNativeUniFfi : MtprotoNative {
    override fun downloadMessageMediaChunk(handle: Long, chatId: Long, messageId: Int, destPath: String, offset: Long): String =
        uniffi.lunagram_mtproto.downloadMessageMediaChunk(handle.toULong(), chatId, messageId, destPath, offset)
    override fun createRequestControl(): Long = uniffi.lunagram_mtproto.createRequestControl().toLong()
    override fun bindRequestControl(id: Long): Long = uniffi.lunagram_mtproto.bindRequestControl(id.toULong()).toLong()
    override fun cancelRequestControl(id: Long) = uniffi.lunagram_mtproto.cancelRequestControl(id.toULong())

    override fun promoteRequestControl(id: Long, dispatchClass: Int) =
        uniffi.lunagram_mtproto.promoteRequestControl(id.toULong(), dispatchClass)
    override fun releaseRequestControl(id: Long) = uniffi.lunagram_mtproto.releaseRequestControl(id.toULong())
    override fun setDispatchClass(classId: Int) =
        uniffi.lunagram_mtproto.setDispatchClass(classId)

    override fun setDownloadConcurrency(lanes: Int, parts: Int) =
        uniffi.lunagram_mtproto.setDownloadConcurrency(lanes, parts)

    override fun downloadConcurrency(): List<Int> =
        uniffi.lunagram_mtproto.downloadConcurrency()

    override fun setFilePartKib(kib: Int) =
        uniffi.lunagram_mtproto.setFilePartKib(kib)

    override fun setDownloadChunkKib(kib: Int) =
        uniffi.lunagram_mtproto.setDownloadChunkKib(kib)

    override fun downloadChunkKib(): Int =
        uniffi.lunagram_mtproto.downloadChunkKib()

    override fun setDownloadProgressListener(listener: ((String, Long, Long) -> Unit)?) {
        val cb = listener ?: return
        uniffi.lunagram_mtproto.setDownloadProgressListener(
            object : uniffi.lunagram_mtproto.DownloadProgressListener {
                override fun onProgress(path: String, downloaded: Long, total: Long) {
                    cb(path, downloaded, total)
                }
            },
        )
    }
    init {
        uniffiEnsureInitialized()
    }

    override fun setTransportMode(mode: String) = nativeSetTransportMode(mode)
    override fun setProxy(kind: String, host: String, port: Int, username: String?, password: String?, secret: ByteArray) = nativeSetProxy(kind, host, port.toUShort(), username, password, secret)

    override fun pingProxy(
        kind: String,
        host: String,
        port: Int,
        username: String?,
        password: String?,
        secret: ByteArray,
    ): Long = nativePingProxy(kind, host, port.toUShort(), username, password, secret)

    override fun clearProxy() = nativeClearProxy()

    override fun libraryVersion(): String = nativeLibraryVersion()

    override fun setInitConnectionInfo(
        deviceModel: String,
        systemVersion: String,
        appVersion: String,
        systemLangCode: String,
        langPack: String,
        langCode: String,
    ) = nativeSetInitConnectionInfo(
        InitConnectionInfo(
            deviceModel = deviceModel,
            systemVersion = systemVersion,
            appVersion = appVersion,
            systemLangCode = systemLangCode,
            langPack = langPack,
            langCode = langCode,
        ),
    )

    override fun perfSetEnabled(enabled: Boolean) =
        uniffi.lunagram_mtproto.perfSetEnabled(enabled)

    override fun perfSnapshot(reset: Boolean): String =
        uniffi.lunagram_mtproto.perfSnapshot(reset)

    override fun createClient(apiId: Int, apiHash: String, sessionPath: String): Long {
        val key = try {
            SessionKeyStore.loadOrCreate(sessionPath)
        } catch (_: Exception) {
            throw MtprotoException.Message("session key unavailable")
        }
        return try {
            nativeCreateEncryptedClient(apiId, apiHash, File(sessionPath).canonicalPath, key).toLong()
        } finally {
            key.fill(0)
        }
    }

    override fun setTestDc(handle: Long, enabled: Boolean) {
        nativeSetClientTestDc(handle.toULong(), enabled)
    }

    override fun usesTestDc(handle: Long): Boolean =
        nativeClientUsesTestDc(handle.toULong())

    override fun isAuthorized(handle: Long): Boolean =
        nativeIsAuthorized(handle.toULong())

    override fun connect(handle: Long) {
        nativeConnect(handle.toULong())
    }

    override fun destroyClient(handle: Long) {
        nativeDestroyClient(handle.toULong())
    }

    override fun clientExists(handle: Long): Boolean = nativeClientExists(handle.toULong())

    override fun clientApiId(handle: Long): Int = nativeClientApiId(handle.toULong())

    override fun sendAuthCode(handle: Long, phone: String): AuthCodeSent =
        nativeSendAuthCode(handle.toULong(), phone)

    override fun resendAuthCode(
        handle: Long,
        phone: String,
        phoneCodeHash: String,
    ): AuthCodeSent = nativeResendAuthCode(handle.toULong(), phone, phoneCodeHash)

    override fun signIn(
        handle: Long,
        phone: String,
        phoneCodeHash: String,
        phoneCode: String,
    ): AuthSignedIn = nativeSignIn(handle.toULong(), phone, phoneCodeHash, phoneCode)

    override fun checkPassword(handle: Long, password: String): AuthSignedIn =
        nativeCheckPassword(handle.toULong(), password)

    override fun logout(handle: Long) {
        nativeLogout(handle.toULong())
    }

    override fun getChats(handle: Long): List<ChatDto> = nativeGetChats(handle.toULong())

    override fun getWallpapers(handle: Long, hash: Long) =
        uniffi.lunagram_mtproto.getWallpapers(handle.toULong(), hash)

    override fun downloadWallpaper(handle: Long, id: Long, accessHash: Long, destPath: String) =
        uniffi.lunagram_mtproto.downloadWallpaper(handle.toULong(), id, accessHash, destPath)

    override fun getWebPage(handle: Long, url: String, hash: Int): InstantViewDto =
        nativeGetWebPage(handle.toULong(), url, hash)

    override fun getWebPagePreview(handle: Long, message: String): InstantViewDto =
        nativeGetWebPagePreview(handle.toULong(), message)

    override fun getFolders(handle: Long): List<FolderDto> = nativeGetFolders(handle.toULong())

    override fun updateFolder(handle: Long, folder: FolderDto) =
        nativeUpdateFolder(handle.toULong(), folder)

    override fun deleteFolder(handle: Long, id: Int) =
        nativeDeleteFolder(handle.toULong(), id)

    override fun updateFolderOrder(handle: Long, order: List<Int>) =
        nativeUpdateFolderOrder(handle.toULong(), order)

    override fun getHistory(handle: Long, chatId: Long, limit: Int): List<MessageDto> =
        nativeGetHistory(handle.toULong(), chatId, limit)

    override fun getHistoryPage(
        handle: Long,
        chatId: Long,
        limit: Int,
        offsetId: Int,
        offsetDate: Int,
        addOffset: Int,
    ): List<MessageDto> =
        nativeGetHistoryPage(handle.toULong(), chatId, limit, offsetId, offsetDate, addOffset)

    override fun getReplies(
        handle: Long,
        chatId: Long,
        msgId: Int,
        limit: Int,
        offsetId: Int,
        addOffset: Int,
    ): List<MessageDto> =
        nativeGetReplies(handle.toULong(), chatId, msgId, limit, offsetId, addOffset)

    override fun getForumTopics(
        handle: Long,
        chatId: Long,
        offsetDate: Int,
        offsetId: Int,
        offsetTopic: Int,
        limit: Int,
    ): ForumTopicsPageDto =
        nativeGetForumTopics(handle.toULong(), chatId, offsetDate, offsetId, offsetTopic, limit)

    override fun getForumTopicsById(
        handle: Long,
        chatId: Long,
        topicIds: List<Int>,
    ): ForumTopicsPageDto =
        nativeGetForumTopicsById(handle.toULong(), chatId, topicIds)

    override fun editForumTopicHidden(
        handle: Long,
        chatId: Long,
        topicId: Int,
        hidden: Boolean,
    ) {
        nativeEditForumTopicHidden(handle.toULong(), chatId, topicId, hidden)
    }

    override fun loadMoreChats(
        handle: Long,
        offsetDate: Int,
        offsetId: Int,
        offsetPeerId: Long,
        folderId: Int,
    ): List<ChatDto> =
        nativeLoadMoreChats(handle.toULong(), offsetDate, offsetId, offsetPeerId, folderId)

    override fun searchMessages(
        handle: Long,
        chatId: Long,
        query: String,
        limit: Int,
    ): List<MessageDto> =
        nativeSearchMessages(handle.toULong(), chatId, query, limit)

    override fun searchMessagesFiltered(
        handle: Long,
        chatId: Long,
        query: String,
        filter: String,
        offsetId: Int,
        addOffset: Int,
        limit: Int,
    ): List<MessageDto> =
        nativeSearchMessagesFiltered(
            handle.toULong(),
            chatId,
            query,
            filter,
            offsetId,
            addOffset,
            limit,
        )

    override fun searchMessagesPage(
        handle: Long,
        chatId: Long,
        query: String,
        filter: String,
        offsetId: Int,
        addOffset: Int,
        limit: Int,
    ): MessagesPageDto =
        nativeSearchMessagesPage(
            handle.toULong(),
            chatId,
            query,
            filter,
            offsetId,
            addOffset,
            limit,
        )

    override fun contactsSearch(
        handle: Long,
        query: String,
        limit: Int,
    ): ContactsSearchDto = nativeContactsSearch(handle.toULong(), query, limit)

    override fun searchGlobal(
        handle: Long,
        query: String,
        offsetRate: Int,
        offsetPeerId: Long,
        offsetId: Int,
        limit: Int,
        folderId: Int,
    ): GlobalMessageSearchDto =
        nativeSearchGlobal(
            handle.toULong(),
            query,
            offsetRate,
            offsetPeerId,
            offsetId,
            limit,
            folderId,
        )

    override fun getPinnedMessages(
        handle: Long,
        chatId: Long,
        limit: Int,
    ): List<MessageDto> =
        nativeGetPinnedMessages(handle.toULong(), chatId, limit)

    override fun getMessageReadParticipants(
        handle: Long,
        chatId: Long,
        msgId: Int,
    ): ReadParticipantsDto =
        nativeGetMessageReadParticipants(handle.toULong(), chatId, msgId)

    override fun getMessageReactionsList(
        handle: Long,
        chatId: Long,
        messageId: Int,
    ): ReactionPeersDto =
        nativeGetMessageReactionsList(handle.toULong(), chatId, messageId)

    override fun getPollVotes(
        handle: Long,
        chatId: Long,
        messageId: Int,
    ): PollVotersDto =
        nativeGetPollVotes(handle.toULong(), chatId, messageId)

    override fun getOutboxReadDate(
        handle: Long,
        chatId: Long,
        msgId: Int,
    ): OutboxReadDto = nativeGetOutboxReadDate(handle.toULong(), chatId, msgId)

    override fun getReadReceiptConfig(handle: Long): ReadReceiptConfigDto =
        nativeGetReadReceiptConfig(handle.toULong())

    override fun sendTextMessage(
        handle: Long,
        chatId: Long,
        text: String,
        replyToMsgId: Int,
        entitiesJson: String?,
        topMsgId: Int,
        webpageUrl: String?,
        randomId: Long,
    ): MessageDto =
        nativeSendTextMessage(
            handle.toULong(),
            chatId,
            text,
            replyToMsgId,
            entitiesJson,
            topMsgId,
            webpageUrl,
            randomId,
        )

    override fun sendPhotoMessage(
        handle: Long,
        chatId: Long,
        path: String,
        caption: String,
        replyToMsgId: Int,
        topMsgId: Int,
        entitiesJson: String?,
    ): MessageDto =
        nativeSendPhotoMessage(handle.toULong(), chatId, path, caption, replyToMsgId, topMsgId, entitiesJson)

    override fun sendUploadedMedia(
        handle: Long,
        chatId: Long,
        item: UploadItemDto,
        replyToMsgId: Int,
        topMsgId: Int,
        entitiesJson: String?,
    ): MessageDto =
        nativeSendUploadedMedia(
            handle.toULong(),
            chatId,
            item,
            replyToMsgId,
            topMsgId,
            entitiesJson,
        )

    override fun sendUploadedAlbum(
        handle: Long,
        chatId: Long,
        items: List<UploadItemDto>,
        replyToMsgId: Int,
        topMsgId: Int,
    ): List<MessageDto> =
        nativeSendUploadedAlbum(
            handle.toULong(),
            chatId,
            items,
            replyToMsgId,
            topMsgId,
        )

    override fun editTextMessage(
        handle: Long,
        chatId: Long,
        messageId: Int,
        text: String,
        entitiesJson: String?,
    ): MessageDto =
        nativeEditTextMessage(handle.toULong(), chatId, messageId, text, entitiesJson)

    override fun forwardMessages(
        handle: Long,
        fromChatId: Long,
        messageIds: List<Int>,
        toChatId: Long,
        dropAuthor: Boolean,
    ): List<MessageDto> =
        nativeForwardMessages(handle.toULong(), fromChatId, messageIds, toChatId, dropAuthor)

    override fun deleteMessage(
        handle: Long,
        chatId: Long,
        messageId: Int,
        revoke: Boolean,
    ) {
        nativeDeleteMessage(handle.toULong(), chatId, messageId, revoke)
    }

    override fun readHistory(handle: Long, chatId: Long, maxId: Int) {
        nativeReadHistory(handle.toULong(), chatId, maxId)
    }

    override fun readMessageContents(handle: Long, chatId: Long, messageIds: List<Int>) {
        nativeReadMessageContents(handle.toULong(), chatId, messageIds)
    }

    override fun markDialogUnread(handle: Long, chatId: Long, unread: Boolean) {
        nativeMarkDialogUnread(handle.toULong(), chatId, unread)
    }

    override fun getUnreadMentions(
        handle: Long,
        chatId: Long,
        offsetId: Int,
        addOffset: Int,
        limit: Int,
        topMsgId: Int,
    ): List<MessageDto> =
        nativeGetUnreadMentions(handle.toULong(), chatId, offsetId, addOffset, limit, topMsgId)

    override fun readMentions(handle: Long, chatId: Long, topMsgId: Int) {
        nativeReadMentions(handle.toULong(), chatId, topMsgId)
    }

    override fun getUnreadReactions(
        handle: Long,
        chatId: Long,
        offsetId: Int,
        addOffset: Int,
        limit: Int,
        topMsgId: Int,
    ): List<MessageDto> =
        nativeGetUnreadReactions(handle.toULong(), chatId, offsetId, addOffset, limit, topMsgId)

    override fun readReactions(handle: Long, chatId: Long, topMsgId: Int) {
        nativeReadReactions(handle.toULong(), chatId, topMsgId)
    }

    override fun readDiscussion(handle: Long, chatId: Long, msgId: Int, readMaxId: Int) {
        nativeReadDiscussion(handle.toULong(), chatId, msgId, readMaxId)
    }

    override fun setTyping(handle: Long, chatId: Long, typing: Boolean) {
        nativeSetTyping(handle.toULong(), chatId, typing)
    }

    override fun updateStatus(handle: Long, offline: Boolean) {
        nativeUpdateStatus(handle.toULong(), offline)
    }

    override fun registerDevice(
        handle: Long,
        tokenType: Int,
        token: String,
        secret: ByteArray,
        noMuted: Boolean,
        appSandbox: Boolean,
        otherUids: List<Long>,
    ) {
        nativeRegisterDevice(handle.toULong(), tokenType, token, secret, noMuted, appSandbox, otherUids)
    }

    override fun unregisterDevice(
        handle: Long,
        tokenType: Int,
        token: String,
        otherUids: List<Long>,
    ) {
        nativeUnregisterDevice(handle.toULong(), tokenType, token, otherUids)
    }

    override fun getNotifySettings(handle: Long, peerKind: String, chatId: Long): NotifySettingsDto =
        nativeGetNotifySettings(handle.toULong(), peerKind, chatId)

    override fun updateNotifySettings(
        handle: Long,
        peerKind: String,
        chatId: Long,
        showPreviews: Boolean?,
        silent: Boolean,
        muteUntil: Int,
        storiesMuted: Boolean,
        sound: String,
    ) {
        nativeUpdateNotifySettings(
            handle.toULong(),
            peerKind,
            chatId,
            showPreviews,
            silent,
            muteUntil,
            storiesMuted,
            sound,
        )
    }

    override fun resetNotifySettings(handle: Long) {
        nativeResetNotifySettings(handle.toULong())
    }

    override fun joinChat(handle: Long, chatId: Long): NativeJoinResult {
        val joined = nativeJoinChat(handle.toULong(), chatId)
        return NativeJoinResult(
            joined = joined.joined,
            canSendPlain = joined.canSendPlain,
            canSendPhotos = joined.canSendPhotos,
        )
    }

    override fun unblockUser(handle: Long, chatId: Long) {
        nativeUnblockUser(handle.toULong(), chatId)
    }

    override fun setContactJoinedSilent(handle: Long, silent: Boolean) {
        nativeSetContactJoinedSilent(handle.toULong(), silent)
    }

    override fun getNotifyExceptions(handle: Long, compareSound: Boolean): List<NotifyExceptionDto> =
        nativeGetNotifyExceptions(handle.toULong(), compareSound)

    override fun decryptPushPayload(secret: ByteArray, payload: String): String =
        nativeDecryptPushPayload(secret, payload)

    override fun getProfile(handle: Long, peerId: Long): ProfileDto =
        nativeGetProfile(handle.toULong(), peerId)

    override fun getGroupAdminTags(handle: Long, chatId: Long): String =
        nativeGetGroupAdminTags(handle.toULong(), chatId)

    override fun getSearchCounters(handle: Long, chatId: Long, filters: List<String>): String =
        nativeGetSearchCounters(handle.toULong(), chatId, filters)

    override fun getParticipants(
        handle: Long,
        chatId: Long,
        filter: String,
        query: String,
        offset: Int,
        limit: Int,
    ): String =
        nativeGetParticipants(handle.toULong(), chatId, filter, query, offset, limit)

    override fun getCommonChats(handle: Long, userId: Long, maxId: Long, limit: Int): String =
        nativeGetCommonChats(handle.toULong(), userId, maxId, limit)

    override fun startUpdates(handle: Long) {
        nativeStartUpdates(handle.toULong())
    }

    override fun updateLazySyncConfig(handle: Long, lazy: Boolean, exceptions: List<Long>) {
        nativeUpdateLazySyncConfig(handle.toULong(), lazy, exceptions)
    }

    override fun clearActiveDialog(handle: Long) {
        nativeClearActiveDialog(handle.toULong())
    }

    override fun drainUpdates(handle: Long): List<UpdateEventDto> =
        nativeDrainUpdates(handle.toULong()).let { it.batches.flatMap { batch -> batch.events } + it.liveEvents }

    override fun drainUpdateBatches(handle: Long): UpdateDrainDto =
        nativeDrainUpdates(handle.toULong())

    override fun pendingUpdateBatches(handle: Long): List<UpdateBatchDto> =
        nativePendingUpdateBatches(handle.toULong())

    override fun acknowledgeUpdateBatch(handle: Long, accountGeneration: Long, batchId: Long) {
        nativeAcknowledgeUpdateBatch(handle.toULong(), accountGeneration, batchId)
    }

    override fun downloadMessageMedia(
        handle: Long,
        chatId: Long,
        messageId: Int,
        destPath: String,
    ): String = nativeDownloadMessageMedia(handle.toULong(), chatId, messageId, destPath)

    override fun downloadMessageThumb(
        handle: Long,
        chatId: Long,
        messageId: Int,
        destPath: String,
    ): String = nativeDownloadMessageThumb(handle.toULong(), chatId, messageId, destPath)

    override fun peekMessageInlineThumb(
        handle: Long,
        chatId: Long,
        messageId: Int,
    ): ByteArray? = nativePeekMessageInlineThumb(handle.toULong(), chatId, messageId)

    override fun peekPeerInlineThumb(handle: Long, peerId: Long): ByteArray? =
        nativePeekPeerInlineThumb(handle.toULong(), peerId)

    override fun downloadMessageDisplay(
        handle: Long,
        chatId: Long,
        messageId: Int,
        destPath: String,
    ): String = nativeDownloadMessageMedia(handle.toULong(), chatId, messageId, destPath)

    override fun downloadCustomEmoji(
        handle: Long,
        documentId: Long,
        destPath: String,
    ): String = nativeDownloadCustomEmoji(handle.toULong(), documentId, destPath)

    override fun getStickerPack(
        handle: Long,
        documentId: Long,
    ): StickerPackDto = nativeGetStickerPack(handle.toULong(), documentId)

    override fun getStickerSet(
        handle: Long,
        setId: Long,
        accessHash: Long,
    ): StickerPackDto = nativeGetStickerSet(handle.toULong(), setId, accessHash)

    override fun getAllStickers(handle: Long, hash: Long): StickerCatalogDto =
        nativeGetAllStickers(handle.toULong(), hash)

    override fun getEmojiStickers(handle: Long, hash: Long): StickerCatalogDto =
        nativeGetEmojiStickers(handle.toULong(), hash)

    override fun getStickers(
        handle: Long,
        emoticon: String,
        hash: Long,
    ): StickerListDto = nativeGetStickers(handle.toULong(), emoticon, hash)

    override fun resolveUsername(handle: Long, username: String): ResolvedPeerDto =
        nativeResolveUsername(handle.toULong(), username)

    override fun getInlineBotResults(
        handle: Long,
        chatId: Long,
        botId: Long,
        query: String,
        offset: String,
    ): InlineBotResultsDto =
        nativeGetInlineBotResults(handle.toULong(), chatId, botId, query, offset)

    override fun sendInlineBotResult(
        handle: Long,
        chatId: Long,
        queryId: Long,
        resultId: String,
        replyToMsgId: Int,
        topMsgId: Int,
    ): MessageDto = nativeSendInlineBotResult(
        handle.toULong(),
        chatId,
        queryId,
        resultId,
        replyToMsgId,
        topMsgId,
    )

    override fun getSavedGifs(handle: Long): List<SavedGifDto> =
        nativeGetSavedGifs(handle.toULong())

    override fun getBotCallbackAnswer(
        handle: Long,
        chatId: Long,
        messageId: Int,
        dataHex: String,
    ): BotCallbackAnswerDto =
        nativeGetBotCallbackAnswer(handle.toULong(), chatId, messageId, dataHex)

    override fun toggleTodoCompleted(
        handle: Long,
        chatId: Long,
        messageId: Int,
        completed: List<Int>,
        incompleted: List<Int>,
    ) {
        nativeToggleTodoCompleted(handle.toULong(), chatId, messageId, completed, incompleted)
    }

    override fun sendLocation(
        handle: Long,
        chatId: Long,
        latitude: Double,
        longitude: Double,
        livePeriodSeconds: Int,
        heading: Int,
        replyToMsgId: Int,
    ) {
        nativeSendLocation(
            handle.toULong(),
            chatId,
            latitude,
            longitude,
            livePeriodSeconds,
            heading,
            replyToMsgId,
        )
    }

    override fun sendPollVote(
        handle: Long,
        chatId: Long,
        messageId: Int,
        options: List<ByteArray>,
    ) {
        nativeSendPollVote(handle.toULong(), chatId, messageId, options)
    }

    override fun appendTodoItems(
        handle: Long,
        chatId: Long,
        messageId: Int,
        firstId: Int,
        titles: List<String>,
    ) {
        nativeAppendTodoItems(handle.toULong(), chatId, messageId, firstId, titles)
    }

    override fun sendSavedGif(
        handle: Long,
        chatId: Long,
        documentId: Long,
        replyToMsgId: Int,
        topMsgId: Int,
    ): MessageDto = nativeSendSavedGif(handle.toULong(), chatId, documentId, replyToMsgId, topMsgId)

    override fun sendReaction(
        handle: Long,
        chatId: Long,
        messageId: Int,
        emoticon: String,
        documentId: Long,
    ) {
        nativeSendReaction(handle.toULong(), chatId, messageId, emoticon, documentId)
    }

    override fun getDiscussionMessage(
        handle: Long,
        chatId: Long,
        messageId: Int,
    ): DiscussionDto = nativeGetDiscussionMessage(handle.toULong(), chatId, messageId)

    override fun animatedEmojiMax(handle: Long): Int =
        nativeAnimatedEmojiMax(handle.toULong())

    override fun customEmojiIsFree(handle: Long, documentId: Long): Boolean =
        nativeCustomEmojiIsFree(handle.toULong(), documentId)

    override fun getRecentReactions(handle: Long): List<ReactionChoiceDto> =
        nativeGetRecentReactions(handle.toULong())

    override fun getUpdatesState(handle: Long): UpdatesStateDto =
        nativeGetUpdatesState(handle.toULong())
}
