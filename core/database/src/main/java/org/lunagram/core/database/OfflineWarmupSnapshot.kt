package org.lunagram.core.database

import org.lunagram.core.models.Chat
import org.lunagram.core.models.Folder
import org.lunagram.core.models.Profile

data class OfflineWarmupSnapshot(
    val chats: List<Chat>,
    val folders: List<Folder>,
    val self: Profile?,
    val mainCount: Int,
)

data class ChatCacheCursor(
    val archived: Boolean,
    val pinned: Boolean,
    val pinnedOrder: Int,
    val lastMessageDate: Long?,
    val id: Long,
) {
    companion object {
        fun from(chat: Chat) = ChatCacheCursor(
            chat.archived, chat.pinned, chat.pinnedOrder, chat.lastMessageDate, chat.id.value,
        )
    }
}
