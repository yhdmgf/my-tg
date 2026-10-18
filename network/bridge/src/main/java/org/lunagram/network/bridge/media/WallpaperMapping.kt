package org.lunagram.network.bridge.media

import org.lunagram.core.models.Wallpaper
import org.lunagram.core.models.WallpaperCatalog
import uniffi.monogram_mtproto.WallpaperCatalogDto

internal fun WallpaperCatalogDto.toModel() = WallpaperCatalog(
    hash = hash,
    notModified = notModified,
    wallpapers = wallpapers.map {
        Wallpaper(
            it.id, it.accessHash, it.slug, it.pattern, it.dark, it.mimeType, it.documentId,
            it.colors, it.intensity, it.rotation, it.blur, it.motion
        )
    },
)
