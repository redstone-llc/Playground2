package llc.redstone.playground.feature.actionMenu

import llc.redstone.playground.action.Action
import llc.redstone.playground.managers.getSandbox
import llc.redstone.playground.menu.Backable
import llc.redstone.playground.menu.Menu
import llc.redstone.playground.menu.invui.AbstractMenu
import llc.redstone.playground.utils.colorize
import llc.redstone.playground.utils.serialize
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType

class ActionEditMenu(
    val action: Action,
    val menu: AbstractMenu
) : Menu(
    "Action Edit",
    if (action.properties.size > 9) InventoryType.CHEST_5_ROW else InventoryType.CHEST_4_ROW
), Backable {
    override fun setupItems(player: Player) {
        val sandbox = player.getSandbox() ?: return
        for ((index, entry) in action.properties.withIndex()) {
            val item = entry.second.getDisplayItem(action, entry.first)
            item.action = { event -> entry.second.runnable(entry.first, action, event, sandbox, player, this) }

            addItem(slots[index], item)
        }
    }

    override fun back(player: Player) {
        menu.open(player)
    }

    override fun backName(player: Player): String {
        return serialize(menu.title)
    }
}