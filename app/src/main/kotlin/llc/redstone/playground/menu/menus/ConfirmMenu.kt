package llc.redstone.playground.menu.menus

import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.Material
import llc.redstone.playground.menu.Menu
import llc.redstone.playground.menu.menuItem

class ConfirmMenu(private val backMenu: Menu, val onCancel: (Player) -> Unit = {
    backMenu.open(it)
}, val onConfirm: () -> Unit): Menu(
    "Confirm Action",
    type = InventoryType.CHEST_3_ROW
) {
    override fun setupItems(player: Player) {
        addItem(12, menuItem(Material.GREEN_WOOL) {
            onConfirm()
            backMenu.open(player)
        }.name("<green>Confirm").action(ClickType.LEFT_CLICK, "to confirm"))

        addItem(14, menuItem(Material.RED_WOOL) {
            onCancel(player)
        }.name("<red>Cancel").action(ClickType.LEFT_CLICK, "to cancel"))
    }
}