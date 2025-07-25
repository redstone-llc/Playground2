package llc.redstone.playground.menu.items

import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import llc.redstone.playground.menu.Menu
import llc.redstone.playground.menu.MenuItem

class BackItem(private val backMenu: Menu): MenuItem(
    ItemStack.of(Material.ARROW), {
        backMenu.open(it.player)
    }
) {
    init {
        name("<red>Go back")
        description("Go back to ${backMenu.title}")
        action(ClickType.LEFT_CLICK, "to go back")
    }
}