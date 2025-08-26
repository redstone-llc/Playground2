package llc.redstone.playground.menu.menus

import llc.redstone.playground.menu.PItem
import llc.redstone.playground.menu.invui.AnvilMenu
import llc.redstone.playground.menu.invui.NormalMenu
import llc.redstone.playground.utils.EnumMaterial
import llc.redstone.playground.utils.colorize
import llc.redstone.playground.utils.toTitleCase
import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import net.minestom.server.item.Material
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.PagedGui

class EnumMenu<T>(
    title: Component,
    val backMenu: NormalMenu,
    val enum: Class<T>,
    val previous: T?,
    val onSelect: (T) -> Unit
) : AnvilMenu(
    title
) {
    override fun initPlayerGUI(player: Player): Gui? {
        return PagedGui.items()
            .setStructure(
                "# x x x x x x x #",
                "# x x x x x x x #",
                "# x x x x x x x #",
                "# # # # b # # # #",
            )
            .addIngredient(
                'b', PItem(Material.ARROW)
                .name("<green>Go Back")
                .description("Go back to the previous menu")
                .leftClick {
                    backMenu.open(player)
                }
                .buildItem()
            )
            .setContent(
                enum.enumConstants.map { enum ->
                    val material = when (enum) {
                        is Material -> enum
                        is EnumMaterial -> enum.material
                        else -> Material.PAPER
                    }
                    PItem(material ?: Material.PAPER)
                        .description("Click to select this option")
                        .apply {
                            if (enum == previous) {
                                name("<green>${enum.toString().toTitleCase()} <gray>(Selected)")
                            } else {
                                name("<green>${enum.toString().toTitleCase()}")
                                leftClick { onSelect(enum) }
                            }
                        }
                        .buildItem()
                }
            )
            .build()
    }

    override fun onRename(player: Player, name: String) {
        TODO("Not yet implemented")
    }


}