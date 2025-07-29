package org.everbuild.asorda.resources.data

import org.everbuild.asorda.resources.data.api.addContent
import org.everbuild.asorda.resources.data.items.GlobalIcons
import team.unnamed.creative.ResourcePack

suspend fun addItems(pack: ResourcePack) {
    addContent(pack, GlobalIcons)

//    pack.unknownFile("assets/minecraft/models/item/spyglass_in_hand.json", resource("jam/spyglass.json"))
//    pack.unknownFile("assets/minecraft/textures/item/spyglass_in_hand.png", resource("jam/spyglass.png"))
}