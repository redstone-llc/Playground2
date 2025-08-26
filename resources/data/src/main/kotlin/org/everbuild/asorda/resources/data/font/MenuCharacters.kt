package org.everbuild.asorda.resources.data.font

import org.everbuild.asorda.resources.data.api.ContentList
import org.everbuild.asorda.resources.data.api.texture.Texture

object MenuCharacters : ContentList() {
    val playgroundMenu = createBitmapCharacter("playground_menu") {
        texture(Texture("font/menu/playground_menu"))
        height(256)
        ascent(48)
    }
    val functionSearchMenu = createBitmapCharacter("function_search_menu") {
        texture(Texture("font/menu/function_search_menu"))
        height(256)
        ascent(13)
    }

    val actionsBase = createBitmapCharacter("actions_base") {
        texture(Texture("font/menu/actions_base"))
        height(256)
        ascent(16)
    }

    val actionLibrary = createBitmapCharacter("action_library") {
        texture(Texture("font/menu/action_library"))
        height(256)
        ascent(16)
    }

    val functionEditorActions = createBitmapCharacter("function_editor_actions") {
        texture(Texture("font/menu/function_editor_actions"))
        height(256)
        ascent(16)
    }

    val functionEditorActionsSettings = createBitmapCharacter("function_editor_actions_settings") {
        texture(Texture("font/menu/function_editor_actions_settings"))
        height(256)
        ascent(16)
    }

    val functionEditorSettings = createBitmapCharacter("function_editor_settings") {
        texture(Texture("font/menu/function_editor_settings"))
        height(256)
        ascent(16)
    }

    val functionEditorArguments = createBitmapCharacter("function_editor_arguments") {
        texture(Texture("font/menu/function_editor_arguments"))
        height(256)
        ascent(16)
    }

    val commandEditorActions = createBitmapCharacter("command_editor_actions") {
        texture(Texture("font/menu/command_editor_actions"))
        height(256)
        ascent(16)
    }

    val commandArgumentProperties = createBitmapCharacter("command_argument_properties") {
        texture(Texture("font/menu/command_argument_properties"))
        height(256)
        ascent(16)
    }

    val commandEditorActionsSettings = createBitmapCharacter("command_editor_actions_settings") {
        texture(Texture("font/menu/command_editor_actions_settings"))
        height(256)
        ascent(16)
    }

    val commandEditorSettings = createBitmapCharacter("command_editor_settings") {
        texture(Texture("font/menu/command_editor_settings"))
        height(256)
        ascent(16)
    }

    val commandEditorArguments = createBitmapCharacter("command_editor_arguments") {
        texture(Texture("font/menu/command_editor_arguments"))
        height(256)
        ascent(16)
    }
    
    val scheduleEditorActions = createBitmapCharacter("schedule_editor_actions") {
        texture(Texture("font/menu/schedule_editor_actions"))
        height(256)
        ascent(16)
    }

    val scheduleEditorSettings = createBitmapCharacter("schedule_editor_settings") {
        texture(Texture("font/menu/schedule_editor_settings"))
        height(256)
        ascent(16)
    }

    val scheduleEditorActionsSettings = createBitmapCharacter("schedule_editor_actions_settings") {
        texture(Texture("font/menu/schedule_editor_actions_settings"))
        height(256)
        ascent(16)
    }
}