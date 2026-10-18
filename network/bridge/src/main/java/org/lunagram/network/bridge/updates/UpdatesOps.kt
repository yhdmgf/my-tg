package org.lunagram.network.bridge.updates

import kotlinx.coroutines.flow.Flow
import org.lunagram.core.common.Outcome
import org.lunagram.network.bridge.DurableUpdateSink
import org.lunagram.network.bridge.MtprotoUpdate
import org.lunagram.network.bridge.UpdatesCursor

interface UpdatesOps {
    suspend fun getUpdatesState(): Outcome<UpdatesCursor>
    fun updates(): Flow<MtprotoUpdate>
    fun sessionLost(): Flow<Unit>

    /**
     * Registers the single consumer that commits durable batches. Until one is
     * installed, saved batches stay queued and are replayed on installation.
     */
    fun installDurableUpdateSink(sink: DurableUpdateSink) = Unit
}
