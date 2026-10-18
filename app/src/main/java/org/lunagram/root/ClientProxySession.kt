package org.lunagram.root

import org.lunagram.core.common.Outcome
import org.lunagram.feature.settings.ui.ProxySession
import org.lunagram.network.bridge.MtprotoClient
import org.lunagram.network.bridge.MtprotoTransportMode
import org.lunagram.network.bridge.ProxyConfig

internal class ClientProxySession(private val client: MtprotoClient) : ProxySession {
    override fun configureProxy(config: ProxyConfig): Outcome<Unit> = client.configureProxy(config)
    override fun clearProxy(): Outcome<Unit> = client.clearProxy()
    override fun setTransportMode(mode: MtprotoTransportMode): Outcome<Unit> =
        client.setTransportMode(mode)

    override suspend fun testProxyConnection(): Outcome<Unit> = client.connect()
    override suspend fun pingProxy(config: ProxyConfig): Outcome<Long> = client.pingProxy(config)
}
