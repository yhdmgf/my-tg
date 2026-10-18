package org.lunagram.network.bridge.chat

import org.lunagram.core.models.Chat
import org.lunagram.core.models.Folder
import org.lunagram.core.models.ForumTopic
import org.lunagram.core.models.ForumTopicsPage
import org.lunagram.core.models.PeerId
import org.lunagram.core.models.isForcedVerifiedChat
import uniffi.lunagram_mtproto.ChatDto
import uniffi.lunagram_mtproto.FolderDto
import uniffi.lunagram_mtproto.ForumTopicDto
import uniffi.lunagram_mtproto.ForumTopicsPageDto

internal fun List<ChatDto>.toChatModels(assignPinOrder: Boolean = true): List<Chat> {
    var pin = 0
    return map { dto ->
        dto.toModel(pinnedOrder = if (assignPinOrder && dto.pinned) pin++ else Int.MAX_VALUE)
    }
}

internal fun ChatDto.toModel(pinnedOrder: Int = Int.MAX_VALUE): Chat = Chat(
    id = PeerId(id),
    title = title,
    isChannel = isChannel,
    isGroup = isGroup,
    isForum = isForum,
    left = left,
    unreadCount = unreadCount,
    lastMessagePreview = lastMessagePreview,
    lastMessageOutgoing = lastMessageOutgoing,
    pinnedOrder = pinnedOrder,
    lastMessageDate = lastMessageDate,
    photoCacheKey = photoCacheKey,
    lastMediaThumbCacheKey = lastMediaThumbCacheKey,
    lastMessageId = lastMessageId,
    canView = canView,
    canSendPlain = canSendPlain,
    canSendPhotos = canSendPhotos,
    canForward = canForward,
    canDeleteOthers = canDeleteOthers,
    canManageTopics = canManageTopics,
    archived = archived,
    muted = muted,
    muteOverride = muteOverride,
    unreadMark = unreadMark,
    unreadMentionsCount = unreadMentionsCount,
    unreadReactionsCount = unreadReactionsCount,
    isContact = isContact,
    isBot = isBot,
    isVerified = isVerified || isForcedVerifiedChat(id),
    pinned = pinned,
    readInboxMaxId = readInboxMaxId,
    readOutboxMaxId = readOutboxMaxId,
    peerStatus = peerStatus,
    peerStatusAt = peerStatusAt,
    emojiStatusDocumentId = emojiStatusDocumentId,
)

internal fun FolderDto.toModel(): Folder = Folder(
    id = id,
    title = title,
    emoticon = emoticon,
    chatIds = chatIds.map(::PeerId),
    pinnedChatIds = pinnedChatIds.map(::PeerId),
    excludeChatIds = excludeChatIds.map(::PeerId),
    includeContacts = includeContacts,
    includeNonContacts = includeNonContacts,
    includeGroups = includeGroups,
    includeChannels = includeChannels,
    includeBots = includeBots,
    excludeMuted = excludeMuted,
    excludeRead = excludeRead,
    excludeArchived = excludeArchived,
)

internal fun Folder.toDto(): FolderDto = FolderDto(
    id = id,
    title = title,
    chatIds = chatIds.map { it.value },
    excludeChatIds = excludeChatIds.map { it.value },
    includeContacts = includeContacts,
    includeNonContacts = includeNonContacts,
    includeGroups = includeGroups,
    includeChannels = includeChannels,
    includeBots = includeBots,
    excludeMuted = excludeMuted,
    excludeRead = excludeRead,
    excludeArchived = excludeArchived,
    emoticon = emoticon,
    pinnedChatIds = pinnedChatIds.map { it.value },
)

internal fun ForumTopicDto.toModel(): ForumTopic = ForumTopic(
    id = id,
    title = title,
    iconColor = iconColor,
    iconEmojiId = iconEmojiId,
    topMessageId = topMessage,
    date = date,
    unreadCount = unreadCount,
    unreadMentionsCount = unreadMentionsCount,
    readInboxMaxId = readInboxMaxId,
    pinned = pinned,
    closed = closed,
    hidden = hidden,
    short = short,
    deleted = deleted,
    lastMessagePreview = lastMessagePreview,
)

internal fun ForumTopicsPageDto.toModel(): ForumTopicsPage = ForumTopicsPage(
    count = count,
    topics = topics.map { it.toModel() }.filterNot { it.deleted },
)
