package llc.redstone.playground.menu.menus

import net.minestom.server.entity.Player
import net.minestom.server.item.Material
import llc.redstone.playground.menu.PItem
import llc.redstone.playground.menu.invui.AbstractMenu
import llc.redstone.playground.utils.colorize
import llc.redstone.playground.menu.invui.NormalMenu
import xyz.xenondevs.invui.gui.Gui

class ConfirmMenu(private val backMenu: AbstractMenu, val onCancel: (Player) -> Unit = {
    backMenu.open(it)
}, val onConfirm: () -> Unit): NormalMenu(
    colorize("Confirm Action"),
) {
    override fun initTopGUI(player: Player): Gui {
                return Gui.normal()
            .setStructure(
                "# # # # # # # # # ",
                "# # c # # d # # # ",
                "# # # # # # # # # "
            )
            .addIngredient('c', PItem(Material.GREEN_WOOL).name("<green>Confirm").leftClick("confirm") { _, _ ->
                onConfirm()
                null
            }.build()) // Confirm item
            .addIngredient('d', PItem(Material.RED_WOOL).name("<red>Cancel").leftClick("cancel") { _, _ ->
                onCancel(player)
                null
            }.build()) // Cancel item
            .build()
    }

}