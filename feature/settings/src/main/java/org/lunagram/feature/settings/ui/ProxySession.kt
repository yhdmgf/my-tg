package org.lunagram.feature.settings.ui

import org.lunagram.core.common.Outcome
import org.lunagram.network.bridge.MtprotoTransportMode
import org.lunagram.network.bridge.ProxyConfig

interface ProxySession {
    fun configureProxy(config: ProxyConfig): Outcome<Unit>
    fun clearProxy(): Outcome<Unit>
    fun setTransportMode(mode: MtprotoTransportMode): Outcome<Unit>
    suspend fun testProxyConnection(): Outcome<Unit>
    suspend fun pingProxy(config: ProxyConfig): Outcome<Long>
}
