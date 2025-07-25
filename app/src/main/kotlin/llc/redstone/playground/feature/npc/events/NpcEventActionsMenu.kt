package llc.redstone.playground.feature.npc.events

import feature.actionMenu.ActionsMenu
import llc.redstone.playground.menu.*
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import llc.redstone.playground.feature.npc.NpcEntity
import llc.redstone.playground.feature.npc.menu.NpcEditMenu
import llc.redstone.playground.database.Sandbox
import llc.redstone.playground.utils.PaginationList

class NpcEventActionsMenu(
    val npc: NpcEntity,
    val sandbox: Sandbox
) : PaginationMenu(
    "Event Actions",
    InventoryType.CHEST_6_ROW
), Backable {
    override fun paginationList(player: Player): PaginationList<MenuItem>? {
        val eventActions = npc.eventActions;
        val menuItems = ArrayList<MenuItem>()
        for (event in NpcEventType.entries) {
            val menuItem = event.menuItem.clone()
            menuItem.action = {
                ActionsMenu(eventActions[event] ?: ArrayList(), this).open(player)
            }
            menuItems.add(menuItem)
        }

        return PaginationList(menuItems, slots.size)
    }

    override fun back(player: Player) {
        NpcEditMenu(npc, sandbox).open(player)
    }

    override fun backName(player: Player): String {
        return "Edit NPC: ${npc.name}"
    }
}