package llc.redstone.playground.feature.commands

import net.minestom.server.entity.Player
import llc.redstone.playground.database.Sandbox
import llc.redstone.playground.feature.housingMenu.SystemsMenu
import llc.redstone.playground.managers.getSandbox
import llc.redstone.playground.menu.*
import llc.redstone.playground.menu.invui.AnvilMenu
import llc.redstone.playground.menu.items.BackItem
import llc.redstone.playground.menu.items.NextItem
import llc.redstone.playground.menu.items.PreviousItem
import llc.redstone.playground.utils.colorize
import llc.redstone.playground.utils.component
import llc.redstone.playground.utils.err
import llc.redstone.playground.utils.error
import llc.redstone.playground.utils.noError
import llc.redstone.playground.utils.openTextInput
import org.everbuild.asorda.resources.data.font.MenuCharacters
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.gui.structure.Markers
import xyz.xenondevs.invui.item.Item

class CommandsMenu : AnvilMenu(
    title = MenuCharacters.functionSearchMenu.component(-60),
    displayName = colorize("<green>Commands Menu")
) {
    var search = ""
    override fun initAnvilGUI(player: Player): Gui {
        return Gui.normal()
            .setStructure(
                "# # #"
            ) // where the anvil input should be
            .build()
    }

    val regex = Regex("[a-z0-9_\\-.]")
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
                "< # # f b a # # >"
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL) // where paged items should be put
            .addIngredient('<', PreviousItem())
            .addIngredient('>', NextItem())
            .addIngredient('b', BackItem(SystemsMenu(), "Systems Menu"))
//            .addIngredient('f') TODO: cycle items
            .addIngredient(
                'a', PItem()
                    .name("<green>Add Command")
                    .description("")
                    .leftClick {
                        player.openTextInput() {
                            title = "Set Command Name"
                            message = "Enter the name of the new command:"
                            validator = { input ->
                                if (input.isBlank()) {
                                    error("Command name cannot be blank!")
                                } else if (input.length > 32) {
                                    error("Command name cannot be longer than 32 characters!")
                                } else if (sandbox.commands.find { it.name == input } != null) {
                                    error("A command with that name already exists!")
                                } else if (regex.find(input) == null) {
                                    error("Command name can only contain letters, numbers, and underscores!")
                                } else {
                                    noError
                                }
                            }
                            action = { name ->
                                val newCommand = Command(name)

                                sandbox.commands.add(newCommand)

                                newCommand.registerCommand(sandbox)

                                CommandEditorMenu(newCommand).open(player)
                            }
                        }
                    }
                    .buildItem())
            .setContent(content(player, sandbox))
            .build()
    }

    private fun content(player: Player, sandbox: Sandbox): List<Item> {
        return sandbox.commands.map { command ->
            command.createDisplayItem()
                .leftClick("edit") {
                    CommandEditorMenu(command).open(player)
                }
                .buildItem()
        }
    }

    override fun onRename(player: Player, name: String) {
        search = name
        (lowerGUI as PagedGui<Item>).setContent(content(player, player.getSandbox() ?: return))
    }
}