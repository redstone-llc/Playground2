package org.everbuild.asorda.resources.data.items

import org.everbuild.asorda.resources.data.api.ContentList
import org.everbuild.asorda.resources.data.api.texture.Texture
import team.unnamed.creative.model.Model

object GlobalIcons : ContentList("global") {
    val inventoryPart = globalIcon("inventory_part")
    val empty = globalIcon("empty")

    private fun globalIcon(name: String) =
        createItem(name) {
            model(createModel {
                parent(Model.ITEM_GENERATED)
                textures("layer0" to Texture("lixel/$name"))
            })
        }
}