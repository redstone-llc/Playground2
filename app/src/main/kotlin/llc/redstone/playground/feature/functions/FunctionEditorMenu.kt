package llc.redstone.playground.feature.functions

import com.ezylang.evalex.functions.FunctionParameterDefinition
import feature.actionMenu.ActionsMenu
import net.minestom.server.entity.Player
import llc.redstone.playground.managers.getSandbox
import llc.redstone.playground.menu.PItem
import llc.redstone.playground.menu.items.BackItem
import llc.redstone.playground.menu.items.ForwardItem
import llc.redstone.playground.menu.items.ReverseItem
import llc.redstone.playground.menu.menus.ConfirmMenu
import llc.redstone.playground.menu.menus.MaterialSelector
import llc.redstone.playground.utils.VariableUtils.toBuilder
import llc.redstone.playground.utils.colorize
import llc.redstone.playground.utils.component
import llc.redstone.playground.utils.dialogs.TextDialogBuilder
import llc.redstone.playground.utils.err
import llc.redstone.playground.utils.error
import llc.redstone.playground.utils.isValidIdentifier
import llc.redstone.playground.utils.item
import llc.redstone.playground.utils.noError
import llc.redstone.playground.utils.openTextInput
import llc.redstone.playground.utils.success
import net.kyori.adventure.text.Component
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import org.everbuild.asorda.resources.data.font.MenuCharacters
import org.everbuild.asorda.resources.data.items.FunctionIcons
import org.everbuild.asorda.resources.data.items.GlobalIcons
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.gui.ScrollGui
import xyz.xenondevs.invui.gui.structure.Markers
import xyz.xenondevs.invui.item.Item

class FunctionEditorMenu(
    private val function: Function,
    private var tab: Int = 1
) : ActionsMenu(
    title = Component.empty(),
    displayName = colorize("<green>Function Editor"),
    actions = function.actions
) {
    private var prevTab = tab

    override fun title(): Component {
        return MenuCharacters.functionEditorActions.component(-10).append {
            when (tab) {
                1 -> MenuCharacters.functionEditorActionsSettings.component(-181)
                2 -> MenuCharacters.functionEditorArguments.component(-181)
                3 -> MenuCharacters.functionEditorSettings.component(-181)
                4 -> MenuCharacters.actionLibrary.component(-181)
                else -> Component.text("error")
            }
        }
    }

    fun initFunctionActionsSettingsTab(player: Player): Gui? {
        return Gui.normal()
            .setStructure(
                "# # # # # # # # #",
                "# c # p # i # e #",
                "# # # # # # # # #",
                "b # # A a s # # f"
            )
            .addIngredient('b', BackItem(FunctionsMenu()))
            .addTabButtons(player)
            .addIngredient(
                'f',
                function.createDisplayItem().build()
            )
            .build()
    }

    fun initFunctionSettingsTab(player: Player): Gui? {
        return Gui.normal()
            .setStructure(
                "# # # # # # # # #",
                "# n # d # m # D #",
                "# # # # # # # # #",
                "b # # A a s # # f"
            )
            .addIngredient('b', BackItem(FunctionsMenu()))
            .addTabButtons(player)
            .addIngredient(
                'f',
                function.createDisplayItem().build()
            )

            .addIngredient(
                'n', PItem(GlobalIcons.empty.item())
                    .name("<green>Change Name")
                    .description("Change the name of the function")
                    .leftClick("open dialog") {
                        player.openTextInput() {
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
                'd', PItem(GlobalIcons.empty.item())
                    .name("<green>Change Description")
                    .description("Change the description of the function")
                    .leftClick("open chat") {
                        player.openTextInput() {
                            title = "<primary>Change Function Description"
                            message = "Enter the new description for the function:"
                            previous = function.description
                            visualizer = TextDialogBuilder.mcFiveVisualizer
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
                'm', PItem(GlobalIcons.empty.item())
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
                'D', PItem(GlobalIcons.empty.item())
                    .name("<red>Delete Function")
                    .description("Delete this function permanently")
                    .leftClick("delete function") {
                        ConfirmMenu(this) {
                            player.getSandbox()!!.functions.remove(function)
                            player.success("Function ${function.name} deleted!")
                            FunctionsMenu().open(player)
                        }.open(player)
                    }
                    .buildItem()
            )
            .build()
    }

    private var selectedArgument: Int? = null

    fun initFunctionArgumentsTab(player: Player): Gui? {
        return PagedGui.items()
            .setStructure(
                "p x x x x x n N N",
                "p x x x x x n V V",
                "p x x x x x n D D",
                "b # # A a s # # f"
            )
            .addIngredient('b', BackItem(FunctionsMenu()))
            .addTabButtons(player)
            .addIngredient(
                'f',
                function.createDisplayItem().build()
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .addIngredient('p', ReverseItem())
            .addIngredient('n', ForwardItem())
            .setContent(functionArgumentsTabContent(player))
            .addIngredient(
                'N', PItem(GlobalIcons.empty.item())
                    .name("<green>Change Name")
                    .description("Change the name of the selected argument" + if (selectedArgument == null) "\n\n<red>Please select an argument to change the name</red>" else "")
                    .apply {
                        if (selectedArgument != null) {
                            leftClick("open dialog") {
                                player.openTextInput() {
                                    title = "<primary>Change Argument Name"
                                    message = "Enter the new name for the argument:"
                                    previous = function.arguments[selectedArgument!!].name
                                    validator = { newName ->
                                        if (!newName.isValidIdentifier())
                                            error("Argument name is not a valid identifier!")
                                        else if (function.arguments.any { it.name == newName && it != function.arguments[selectedArgument!!] })
                                            error("Argument name already exists!")
                                        else noError
                                    }
                                    action = { newName ->
                                        function.arguments[selectedArgument!!] =
                                            function.arguments[selectedArgument!!].toBuilder().name(newName).build()
                                        player.success("Argument name changed to $newName")
                                    }
                                }
                            }
                        }
                    }.buildItem()
            )
            .addIngredient(
                'V', PItem(GlobalIcons.empty.item())
                    .name("<green>Toggle Greedy Argument")
                    .description("Change if the selected argument is a variable argument (greedy)" + if (selectedArgument == null) "\n\n<red>Please select an argument to toggle</red>" else "")
                    .apply {
                        if (selectedArgument != null) {
                            leftClick("toggle variable argument") {
                                function.arguments[selectedArgument!!] =
                                    function.arguments[selectedArgument!!].toBuilder()
                                        .isVarArg(!function.arguments[selectedArgument!!].isVarArg).build()
                                player.success("Argument ${function.arguments[selectedArgument!!].name} is now ${if (function.arguments[selectedArgument!!].isVarArg) "a variable argument" else "not a variable argument"}")
                            }
                        }
                    }.buildItem()
            )
            .addIngredient(
                'D', PItem(GlobalIcons.empty.item())
                    .name("<red>Delete Argument")
                    .description("Delete the selected argument" + if (selectedArgument == null) "\n\n<red>Please select an argument to delete</red>" else "")
                    .apply {
                        if (selectedArgument != null) {
                            leftClick("delete argument") {
                                function.arguments.removeAt(selectedArgument!!)
                                player.success("Argument deleted!")
                                selectedArgument = null
                                open(player)
                            }
                        }
                    }.buildItem()
            )
            .build()
    }

    fun functionArgumentsTabContent(player: Player): List<Item> {
        return function.arguments.mapIndexed { index, it ->
            PItem(if (selectedArgument == index) FunctionIcons.selectedArgument.item() else ItemStack.of(Material.PAPER))
                .name(it.name)
                .info("Argument")
                .data("Greedy", if (it.isVarArg) "<green>Yes" else "<red>No", null)
                .apply {
                    if (selectedArgument == index) {
                        leftClick("deselect argument") {
                            selectedArgument = null
                            open(player)
                        }
                    } else {
                        leftClick("select argument") {
                            selectedArgument = index
                            open(player)
                        }
                    }
                }
                .buildItem()
        }.plus(
            PItem(FunctionIcons.addArgument.item())
                .name("<green>Add Argument")
                .description("Add a new argument to the function")
                .leftClick("add argument") {
                    player.openTextInput {
                        title = "<primary>Add Function Argument"
                        message = "Enter the name of the new argument:"
                        previous = "newArgument"
                        validator = { newName ->
                            if (!newName.isValidIdentifier())
                                error("Argument name is not a valid identifier!")
                            else if (function.arguments.any { it.name == newName })
                                error("Argument name already exists!")
                            else noError
                        }
                        action = { newName ->
                            function.arguments.add(FunctionParameterDefinition.builder().name(newName).build())
                            player.success("Argument $newName added to function ${function.name}")
                            open(player)
                        }
                    }
                }
                .buildItem()
        )
    }

    fun <G : Gui, S : Gui.Builder<G, S>> Gui.Builder<G, S>.addTabButtons(player: Player): Gui.Builder<G, S> {
        addIngredient(
            'A',
            PItem(GlobalIcons.empty.item())
                .name(if (tab != 1) "<green>Actions Settings" else "<red>Actions Settings")
                .description("Copy, paste, import or export actions")
                .apply {
                    if (tab != 1) leftClick("go to actions settings tab") {
                        tab = 1
                        open(player)
                    }
                }
                .buildItem()
        )
        addIngredient(
            'a',
            PItem(GlobalIcons.empty.item())
                .name(if (tab != 2) "<green>Arguments" else "<red>Arguments")
                .description("Edit the arguments of the function")
                .apply {
                    if (tab != 2) leftClick("go to args tab") {
                        tab = 2
                        open(player)
                    }
                }
                .buildItem()
        )
        addIngredient(
            's',
            PItem(GlobalIcons.empty.item())
                .name(if (tab != 3) "<green>Settings" else "<red>Settings")
                .description("Stuff that the function has that should be changed")
                .apply {
                    if (tab != 3) leftClick("go to settings tab") {
                        tab = 3
                        open(player)
                    }
                }
                .buildItem()
        )
        return this
    }

    override fun actionLibraryGui(player: Player): PagedGui.Builder<Item> {
        return super.actionLibraryGui(player)
            .addIngredient(
                'b', PItem(GlobalIcons.empty.item())
                    .name("<red>Back to Previous Tab")
                    .description("Go back to the function editor tab you were on")
                    .leftClick("go back") {
                        tab = prevTab
                        open(player)
                    }
                    .buildItem()
            )
    }

    override fun topGui(player: Player): ScrollGui.Builder<Item> {
        return super.topGui(player)
            .addIngredient(
                'a', PItem(GlobalIcons.empty.item())
                    .name(if (tab != 4) "<green>Action Library" else "<red>Action Library")
                    .description("Open the action library to view all available actions.")
                    .apply {
                        if (tab != 4) leftClick("open action library") {
                            prevTab = tab
                            tab = 4
                            open(player)
                        } else {
                            leftClick("close action library") {
                                tab = prevTab
                                open(player)
                            }
                        }
                    }
                    .buildItem()
            )
    }

    override fun initBottomGUI(player: Player): Gui? {
        when (tab) {
            1 -> return initFunctionActionsSettingsTab(player)
            2 -> return initFunctionArgumentsTab(player)
            3 -> return initFunctionSettingsTab(player)
            4 -> return super.initBottomGUI(player)
            else -> {
                player.err("Invalid tab: $tab")
                return null
            }
        }
    }
}
