package llc.redstone.playground.menu.items

import net.minestom.server.item.ItemStack
import llc.redstone.playground.menu.PItem
import llc.redstone.playground.menu.invui.AbstractMenu
import llc.redstone.playground.utils.item
import llc.redstone.playground.utils.serialize
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.inventory.click.Click
import org.everbuild.asorda.resources.data.items.GlobalIcons
import xyz.xenondevs.invui.item.impl.SimpleItem

class BackItem(
    val backMenu: AbstractMenu,
    val menuName: String = serialize(backMenu.displayName), // Would be smarter to always set this
    val item: ItemStack = PItem(GlobalIcons.empty.item()).name("<red>Go Back").description("To $menuName").build()
) : SimpleItem(item) {
    override fun handleClick(
        clickType: Click,
        player: Player,
        event: InventoryPreClickEvent
    ) {
        backMenu.open(player)
    }

}