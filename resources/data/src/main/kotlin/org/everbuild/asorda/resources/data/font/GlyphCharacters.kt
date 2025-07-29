package org.everbuild.asorda.resources.data.font

import org.everbuild.asorda.resources.data.api.ContentList
import org.everbuild.asorda.resources.data.api.texture.Texture

object GlyphCharacters : ContentList() {
    val empty = createBitmapCharacter("empty") {
        texture(Texture("font/utils/empty"))
        height(20)
        ascent(0)
    }

    val return_glyph = createBitmapCharacter("return") {
        texture(Texture("font/glyphs/return"))
        height(8)
        ascent(7)
    }
}