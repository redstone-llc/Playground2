package feature.actionMenu

import llc.redstone.playground.action.Action
import llc.redstone.playground.menu.*
import net.minestom.server.entity.Player
import llc.redstone.playground.feature.actionMenu.ActionEditMenu
import llc.redstone.playground.feature.actionMenu.AddActionMenu
import llc.redstone.playground.managers.getSandbox
import llc.redstone.playground.menu.invui.AbstractMenu
import llc.redstone.playground.menu.invui.AnvilMenu
import llc.redstone.playground.menu.items.BackItem
import llc.redstone.playground.menu.items.ForwardItem
import llc.redstone.playground.menu.items.ReverseItem
import llc.redstone.playground.utils.component
import llc.redstone.playground.utils.err
import llc.redstone.playground.utils.item
import net.minestom.server.inventory.click.Click
import org.everbuild.asorda.resources.data.font.MenuCharacters
import org.everbuild.asorda.resources.data.items.GlobalIcons
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.gui.structure.Markers
import xyz.xenondevs.invui.item.Item

class ActionsMenu(
    val actions: MutableList<Action>,
    private val backMenu: AbstractMenu
) : AnvilMenu(
    title = MenuCharacters.actionsSearchMenu.component(-60)
) {
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
                "< # # # b a # # >"
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL) // where paged items should be put
            .addIngredient('<', ReverseItem())
            .addIngredient('>', ForwardItem())
            .addIngredient('b', BackItem(backMenu))
            .addIngredient(
                'a', PItem(GlobalIcons.empty.item())
                    .name("<green>Add Action</green>")
                    .leftClick("add") { _, _ ->
                        AddActionMenu(this).open(player)

                        null
                    }.buildItem()
            )
            .setContent(content(player))
            .build()
    }

    private fun content(player: Player): List<Item> {
        return actions.map { action ->
            action.createDisplayItem()
                .leftClick("edit") { _, _ ->
                    if (action.properties.isNotEmpty()) ActionEditMenu(
                        action,
                        this
                    ).open(player)

                    null
                }
                .click(Click.Middle::class, "<success>⮝ clone</success>") { _, _ ->
                    val newAction = action.clone()
                    actions.add(newAction)
                    open(player)

                    null
                }
                .rightClick("remove") { _, _ ->
                    actions.remove(action)
                    open(player)

                    null
                }.buildItem()
        }
    }

    override fun onRename(player: Player, name: String) {

    }
}