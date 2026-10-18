package org.lunagram.mtproto

import uniffi.lunagram_mtproto.MtprotoException
import uniffi.lunagram_mtproto.VpxFrame
import uniffi.lunagram_mtproto.createVpxDecoder
import uniffi.lunagram_mtproto.decodeVpxAlphaPacket
import uniffi.lunagram_mtproto.decodeVpxPacket
import uniffi.lunagram_mtproto.destroyVpxDecoder
import uniffi.lunagram_mtproto.uniffiEnsureInitialized

/** Slim libvpx VP9 decode for video stickers. */
object VpxNative {
    init {
        runCatching {
            MtprotoNativeLoader.loadOrStub()
            uniffiEnsureInitialized()
        }
    }

    @Throws(MtprotoException::class)
    fun create(): Long = createVpxDecoder().toLong()

    fun destroy(handle: Long) {
        runCatching { destroyVpxDecoder(handle.toULong()) }
    }

    @Throws(MtprotoException::class)
    fun decode(handle: Long, packet: ByteArray): VpxFrame? =
        decodeVpxPacket(handle.toULong(), packet)

    @Throws(MtprotoException::class)
    fun decodeAlpha(handle: Long, packet: ByteArray) =
        decodeVpxAlphaPacket(handle.toULong(), packet)
}
