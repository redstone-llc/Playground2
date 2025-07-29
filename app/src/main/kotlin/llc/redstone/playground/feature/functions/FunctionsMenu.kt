package llc.redstone.playground.feature.functions

import feature.actionMenu.ActionsMenu
import net.minestom.server.entity.Player
import net.minestom.server.item.Material
import llc.redstone.playground.action.actions.Return
import llc.redstone.playground.database.Sandbox
import llc.redstone.playground.feature.housingMenu.SystemsMenu
import llc.redstone.playground.managers.getSandbox
import llc.redstone.playground.menu.*
import llc.redstone.playground.menu.invui.AnvilMenu
import llc.redstone.playground.menu.items.BackItem
import llc.redstone.playground.menu.items.ForwardItem
import llc.redstone.playground.menu.items.ReverseItem
import llc.redstone.playground.utils.component
import llc.redstone.playground.utils.err
import llc.redstone.playground.utils.isValidIdentifier
import llc.redstone.playground.utils.openChat
import org.everbuild.asorda.resources.data.font.MenuCharacters
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.gui.structure.Markers
import xyz.xenondevs.invui.item.Item

class FunctionsMenu : AnvilMenu(
    title = MenuCharacters.functionSearchMenu.component(-60)
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
            .addIngredient('<', ReverseItem())
            .addIngredient('>', ForwardItem())
            .addIngredient('b', BackItem(SystemsMenu(), "Systems Menu"))
//            .addIngredient('f') TODO: cycle items
            .addIngredient(
                'a', PItem()
                    .name("<green>Add Function")
                    .description("")
                    .leftClick {
                        player.openChat("", "Enter function name") { name ->
                            if (name.isBlank()) return@openChat
                            if (!name.isValidIdentifier()) return@openChat player.err("Function name is not a valid identifier!")
                            if (name.length > 32) return@openChat player.err("Function name cannot be longer than 32 characters!")
                            val newFunction = Function(name = name, icon = Material.MAP)
                            newFunction.actions.add(Return())

                            sandbox.functions.add(newFunction)
                            this.open(player)
                        }
                    }
                    .buildItem())
            .setContent(content(player, sandbox))
            .build()
    }

    private fun content(player: Player, sandbox: Sandbox): List<Item> {
        return sandbox.functions.map { function ->
            PItem(Material.fromKey(function.icon)!!)
                .name("<green>${function.name}")
                .description(function.description)
                .data("Actions", "${function.actions.size} actions", null)
                .data("", "", null)
                .data("", "<yellow>Variables", null)
                .apply {
                    function.arguments.forEach {
                        this.data("<gray>${it.first.name}", "<white>${it.second}", null)
                    }
                }
                .leftClick {
                    ActionsMenu(function.actions, this).open(player)
                    null
                }
                .rightClick {
                    FunctionSettingsMenu(function).open(player)
                }
                .buildItem()
        }
    }

    override fun onRename(player: Player, name: String) {
        search = name
        (lowerGUI as PagedGui<Item>).setContent(content(player, player.getSandbox() ?: return))
    }
}