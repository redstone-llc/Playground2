package llc.redstone.playground.menu.invui

import llc.redstone.playground.menu.DefaultItems
import llc.redstone.playground.menu.items.ForwardItem
import llc.redstone.playground.menu.items.ReverseItem
import llc.redstone.playground.utils.colorize
import llc.redstone.playground.utils.component
import llc.redstone.playground.utils.item
import llc.redstone.playground.utils.spacingComp
import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import org.everbuild.asorda.resources.data.font.MenuAndUtilsCharacters
import org.everbuild.asorda.resources.data.items.GlobalIcons
import org.everbuild.asorda.resources.data.items.SystemIcons
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.gui.structure.Markers
import xyz.xenondevs.invui.item.Item
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.impl.SimpleItem


class TestMenu : AnvilMenu(
    title = MenuAndUtilsCharacters.functionSearchMenu.component(-59)
) {
    var search = ""
    override fun initAnvilGUI(player: Player): Gui {
        return Gui.normal()
            .setStructure(
                "a # #"
            )
            .addIngredient('a', ItemStack.of(Material.PAPER)) // where the anvil input should be
            .build()
    }

    override fun initPlayerGUI(player: Player): Gui {
        return PagedGui.items()
            .setStructure(
                "# x x x x x x x #",
                "# x x x x x x x #",
                "# x x x x x x x #",
                "# # # < # > # # #"
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL) // where paged items should be put
//            .addIngredient('<', ReverseItem())
//            .addIngredient('>', ForwardItem())
            .setContent(content())
            .build()
    }

    private fun content(): List<Item> {
        return Material.values()
            .filter { it.name().lowercase().contains(search) }
            .map {
                SimpleItem(
                    ItemBuilder(it)
                        .setDisplayName(Component.text("Item: ${it.name()}"))
                        .addLoreLines(Component.text("This is a test item."))
                )
            }
            .toList()
    }

    override fun onRename(player: Player, name: String) {
        search = name
        (lowerGUI as PagedGui<Item>).setContent(content())
    }
}