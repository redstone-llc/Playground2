package org.everbuild.asorda.resources.data.font

import org.everbuild.asorda.resources.data.api.ContentList
import org.everbuild.asorda.resources.data.api.texture.Texture

object MenuCharacters : ContentList() {
    val functionSearchMenu = createBitmapCharacter("function_search_menu") {
        texture(Texture("font/menu/function_search_menu"))
        height(256)
        ascent(13)
    }

    val actionsSearchMenu = createBitmapCharacter("actions_search_menu") {
        texture(Texture("font/menu/actions_search_menu"))
        height(256)
        ascent(13)
    }
}