package org.lunagram.network.bridge.web

import org.lunagram.core.common.Outcome
import org.lunagram.core.models.InstantViewPage

interface WebOps {
    suspend fun getWebPage(url: String, hash: Int = 0): Outcome<InstantViewPage> =
        Outcome.Err("unsupported")

    suspend fun getWebPagePreview(message: String): Outcome<InstantViewPage> =
        getWebPage(message, 0)
}
