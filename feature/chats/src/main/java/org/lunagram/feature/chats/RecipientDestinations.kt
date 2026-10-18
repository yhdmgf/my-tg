package org.lunagram.feature.chats

import org.lunagram.core.models.Chat

internal fun recipientPaneIds(
    paneIds: List<Long>,
    chat: (Long) -> Chat?,
    selectingRecipient: Boolean,
    canSelectRecipient: (Chat) -> Boolean,
): List<Long> {
    if (!selectingRecipient) return paneIds
    return paneIds.filter { id ->
        val row = chat(id) ?: return@filter false
        canSelectRecipient(row)
    }
}
