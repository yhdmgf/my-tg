package org.lunagram.network.bridge.notify

import org.lunagram.core.common.Outcome
import org.lunagram.core.models.NotifyException
import org.lunagram.core.models.NotifySettings
import org.lunagram.core.models.PeerId
import org.lunagram.core.models.PushTokenType

interface NotifyOps {
    suspend fun registerDevice(
        tokenType: PushTokenType,
        token: String,
        secret: ByteArray,
        noMuted: Boolean = true,
        appSandbox: Boolean = false,
        otherUids: List<Long> = emptyList(),
    ): Outcome<Unit> = Outcome.Err("unsupported")

    suspend fun unregisterDevice(
        tokenType: PushTokenType,
        token: String,
        otherUids: List<Long> = emptyList(),
    ): Outcome<Unit> = Outcome.Err("unsupported")

    suspend fun getNotifySettings(
        peerKind: String,
        chatId: PeerId = PeerId(0)
    ): Outcome<NotifySettings> =
        Outcome.Err("unsupported")

    suspend fun updateNotifySettings(
        peerKind: String,
        settings: NotifySettings,
        chatId: PeerId = PeerId(0),
    ): Outcome<Unit> = Outcome.Err("unsupported")

    suspend fun resetNotifySettings(): Outcome<Unit> = Outcome.Err("unsupported")
    suspend fun setContactJoinedSilent(silent: Boolean): Outcome<Unit> = Outcome.Err("unsupported")
    suspend fun getNotifyExceptions(compareSound: Boolean = false): Outcome<List<NotifyException>> =
        Outcome.Err("unsupported")

    fun decryptPushPayload(secret: ByteArray, payload: String): Outcome<String> =
        Outcome.Err("unsupported")
}
