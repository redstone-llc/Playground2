package llc.redstone.playground.feature.functions

import feature.actionMenu.ActionsMenu
import net.minestom.server.entity.Player
import llc.redstone.playground.managers.getSandbox
import llc.redstone.playground.menu.PItem
import llc.redstone.playground.menu.items.BackItem
import llc.redstone.playground.menu.menus.MaterialSelector
import llc.redstone.playground.utils.DefaultFontInfo
import llc.redstone.playground.utils.StringUtils
import llc.redstone.playground.utils.colorize
import llc.redstone.playground.utils.component
import llc.redstone.playground.utils.err
import llc.redstone.playground.utils.error
import llc.redstone.playground.utils.isValidIdentifier
import llc.redstone.playground.utils.item
import llc.redstone.playground.utils.noError
import llc.redstone.playground.utils.openChat
import llc.redstone.playground.utils.openDialogInput
import llc.redstone.playground.utils.removeColor
import llc.redstone.playground.utils.serialize
import llc.redstone.playground.utils.success
import llc.redstone.playground.utils.wrapLoreLines
import llc.redstone.playground.utils.wrapMcFiveLoreLines
import net.kyori.adventure.text.Component
import org.everbuild.asorda.resources.data.font.MenuCharacters
import org.everbuild.asorda.resources.data.font.mcFiveCalcWidth
import org.everbuild.asorda.resources.data.items.GlobalIcons
import org.everbuild.celestia.orion.core.packs.OrionPacks
import xyz.xenondevs.invui.gui.Gui

class FunctionEditorMenu(
    private val function: Function,
    private val tab: Int = 1,
    private var titleWidth: Int = mcFiveCalcWidth("FUNCTION EDITOR: ${function.name}")
) : ActionsMenu(
    title = when (tab) {
        1 -> MenuCharacters.functionEditorInfo.component(-8)
        2 -> MenuCharacters.functionEditorSettings.component(-8)

        else -> Component.text("error")
    }.append {
        OrionPacks.getSpaceComponent(-173 + ((173 - titleWidth) / 2))
            .append(colorize("<font:playground:mcfive><black>FUNCTION EDITOR: ${function.name}</black></font>"))
    }.append {
        if (tab != 1) {
            Component.text {}
        } else {
            //TODO fix this :)
            val lines = wrapMcFiveLoreLines(colorize(function.description), 160)
            var comp = OrionPacks.getSpaceComponent(titleWidth * -1 + 36)
            var width = 0;
            for ((i, line) in lines.withIndex()) {
                var str = serialize(line).trim()

                if (i == 3) {
                    str += "..."
                }

                comp = comp.append(
                    OrionPacks.getSpaceComponent(-1 * width)
                        .append(colorize("<font:playground:mc_${i + 14}><black>${str}</black></font>"))
                )

                width = DefaultFontInfo.getDefaultFontInfo(removeColor(str))

                if (i == 3) {
                    break
                }
            }
            comp
        }
    },
    displayName = colorize("<green>Function Editor"),
    actions = function.actions
) {

    private fun <G : Gui, S : Gui.Builder<G, S>> Gui.Builder<G, S>.createDefault(): S {
        return this.setStructure(
            "# # # # # # # # #",
            "# d # D # V # M #",
            "# # # # # # # # #",
            "b # # # # # # i s"
        )
            .addIngredient('b', BackItem(FunctionsMenu()))
    }

    fun initFunctionInfoTab(player: Player): Gui? {
        return Gui.normal()
            .createDefault()
            .addIngredient('d', function.createDisplayItem().buildItem())
            .addIngredient(
                'i', PItem(GlobalIcons.empty.item())
                    .name("<red>Info")
                    .description("Stuff about the function")
                    .build()
            )
            .addIngredient(
                's',
                PItem(GlobalIcons.empty.item())
                    .name("<green>Settings")
                    .description("Stuff that the function has that should be changed")
                    .leftClick("switch to tab") {
                        FunctionEditorMenu(function, 2).open(player)
                        null
                    }
                    .buildItem()
            )
            .build()
    }

    fun initFunctionSettingsTab(player: Player): Gui? {
        return Gui.normal()
            .createDefault()
            .addIngredient(
                'd', PItem(GlobalIcons.empty.item())
                    .name("<green>Change Name")
                    .description("Change the name of the function")
                    .leftClick("open dialog") {
                        player.openDialogInput() {
                            title = "<primary>Change Function Name"
                            message = "Enter the new name for the function:"
                            previous = function.name
                            validator = { newName ->
                                if (!newName.isValidIdentifier())
                                    error("Function name is not a valid identifier!")
                                else if (player.getSandbox()!!.functions.any { it.name == newName })
                                    error("Function name already exists!")
                                else noError
                            }
                            action = { newName ->
                                function.name = newName
                                player.success("Function name changed to $newName")
                                this@FunctionEditorMenu.open(player)
                            }
                        }
                    }
                    .buildItem()
            )
            .addIngredient(
                'D', PItem(GlobalIcons.empty.item())
                    .name("<green>Change Description")
                    .description("Change the description of the function")
                    .leftClick("open chat") {
                        player.openDialogInput() {
                            title = "<primary>Change Function Description"
                            message = "Enter the new description for the function:"
                            previous = function.description
                            visualizer = { input ->
                                val lines = wrapMcFiveLoreLines(colorize(function.description), 160)
                                var comp = Component.empty()
                                for ((i, line) in lines.withIndex()) {
                                    var str = serialize(line).trim()
                                    if (i == 3) {
                                        str += "..."
                                    }
                                    comp = comp.append(colorize("<font:playground:mcfive>${str}</font>\n").shadowColor(null))
                                }
                                comp
                            }
                            validator = { newDescription ->
                                if (newDescription.isBlank())
                                    error("Function description cannot be empty!")
                                else noError
                            }
                            action = { newDescription ->
                                function.description = newDescription
                                player.success("Function description changed to $newDescription")
                                this@FunctionEditorMenu.open(player)
                            }
                        }
                    }
                    .buildItem()
            )
            .addIngredient(
                'V', PItem(GlobalIcons.empty.item())
                    .name("<green>Edit Arguments")
                    .description("Edit the arguments of the function")
                    .leftClick("open argument editor") {
                        null
                    }
                    .buildItem()
            )
            .addIngredient(
                'M', PItem(GlobalIcons.empty.item())
                    .name("<green>Change Icon")
                    .description("Change the icon of the function")
                    .leftClick("open material selector") {
                        MaterialSelector(this) {
                            function.icon = it.name()
                            this.open(player)
                        }.open(player)
                    }
                    .buildItem()
            )
            .addIngredient(
                'i', PItem(GlobalIcons.empty.item())
                    .name("<green>Info")
                    .description("Stuff about the function")
                    .leftClick("switch to tab") {
                        FunctionEditorMenu(function, 1).open(player)
                        null
                    }
                    .buildItem()
            )
            .addIngredient(
                's',
                PItem(GlobalIcons.empty.item())
                    .name("<red>Settings")
                    .description("Stuff that the function has that should be changed")
                    .build()
            )
            .build()
    }

    override fun initBottomGUI(player: Player): Gui? {
        when (tab) {
            1 -> return initFunctionInfoTab(player)
            2 -> return initFunctionSettingsTab(player)
            else -> {
                player.err("Invalid tab: $tab")
                return null
            }
        }
    }
}
