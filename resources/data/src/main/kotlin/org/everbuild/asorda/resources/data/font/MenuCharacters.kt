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

    val functionEditorInfo = createBitmapCharacter("function_editor_info") {
        texture(Texture("font/menu/function_editor_info"))
        height(256)
        ascent(13)
    }

    val functionEditorSettings = createBitmapCharacter("function_editor_settings") {
        texture(Texture("font/menu/function_editor_settings"))
        height(256)
        ascent(13)
    }

//    val functionEditorArguments = createBitmapCharacter("function_editor_args") {
//        texture(Texture("font/menu/function_editor_args"))
//        height(256)
//        ascent(13)
//    }
}