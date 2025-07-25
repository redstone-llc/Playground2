package llc.redstone.playground.feature.housingMenu

import llc.redstone.playground.feature.npc.events.NpcEventActionsMenu
import llc.redstone.playground.menu.Backable
import llc.redstone.playground.menu.Menu
import llc.redstone.playground.menu.menuItem
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.Material
import llc.redstone.playground.feature.events.EventActionsMenu
import llc.redstone.playground.feature.functions.FunctionsMenu

class SystemsMenu : Menu(
    "Systems",
    InventoryType.CHEST_6_ROW
), Backable {
    override fun setupItems(player: Player) {
        for (i in 0 until type.size) { //Template for adding items
            addItem(i, menuItem(Material.AIR) {
                player.sendMessage("You clicked on item $i")
            })
        }

        addItem(
            10, menuItem(Material.COBWEB) {
                EventActionsMenu().open(player)
            }.name("<green>Event Actions")
                .description("")
                .action(ClickType.LEFT_CLICK, "to edit")
        )
        addItem(
            11, menuItem(Material.MAP) {
                FunctionsMenu().open(player)
            }.name("<green>Functions Menu")
                .description("")
                .action(ClickType.LEFT_CLICK, "to edit")
        )
    }

    override fun back(player: Player) {
        PlaygroundMenu().open(player)
    }

    override fun backName(player: Player): String {
        return "Playground Menu"
    }
}