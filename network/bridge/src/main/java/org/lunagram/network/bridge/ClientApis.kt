package org.lunagram.network.bridge

import org.lunagram.network.bridge.chat.ChatApi
import org.lunagram.network.bridge.media.MediaApi
import org.lunagram.network.bridge.message.MessageApi
import org.lunagram.network.bridge.notify.NotifyApi
import org.lunagram.network.bridge.profile.ProfileApi
import org.lunagram.network.bridge.session.SessionCore
import org.lunagram.network.bridge.updates.UpdatesPump
import org.lunagram.network.bridge.web.WebApi

internal class ClientApis(core: SessionCore) {
    internal val core: SessionCore = core
    internal val profile = ProfileApi(core)
    internal val chat = ChatApi(core)
    internal val message = MessageApi(core, profile)
    internal val media = MediaApi(core)
    internal val web = WebApi(core)
    internal val notify = NotifyApi(core)
    internal val updates = UpdatesPump(core)
}