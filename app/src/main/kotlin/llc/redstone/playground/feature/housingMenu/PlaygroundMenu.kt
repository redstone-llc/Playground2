package llc.redstone.playground.feature.housingMenu

import llc.redstone.playground.menu.PItem
import llc.redstone.playground.menu.items.CloseItem
import llc.redstone.playground.utils.colorize
import llc.redstone.playground.menu.invui.NormalMenu
import llc.redstone.playground.utils.component
import llc.redstone.playground.utils.err
import llc.redstone.playground.utils.item
import net.minestom.server.entity.Player
import net.minestom.server.item.Material
import org.everbuild.asorda.resources.data.font.MenuCharacters
import org.everbuild.asorda.resources.data.items.GlobalIcons
import xyz.xenondevs.invui.gui.Gui

class PlaygroundMenu: NormalMenu(
    MenuCharacters.playgroundMenu.component(-9),
    colorize("Playground Menu"),
){
    override fun initTopGUI(player: Player): Gui {
        return Gui.normal()
            .setStructure(
                "# # # # # # # # #",
                "# # # # # # # # #",
                "# # # s s s # # #",
                "# # # s s s # # #",
                "# # # # # # # # #",
                "# # # # # # # # #",
            )
            .addIngredient(
                's', PItem(GlobalIcons.empty.item())
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