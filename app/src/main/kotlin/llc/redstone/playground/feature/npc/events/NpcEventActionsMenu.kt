package llc.redstone.playground.feature.npc.events

import feature.actionMenu.ActionsMenu
import net.minestom.server.entity.Player
import llc.redstone.playground.feature.npc.NpcEntity
import llc.redstone.playground.feature.npc.menu.NpcEditMenu
import llc.redstone.playground.database.Sandbox
import llc.redstone.playground.managers.getSandbox
import llc.redstone.playground.menu.invui.AnvilMenu
import llc.redstone.playground.menu.items.BackItem
import llc.redstone.playground.menu.items.NextItem
import llc.redstone.playground.menu.items.PreviousItem
import llc.redstone.playground.utils.colorize
import llc.redstone.playground.utils.err
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.gui.structure.Markers
import xyz.xenondevs.invui.item.Item


class NpcEventActionsMenu(
    private val npc: NpcEntity,
    private val sandbox: Sandbox
) : AnvilMenu(
    title = colorize("todo")
) {
    private var search: String = ""
    override fun initAnvilGUI(player: Player): Gui? {
        return Gui.normal()
            .setStructure(
                "# # #"
            )
            .build()
    }

    override fun initPlayerGUI(player: Player): Gui? {
        val sandbox = player.getSandbox() ?: run {
            player.err("You are not in a sandbox!")
            return null;
        }

        return PagedGui.items()
            .setStructure(
                "# x x x x x x x #",
                "# x x x x x x x #",
                "# x x x x x x x #",
                "< # # # b # # # >"
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL) // where paged items should be put
            .addIngredient('<', PreviousItem())
            .addIngredient('>', NextItem())
            .addIngredient('b', BackItem(NpcEditMenu(npc, sandbox)))
            .setContent(content(player, sandbox))
            .build()
    }

    private fun content(player: Player, sandbox: Sandbox): List<Item> {
        val eventActions = npc.eventActions
        val menuItems = ArrayList<Item>()
        for (event in NpcEventType.entries) {
            if (!eventActions.contains(event)) {
                eventActions[event] = ArrayList()
            }
            val menuItem = event.menuItem.clone()
            menuItem.leftClick("to edit") { _, _ ->
                ActionsMenu(eventActions[event] ?: ArrayList(), this).open(player)
                null
            }
            menuItems.add(menuItem.buildItem())
        }
        return menuItems
    }

    override fun onRename(player: Player, name: String) {
        search = name
        (lowerGUI as PagedGui<Item>).setContent(content(player, player.getSandbox() ?: return))
    }
}