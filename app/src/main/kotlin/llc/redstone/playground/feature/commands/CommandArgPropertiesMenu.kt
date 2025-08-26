package llc.redstone.playground.feature.commands

import net.minestom.server.entity.Player
import llc.redstone.playground.database.Sandbox
import llc.redstone.playground.managers.getSandbox
import llc.redstone.playground.menu.invui.NormalMenu
import llc.redstone.playground.menu.items.BackItem
import llc.redstone.playground.utils.colorize
import llc.redstone.playground.utils.component
import llc.redstone.playground.utils.err
import org.everbuild.asorda.resources.data.font.MenuCharacters
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.gui.structure.Markers
import xyz.xenondevs.invui.item.Item

class CommandArgPropertiesMenu(
    private var command: Command,
    private var argument: CommandArg
) : NormalMenu(
    title = MenuCharacters.commandArgumentProperties.component(-10),
    displayName = colorize("<green>Argument Properties Menu")
) {

    override fun initTopGUI(player: Player): Gui? {
        val sandbox = player.getSandbox() ?: run {
            player.err("You are not in a sandbox!")
            return null
        }

        return PagedGui.items()
            .setStructure(
                "# x x x x x x x #",
                "# x x x x x x x #",
                "# x x x x x x x #",
                "# # # # b # # # a"
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL) // where paged items should be put
            .addIngredient('b', BackItem(CommandEditorMenu(command), "Command Editor"))
            .setContent(content(player, sandbox))
            .build()
    }

    private fun content(player: Player, sandbox: Sandbox): List<Item> {
        return argument.properties.map { (field, property) ->
            property.getDisplayItem(argument, field)
                .leftClick {
                    property.runnable(field, argument, it, sandbox, player, this)
                }
                .buildItem()
        }
    }
}