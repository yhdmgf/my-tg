package org.lunagram.network.bridge.web

import org.lunagram.core.common.AppLog
import org.lunagram.core.common.Outcome
import org.lunagram.core.models.InstantViewPage
import org.lunagram.network.bridge.session.SessionCore

internal class WebApi(private val core: SessionCore) : WebOps {
    override suspend fun getWebPage(url: String, hash: Int): Outcome<InstantViewPage> {
        when (val connected = core.ensureConnected()) {
            is Outcome.Err -> return connected
            is Outcome.Ok -> Unit
        }
        AppLog.api("instant view", "start hash=$hash")
        return core.rpc("instant view failed") { handle ->
            core.native.getWebPage(handle, url, hash).toModel().also {
                AppLog.api("instant view", "ok hash=$hash")
            }
        }
    }

    override suspend fun getWebPagePreview(message: String): Outcome<InstantViewPage> {
        when (val connected = core.ensureConnected()) {
            is Outcome.Err -> return connected
            is Outcome.Ok -> Unit
        }
        AppLog.api("link preview", "start")
        return core.rpcBackground("link preview failed") { handle ->
            core.native.getWebPagePreview(handle, message).toModel().also {
                AppLog.api("link preview", "ok")
            }
        }
    }
}
