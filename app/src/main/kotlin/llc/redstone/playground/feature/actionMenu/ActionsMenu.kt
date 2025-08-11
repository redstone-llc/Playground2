package feature.actionMenu

import llc.redstone.playground.action.Action
import llc.redstone.playground.menu.*
import net.minestom.server.entity.Player
import llc.redstone.playground.feature.actionMenu.ActionEditMenu
import llc.redstone.playground.feature.actionMenu.AddActionMenu
import llc.redstone.playground.feature.functions.FunctionsMenu
import llc.redstone.playground.managers.getSandbox
import llc.redstone.playground.menu.invui.AbstractMenu
import llc.redstone.playground.menu.invui.AnvilMenu
import llc.redstone.playground.menu.invui.NormalMenu
import llc.redstone.playground.menu.items.BackItem
import llc.redstone.playground.menu.items.DownItem
import llc.redstone.playground.menu.items.ForwardItem
import llc.redstone.playground.menu.items.ReverseItem
import llc.redstone.playground.menu.items.UpItem
import llc.redstone.playground.utils.colorize
import llc.redstone.playground.utils.component
import llc.redstone.playground.utils.err
import llc.redstone.playground.utils.item
import net.kyori.adventure.text.Component
import net.minestom.server.inventory.click.Click
import org.everbuild.asorda.resources.data.font.MenuCharacters
import org.everbuild.asorda.resources.data.items.GlobalIcons
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.gui.ScrollGui
import xyz.xenondevs.invui.gui.TabGui
import xyz.xenondevs.invui.gui.structure.Markers
import xyz.xenondevs.invui.item.Item

open class ActionsMenu(
    val actions: MutableList<Action>,
    val backMenu: AbstractMenu? = null,
    title: Component = MenuCharacters.actionsSearchMenu.component(-60),
    displayName: Component = colorize("<green>Actions Menu")
) : NormalMenu(title, displayName) {
    override fun initTopGUI(player: Player): Gui {
        return ScrollGui.items()
            .setStructure(
                "x x x x x x x x x",
                "x x x x x x x x x",
                "x x x x x x x x x",
                "x x x x x x x x x",
                "x x x x x x x x x",
                "u u u # a # d d d"
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL) // where paged items should be put
            .addIngredient('u', UpItem())
            .addIngredient('d', DownItem())
            .addIngredient('a', PItem(GlobalIcons.empty.item())
                .name("<green>Add Action")
                .description("Add a new action to the list")
                .leftClick("open menu") { _, _ ->

                    null
                }
                .buildItem()
            )
            .setContent(content(player))
            .build()
    }



    open fun actionLibraryGui(player: Player): Gui {
        return PagedGui.items()
            .setStructure(
                "# x x x x x x x #",
                "# x x x x x x x #",
                "# x x x x x x x #",
                "# # # # # # # # #"
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL) // where paged items should be put
            .build()
    }

    override fun initBottomGUI(player: Player): Gui? {
        val tab = TabGui.normal()
            .setStructure(
                "# # # # # # # # #",
                "# # # # # # # # #",
                "# # # # b # # # #",
                "# # # # # # # # #"
            )
            .addTab(Gui.normal().build()) // Tab 0 is empty, can be used for future purposes
            .addTab(actionLibraryGui(player)) //TODO: Action library tab
        if (backMenu != null) {
            tab.addIngredient('b', BackItem(backMenu))
        }

        return tab.build()
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
}