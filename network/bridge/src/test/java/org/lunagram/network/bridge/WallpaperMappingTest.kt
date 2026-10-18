package org.lunagram.network.bridge

import org.junit.Assert.*
import org.junit.Test
import uniffi.lunagram_mtproto.WallpaperCatalogDto
import uniffi.lunagram_mtproto.WallpaperDto
import org.lunagram.network.bridge.media.toModel

class WallpaperMappingTest {
    @Test fun preservesPatternSettingsAndBlackColor() {
        val dto = WallpaperDto(1, 2, "pattern", true, true, "application/x-tgwallpattern", 3,
            listOf(0, 0xffffff, 0x123456), -60, 135, true, true)
        val result = WallpaperCatalogDto(77, false, listOf(dto)).toModel()
        assertEquals(77L, result.hash)
        assertFalse(result.notModified)
        val wallpaper = result.wallpapers.single()
        assertEquals(dto.colors, wallpaper.colors)
        assertEquals(-60, wallpaper.intensity)
        assertEquals(135, wallpaper.rotation)
        assertTrue(wallpaper.pattern && wallpaper.dark && wallpaper.blur && wallpaper.motion)
        assertEquals(3L, wallpaper.documentId)
    }

    @Test fun notModifiedIsNotAnEmptyCatalogReplacement() {
        val result = WallpaperCatalogDto(77, true, emptyList()).toModel()
        assertTrue(result.notModified)
        assertEquals(77L, result.hash)
    }
}
