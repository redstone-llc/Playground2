package llc.redstone.playground.feature.housingMenu

import llc.redstone.playground.menu.PItem
import llc.redstone.playground.menu.items.CloseItem
import llc.redstone.playground.utils.colorize
import llc.redstone.playground.menu.invui.NormalMenu
import llc.redstone.playground.utils.err
import net.minestom.server.entity.Player
import net.minestom.server.item.Material
import xyz.xenondevs.invui.gui.Gui

class PlaygroundMenu: NormalMenu(
    colorize("Playground Menu"),
){
    override fun initTopGUI(player: Player): Gui {
        return Gui.normal()
            .setStructure(
                "# # # # # # # # #",
                "# # # # s # # # #",
                "# # # # # # # # #",
                "# # # # # # # # #",
                "# # # # b # # # #",
            )
            .addIngredient('b', CloseItem())
            .addIngredient(
                's', PItem(Material.ACTIVATOR_RAIL)
                    .name("<green>Systems")
                    .leftClick("open") { _, _ ->
                        try {
                            SystemsMenu().open(player)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            player.err("Failed to open Systems Menu: ${e.message}")
                        }
                        null
                    }.buildItem()
            )
            .build()
    }
}