package llc.redstone.playground.menu.menus

import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.Material
import llc.redstone.playground.menu.Menu
import llc.redstone.playground.menu.MenuItem
import llc.redstone.playground.menu.PaginationMenu
import llc.redstone.playground.menu.menuItem
import llc.redstone.playground.utils.PaginationList

class MaterialSelector(backMenu: Menu, val invoke: (Material) -> Unit): PaginationMenu(
    "Material Selector",
    InventoryType.CHEST_6_ROW
) {
    override fun paginationList(player: Player): PaginationList<MenuItem>? {
        return PaginationList(
            Material.values().map { material ->
                menuItem(material) {
                    invoke(material)
                    player.sendMessage("Selected material: ${material.name()}")
                }.name("<green>${material.name()}")
                    .action(ClickType.LEFT_CLICK, "to select")
            },
            slots.size
        )
    }

}