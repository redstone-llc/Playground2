package llc.redstone.playground.feature.commands

import feature.actionMenu.ActionsMenu
import net.minestom.server.entity.Player
import llc.redstone.playground.managers.getSandbox
import llc.redstone.playground.menu.PItem
import llc.redstone.playground.menu.items.BackItem
import llc.redstone.playground.menu.items.NextItem
import llc.redstone.playground.menu.items.PreviousItem
import llc.redstone.playground.menu.menus.ConfirmMenu
import llc.redstone.playground.menu.menus.EnumMenu
import llc.redstone.playground.utils.colorize
import llc.redstone.playground.utils.component
import llc.redstone.playground.utils.dialogs.TextDialogBuilder
import llc.redstone.playground.utils.err
import llc.redstone.playground.utils.error
import llc.redstone.playground.utils.isValidIdentifier
import llc.redstone.playground.utils.item
import llc.redstone.playground.utils.noError
import llc.redstone.playground.utils.openMultiInput
import llc.redstone.playground.utils.openTextInput
import llc.redstone.playground.utils.success
import llc.redstone.playground.utils.toTitleCase
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

class CommandEditorMenu(
    private val command: Command,
    private var tab: Int = 1
) : ActionsMenu(
    title = Component.empty(),
    displayName = colorize("<green>Command Editor"),
    actions = command.actions
) {
    private var prevTab = tab

    override fun title(): Component {
        return MenuCharacters.commandEditorActions.component(-10).append {
            when (tab) {
                1 -> MenuCharacters.commandEditorActionsSettings.component(-181)
                2 -> MenuCharacters.commandEditorArguments.component(-181)
                3 -> MenuCharacters.commandEditorSettings.component(-181)
                4 -> MenuCharacters.actionLibrary.component(-181)
                else -> Component.text("error")
            }
        }
    }

    fun initCommandActionsSettingsTab(player: Player): Gui? {
        return Gui.normal()
            .setStructure(
                "# # # # # # # # #",
                "# c # p # i # e #",
                "# # # # # # # # #",
                "b # # A a s # # f"
            )
            .addIngredient('b', BackItem(CommandsMenu()))
            .addTabButtons(player)
            .addIngredient(
                'f',
                command.createDisplayItem().build()
            )
            .build()
    }

    private var selectedArgument: Int? = null

    fun initCommandArgumentsTab(player: Player): Gui? {
        return PagedGui.items()
            .setStructure(
                "p x x x x x n N T",
                "p x x x x x n P P",
                "p x x x x x n D D",
                "b # # A a s # # f"
            )
            .addIngredient('b', BackItem(CommandsMenu()))
            .addTabButtons(player)
            .addIngredient(
                'f',
                command.createDisplayItem().build()
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .addIngredient('p', PreviousItem())
            .addIngredient('n', NextItem())
            .setContent(commandArgumentsTabContent(player))
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
                                    previous = command.arguments[selectedArgument!!].name
                                    validator = { newName ->
                                        if (!newName.isValidIdentifier())
                                            error("Argument name is not a valid identifier!")
                                        else if (command.arguments.any { it.name == newName && it != command.arguments[selectedArgument!!] })
                                            error("Argument name already exists!")
                                        else noError
                                    }
                                    action = { newName ->
                                        command.arguments[selectedArgument!!].name = newName
                                        command.registerCommand(player.getSandbox()!!)
                                        open(player)
                                        player.success("Argument name changed to $newName")
                                    }
                                }
                            }
                        }
                    }.buildItem()
            )
            .addIngredient(
                'T', PItem(GlobalIcons.empty.item())
                    .name("<green>Change Type")
                    .description("Change the type of the selected argument" + if (selectedArgument == null) "\n\n<red>Please select an argument to change the type</red>" else "")
                    .apply {
                        if (selectedArgument != null) {
                            leftClick("edit type") {
                                EnumMenu(
                                    title = colorize("<green>Change Argument Type"),
                                    backMenu = this@CommandEditorMenu,
                                    enum = CommandArgType::class.java,
                                    previous = command.arguments[selectedArgument!!].enum
                                ) { newType ->
                                    if (newType.greedy) {
                                        if (selectedArgument!! < command.arguments.size - 1) {
                                            return@EnumMenu player.err("Cannot change type to ${newType.name}, because there are more arguments after it.")
                                        }
                                        if (command.arguments.any { arg -> arg.enum.greedy }) {
                                            return@EnumMenu player.err("Cannot change type to ${newType.name}, because there is already a greedy argument.")
                                        }
                                    }
                                    command.arguments[selectedArgument!!].enum = newType
                                    command.registerCommand(player.getSandbox()!!)
                                    open(player)
                                    player.success("Argument type changed to ${newType.name}")
                                }
                            }
                        }
                    }.buildItem()
            )
            .addIngredient(
                'P', PItem(GlobalIcons.empty.item())
                    .name("<green>Argument Properties")
                    .description("Change properties of the selected argument" + if (selectedArgument == null) "\n\n<red>Please select an argument to change properties</red>" else "")
                    .apply {
                        if (selectedArgument != null) {
                            leftClick("open properties menu") {
                                val arg = command.arguments[selectedArgument!!]
                                CommandArgPropertiesMenu(command, arg).open(player)
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
                                command.arguments.removeAt(selectedArgument!!)
                                player.success("Argument deleted!")
                                selectedArgument = null
                                open(player)
                            }
                        }
                    }.buildItem()
            )
            .build()
    }

    val regex = Regex("[a-z0-9_\\-.]")

    fun commandArgumentsTabContent(player: Player): List<Item> {
        fun item(index: Int, arg: CommandArg): PItem {
            return PItem(if (selectedArgument == index) FunctionIcons.selectedArgument.item() else ItemStack.of(Material.PAPER))
                .name(arg.name)
                .info("Argument")
                .data("Greedy", if (arg.enum.greedy) "<green>Yes" else "<red>No", null)
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
        }

        var last = command.arguments.lastOrNull()

        val items = command.arguments.takeWhile { !it.enum.greedy }.mapIndexed { index, it ->
            item(index, it).buildItem()
        }.plus(
            PItem(FunctionIcons.addArgument.item())
                .name("<green>Add Argument")
                .description("Add a new argument to the command")
                .leftClick("add argument") {
                    player.openTextInput {
                        title = "<primary>Add Command Argument"
                        message = "Enter the name of the new argument:"
                        previous = "newArgument"
                        validator = { newName ->
                            if (regex.find(newName) == null)
                                error("Argument name is not a valid identifier!")
                            else if (command.arguments.any { it.name == newName })
                                error("Argument name already exists!")
                            else noError
                        }
                        action = { newName ->
                            player.openMultiInput {
                                title = "<primary>Add Command Argument Type"
                                message = "Select the type of the new argument:"
                                options = CommandArgType.entries.map { it.name.lowercase() }.toMutableList()
                                action = { selectedType ->
                                    val type = CommandArgType.valueOf(selectedType)
                                    val newArg =
                                        type.clazz.getConstructor(String::class.java)
                                            .newInstance(newName)
                                    command.arguments.add(newArg)
                                    command.registerCommand(player.getSandbox()!!)
                                    player.success("Argument $newName of type $selectedType added!")
                                }
                            }
                        }
                    }
                }.buildItem()
        ).toMutableList()

        if (last != null && last.enum.greedy) {
            items += item(command.arguments.indexOf(last), last).buildItem()
        }

        return items
    }

    fun initCommandSettingsTab(player: Player): Gui? {
        return Gui.normal()
            .setStructure(
                "# # # # # # # # #",
                "# # n # d # D # #",
                "# # # # # # # # #",
                "b # # A a s # # f"
            )
            .addIngredient('b', BackItem(CommandsMenu()))
            .addTabButtons(player)
            .addIngredient(
                'f',
                command.createDisplayItem().build()
            )

            .addIngredient(
                'n', PItem(GlobalIcons.empty.item())
                    .name("<green>Change Name")
                    .description("Change the name of the command")
                    .leftClick("open dialog") {
                        player.openTextInput() {
                            title = "<primary>Change Command Name"
                            message = "Enter the new name for the command:"
                            previous = command.name
                            validator = { newName ->
                                if (!newName.isValidIdentifier())
                                    error("Command name is not a valid identifier!")
                                else if (player.getSandbox()!!.commands.any { it.name == newName })
                                    error("Command name already exists!")
                                else noError
                            }
                            action = { newName ->
                                command.name = newName
                                player.success("Command name changed to $newName")
                                this@CommandEditorMenu.open(player)
                            }
                        }
                    }
                    .buildItem()
            )
            .addIngredient(
                'd', PItem(GlobalIcons.empty.item())
                    .name("<green>Change Description")
                    .description("Change the description of the command")
                    .leftClick("open chat") {
                        player.openTextInput() {
                            title = "<primary>Change Command Description"
                            message = "Enter the new description for the command:"
                            previous = command.description
                            visualizer = TextDialogBuilder.mcFiveVisualizer
                            validator = { newDescription ->
                                if (newDescription.isBlank())
                                    error("Command description cannot be empty!")
                                else noError
                            }
                            action = { newDescription ->
                                command.description = newDescription
                                player.success("Command description changed to $newDescription")
                                this@CommandEditorMenu.open(player)
                            }
                        }
                    }
                    .buildItem()
            )
            .addIngredient(
                'D', PItem(GlobalIcons.empty.item())
                    .name("<red>Delete Command")
                    .description("Delete this command permanently")
                    .leftClick("delete command") {
                        ConfirmMenu(this) {
                            player.getSandbox()!!.commands.remove(command)
                            player.success("Command ${command.name} deleted!")
                            CommandsMenu().open(player)
                        }.open(player)
                    }
                    .buildItem()
            )
            .build()
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
                .description("Add or remove arguments for the command")
                .apply {
                    if (tab != 2) leftClick("go to arguments tab") {
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
                .description("Stuff that the command has that should be changed")
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
                    .description("Go back to the command editor tab you were on")
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
            1 -> return initCommandActionsSettingsTab(player)
            2 -> return initCommandArgumentsTab(player)
            3 -> return initCommandSettingsTab(player)
            4 -> return super.initBottomGUI(player)
            else -> {
                player.err("Invalid tab: $tab")
                return null
            }
        }
    }
}
