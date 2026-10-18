package org.lunagram.network.bridge

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.lunagram.core.common.Outcome
import org.lunagram.core.common.TelegramCredentials
import org.lunagram.mtproto.MtprotoNative
import org.lunagram.mtproto.MtprotoNativeLoader
import org.lunagram.network.bridge.chat.ChatOps
import org.lunagram.network.bridge.media.MediaOps
import org.lunagram.network.bridge.message.MessageOps
import org.lunagram.network.bridge.notify.NotifyOps
import org.lunagram.network.bridge.profile.ProfileOps
import org.lunagram.network.bridge.session.DcTxtBootstrap
import org.lunagram.network.bridge.session.MonotonicClock
import org.lunagram.network.bridge.session.SessionCore
import org.lunagram.network.bridge.session.SessionOps
import org.lunagram.network.bridge.updates.UpdatesOps
import org.lunagram.network.bridge.web.WebOps

class BridgedMtprotoClient internal constructor(
    private val apis: ClientApis,
) : MtprotoClient,
    SessionOps by apis.core,
    ChatOps by apis.chat,
    MessageOps by apis.message,
    ProfileOps by apis.profile,
    MediaOps by apis.media,
    WebOps by apis.web,
    NotifyOps by apis.notify,
    UpdatesOps by apis.updates {
    constructor(
        credentials: TelegramCredentials,
        sessionPath: String,
        native: MtprotoNative = MtprotoNativeLoader.loadOrStub(),
        historyTimeoutMs: Long = 20_000,
        nativeDispatcher: CoroutineDispatcher = Dispatchers.IO,
        refreshDcSidecar: (String) -> Unit = DcTxtBootstrap::refreshSidecar,
        initConnection: ClientInitInfo? = null,
    ) : this(
        ClientApis(
            SessionCore(
                credentials,
                sessionPath,
                native,
                historyTimeoutMs,
                nativeDispatcher,
                refreshDcSidecar,
                initConnection = initConnection,
            )
        ),
    )

    internal constructor(
        credentials: TelegramCredentials,
        sessionPath: String,
        native: MtprotoNative,
        nativeDispatcher: CoroutineDispatcher,
        refreshDcSidecar: (String) -> Unit,
        clock: MonotonicClock,
        historyTimeoutMs: Long = 20_000,
        initConnection: ClientInitInfo? = null,
    ) : this(
        ClientApis(
            SessionCore(
                credentials,
                sessionPath,
                native,
                historyTimeoutMs,
                nativeDispatcher,
                refreshDcSidecar,
                clock,
                initConnection,
            )
        ),
    )

    override suspend fun isLocallyAuthorized(): Outcome<Boolean> = apis.core.isLocallyAuthorized()

    override suspend fun isAuthorized(): Outcome<Boolean> = apis.core.isAuthorized()
}