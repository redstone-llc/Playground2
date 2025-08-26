package llc.redstone.playground.feature.functions

import llc.redstone.playground.action.ActionExecutor
import net.minestom.server.entity.Player
import net.minestom.server.item.Material
import llc.redstone.playground.action.actions.Return
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
import llc.redstone.playground.utils.isValidIdentifier
import llc.redstone.playground.utils.noError
import llc.redstone.playground.utils.openMultiInput
import llc.redstone.playground.utils.openTextInput
import org.everbuild.asorda.resources.data.font.MenuCharacters
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.gui.structure.Markers
import xyz.xenondevs.invui.item.Item

class FunctionsMenu : AnvilMenu(
    title = MenuCharacters.functionSearchMenu.component(-60),
    displayName = colorize("<green>Functions Menu")
) {
    var search = ""
    override fun initAnvilGUI(player: Player): Gui {
        return Gui.normal()
            .setStructure(
                "# # #"
            ) // where the anvil input should be
            .build()
    }

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
                    .name("<green>Add Function")
                    .description("")
                    .leftClick {
                        player.openTextInput() {
                            title = "Set Function Name"
                            message = "Enter the name of the new function:"
                            validator = { input ->
                                if (input.isBlank()) {
                                    error("Function name cannot be blank!")
                                } else if (!input.isValidIdentifier()) {
                                    error("Function name is not a valid identifier!")
                                } else if (input.length > 32) {
                                    error("Function name cannot be longer than 32 characters!")
                                } else {
                                    noError
                                }
                            }
                            action = { name ->
                                player.openMultiInput {
                                    title = "Function Scope"
                                    message = "Select the function scope that determines where this function can be used."
                                    options = mutableListOf("player", "entity", "sandbox")
                                    action = { scope ->
                                        val functionScope = ActionExecutor.ActionScope.valueOf(scope.uppercase())
                                        val newFunction = Function(name, functionScope, icon = Material.MAP)
                                        newFunction.actions.add(Return())

                                        sandbox.functions.add(newFunction)
                                        FunctionEditorMenu(newFunction).open(player)
                                    }
                                }
                            }
                        }
                    }
                    .buildItem())
            .setContent(content(player, sandbox))
            .build()
    }

    private fun content(player: Player, sandbox: Sandbox): List<Item> {
        return sandbox.functions.map { function ->
            function.createDisplayItem()
                .leftClick("edit") {
                    FunctionEditorMenu(function).open(player)
                }
                .buildItem()
        }
    }

    override fun onRename(player: Player, name: String) {
        search = name
        (lowerGUI as PagedGui<Item>).setContent(content(player, player.getSandbox() ?: return))
    }
}