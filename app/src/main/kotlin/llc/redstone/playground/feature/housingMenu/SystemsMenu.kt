package llc.redstone.playground.feature.housingMenu

import net.minestom.server.entity.Player
import net.minestom.server.item.Material
import llc.redstone.playground.feature.events.EventActionsMenu
import llc.redstone.playground.feature.functions.FunctionsMenu
import llc.redstone.playground.menu.PItem
import llc.redstone.playground.menu.items.BackItem
import llc.redstone.playground.utils.colorize
import llc.redstone.playground.menu.invui.NormalMenu
import xyz.xenondevs.invui.gui.Gui

class SystemsMenu : NormalMenu(
    colorize("Systems"),
) {
    override fun initTopGUI(player: Player): Gui {
        return Gui.normal()
            .setStructure(
                "# # # # # # # # #",
                "# e f # # # # # #",
                "# # # # # # # # #",
                "# # # # b # # # #",
            )
            .addIngredient('b', BackItem(PlaygroundMenu()))
            .addIngredient(
                'e', PItem(Material.GRASS_BLOCK)
                    .name("<green>Event Actions")
                    .leftClick("edit") { _, _ ->
                        EventActionsMenu().open(player)
                        null
                    }.buildItem()
            )
            .addIngredient(
                'f', PItem(Material.MAP)
                    .name("<green>Functions Menu")
                    .leftClick("edit") { _, _ ->
                        FunctionsMenu().open(player)
                        null
                    }.buildItem()
            )
            .build()
    }
}