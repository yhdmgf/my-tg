package org.lunagram.network.bridge.notify

import org.lunagram.core.models.NotifyException
import org.lunagram.core.models.NotifySettings
import org.lunagram.core.models.PeerId
import uniffi.lunagram_mtproto.NotifyExceptionDto
import uniffi.lunagram_mtproto.NotifySettingsDto

internal fun NotifySettingsDto.toModel(): NotifySettings = NotifySettings(
    showPreviews = showPreviews ?: true,
    previewInherited = showPreviews == null,
    silent = silent,
    muteUntil = muteUntil,
    storiesMuted = storiesMuted,
    storiesHideSender = storiesHideSender,
    sound = sound.ifBlank { "default" },
)

internal fun NotifyExceptionDto.toModel(): NotifyException = NotifyException(
    peerKind = peerKind,
    chatId = PeerId(chatId),
    topicId = topicId,
    settings = NotifySettings(
        showPreviews = showPreviews ?: true,
        previewInherited = showPreviews == null,
        silent = silent,
        muteUntil = muteUntil,
        storiesMuted = storiesMuted,
        storiesHideSender = storiesHideSender,
        sound = sound.ifBlank { "default" },
    ),
)
