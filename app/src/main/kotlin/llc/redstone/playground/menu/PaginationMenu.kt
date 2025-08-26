package llc.redstone.playground.menu

import llc.redstone.playground.menu.invui.AbstractMenu
import llc.redstone.playground.menu.invui.NormalMenu
import llc.redstone.playground.menu.items.BackItem
import llc.redstone.playground.menu.items.NextItem
import llc.redstone.playground.menu.items.PreviousItem
import net.minestom.server.entity.Player
import net.minestom.server.item.Material
import llc.redstone.playground.utils.colorize
import llc.redstone.playground.utils.err
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.gui.structure.Markers

abstract class PaginationMenu(
    title: String,
    val back: AbstractMenu,
) : NormalMenu(colorize(title)) {
    override fun initTopGUI(player: Player): Gui? {
        return PagedGui.items()
            .setStructure(
                "# x x x x x x x #",
                "# x x x x x x x #",
                "# x x x x x x x #",
                "# x x x x x x x #",
                "# x x x x x x x #",
                "< # # # b # # # >"
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL) // where paged items should be put
            .addIngredient('<', PreviousItem())
            .addIngredient('>', NextItem())
            .addIngredient('b', BackItem(back))
            .setContent(list(player).map { it.buildItem() })
            .build()
    }

    abstract fun list(player: Player): MutableList<PItem>
    open fun notFoundItem(player: Player): PItem = PItem(Material.BEDROCK)
        .name("<red>No items found!")
        .leftClick {
            player.err("No items found!")
        }
}