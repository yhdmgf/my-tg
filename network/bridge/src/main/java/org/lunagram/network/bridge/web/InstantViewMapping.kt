package org.lunagram.network.bridge.web

import org.lunagram.core.models.InstantViewDtoLike
import org.lunagram.core.models.InstantViewPage
import org.lunagram.core.models.InstantViewPages
import uniffi.lunagram_mtproto.InstantViewDto

internal fun InstantViewDto.toModel(): InstantViewPage = InstantViewPages.parse(
    InstantViewDtoLike(
        url = url,
        displayUrl = displayUrl,
        title = title,
        siteName = siteName,
        description = description,
        webpageType = webpageType,
        hash = hash,
        hasInstantView = hasInstantView,
        part = part,
        rtl = rtl,
        v2 = v2,
        notModified = notModified,
        blocksJson = blocksJson,
    ),
)
