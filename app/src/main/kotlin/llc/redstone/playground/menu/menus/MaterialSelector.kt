package llc.redstone.playground.menu.menus

import net.minestom.server.entity.Player
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.Material
import llc.redstone.playground.menu.PItem
import llc.redstone.playground.menu.PaginationMenu
import llc.redstone.playground.menu.invui.NormalMenu
import llc.redstone.playground.utils.PaginationList

class MaterialSelector(backMenu: NormalMenu, val invoke: (Material) -> Unit) : PaginationMenu(
    "Material Selector",
    backMenu
) {
    override fun list(player: Player): MutableList<PItem> {
        return Material.values().map { material ->
                PItem(material).name("<green>${material.name()}")
                    .leftClick("select") { e ->
                        invoke(material)
                        player.sendMessage("Selected material: ${material.name()}")
                    }
            }.toMutableList()
    }

}