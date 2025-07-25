package llc.redstone.playground.feature.events

import feature.actionMenu.ActionsMenu
import llc.redstone.playground.feature.housingMenu.SystemsMenu
import llc.redstone.playground.managers.getSandbox
import llc.redstone.playground.menu.*
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import llc.redstone.playground.utils.PaginationList

class EventActionsMenu : PaginationMenu(
    "Event Actions",
    InventoryType.CHEST_6_ROW
), Backable {
    override fun paginationList(player: Player): PaginationList<MenuItem>? {
        val sandbox = player.getSandbox() ?: return null
        val eventActions = sandbox.events
        val menuItems = ArrayList<MenuItem>()
        for (event in EventType.entries) {
            if (!eventActions.contains(event.name)) {
                eventActions[event.name] = ArrayList()
            }
            val menuItem = event.menuItem.clone()
            menuItem.action = {
                ActionsMenu(eventActions[event.name] ?: ArrayList(), this).open(player)
            }
            menuItems.add(menuItem)
        }

        return PaginationList(menuItems, slots.size)
    }

    override fun back(player: Player) {
        SystemsMenu().open(player)
    }

    override fun backName(player: Player): String {
        return "Systems Menu"
    }
}