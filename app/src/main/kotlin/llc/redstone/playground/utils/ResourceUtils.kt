package llc.redstone.playground.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import org.everbuild.asorda.resources.data.api.item.ItemResource
import org.everbuild.asorda.resources.data.api.model.ModelResource
import org.everbuild.celestia.orion.core.packs.OrionPacks

fun String.component(spacing: Int = 0): Component {
    return OrionPacks.getSpaceComponent(spacing).style {
        it.color(NamedTextColor.WHITE)
    }.append { colorize(OrionPacks.getCharacterCodepoint(this)) }
}

fun spacingComp(spacing: Int): Component {
    return OrionPacks.getSpaceComponent(spacing)
}

fun ModelResource.item(): ItemStack {
    return ItemStack.of(Material.PAPER)
        .with {
            it.itemModel(this.key.toString())
        }
}

fun ItemResource.item(): ItemStack {
    return ItemStack.of(Material.PAPER)
        .with {
            it.itemModel(this.model.toString())
        }
}