package llc.redstone.playground.feature.functions

import com.ezylang.evalex.data.EvaluationValue
import com.ezylang.evalex.functions.FunctionParameterDefinition
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.click.Click
import net.minestom.server.inventory.click.Click.Left
import net.minestom.server.inventory.click.Click.LeftShift
import net.minestom.server.inventory.click.Click.Right
import net.minestom.server.inventory.click.Click.RightShift
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.Material
import llc.redstone.playground.managers.getSandbox
import llc.redstone.playground.menu.*
import llc.redstone.playground.utils.PaginationList
import llc.redstone.playground.utils.VariableUtils
import llc.redstone.playground.utils.err
import llc.redstone.playground.utils.isValidIdentifier
import llc.redstone.playground.utils.openChat
import llc.redstone.playground.utils.VariableUtils.toBuilder

class VariablesMenu(val function: Function) : PaginationMenu(
    "Variables",
    InventoryType.CHEST_6_ROW
), Backable {
    override fun setupItems(player: Player) {
        super.setupItems(player)

        val sandbox = player.getSandbox() ?: return player.err("You are not in a sandbox!")

        addItem(
            53, menuItem(Material.LIME_WOOL) {
                player.openChat("", "Enter variable name") { name ->
                    if (name.isBlank()) return@openChat player.err("Variable name cannot be empty!")
                    if (!name.isValidIdentifier()) return@openChat player.err("Variable name is not a valid identifier!")
                    if (name.length > 16) return@openChat player.err("Variable name cannot be longer than 16 characters!")

                    if (sandbox.functions.any { it.arguments.any { it.first.name == name } }) {
                        return@openChat player.err("Variable with name '$name' already exists in this function!")
                    }

                    val parameterDefinition = FunctionParameterDefinition.builder().name(name).build()
                    function.arguments.add(Pair(parameterDefinition, EvaluationValue.DataType.STRING))

                    this.open(player)
                }
            }.name("<green>Add Variable")
                .description("Click to add a new variable")
                .action(ClickType.LEFT_CLICK, "to add")
        )
    }

    override fun paginationList(player: Player): PaginationList<MenuItem>? {
        val sandbox = player.getSandbox() ?: return null
        return PaginationList(function.arguments.mapIndexed() { i, (arg, type) ->
            menuItem(VariableUtils.argTypeToMaterial(type)!!) {
                when (it.click) {
                    is Left -> {
                        player.openChat(arg.name, "Enter new variable name") { newName ->
                            if (newName.isBlank()) return@openChat player.err("Variable name cannot be empty!")
                            if (!newName.isValidIdentifier()) return@openChat player.err("Variable name is not a valid identifier!")
                            if (newName.length > 16) return@openChat player.err("Variable name cannot be longer than 16 characters!")

                            if (sandbox.functions.any { it.arguments.any { it.first.name == newName } }) {
                                return@openChat player.err("Variable with name '$newName' already exists in this function!")
                            }

                            function.arguments[i] = Pair(
                                arg.toBuilder().name(newName).build(),
                                type
                            )

                            this.open(player)
                        }
                    }

                    is Right -> {
                        TypeMenu(this, function.arguments, i).open(player)
                    }

                    is LeftShift -> {
                        function.arguments[i] = Pair(
                            arg.toBuilder().isVarArg(!arg.isVarArg).build(),
                            type
                        )
                        this.open(player)
                    }

                    is RightShift -> {
                        function.arguments[i] = Pair(
                            arg.toBuilder().isLazy(!arg.isLazy).build(),
                            type
                        )
                        this.open(player)
                    }

                    is Click.Middle -> {
                        function.arguments.removeIf { it.first.name == arg.name }
                        this.open(player)
                    }

                    else -> {}
                }
            }.name("<green>${arg.name}")
                .info("Type", "<gray>${type.name}")
                .info("Greedy", arg.isVarArg.toString())
                .info("Lazy", arg.isLazy.toString())
                .action(Left::class.java, "to edit name")
                .action(Right::class.java, "to edit type")
                .action(LeftShift::class.java, "to toggle greedy")
                .action(RightShift::class.java, "to toggle lazy")
                .action(Click.Middle::class.java, "to remove")
        }, slots.size)
    }

    override fun back(player: Player) {
        FunctionSettingsMenu(function).open(player)
    }

    override fun backName(player: Player): String {
        return "Function Settings Menu"
    }

    class TypeMenu(private val menu: VariablesMenu, private val args: MutableList<Pair<FunctionParameterDefinition, EvaluationValue.DataType>>, val index: Int): PaginationMenu(
        "Variable Type",
        InventoryType.CHEST_4_ROW
    ), Backable {
        override fun paginationList(player: Player): PaginationList<MenuItem> {
            return PaginationList(
                EvaluationValue.DataType.entries.filter { VariableUtils.argTypeToMaterial(it) != null }.map { type ->
                    menuItem(VariableUtils.argTypeToMaterial(type)!!) {
                        args[index] = Pair(args[index].first, type)
                        menu.open(player)
                    }.name("<green>${type.name}")
                        .action(Left::class.java, "to select")
                }, slots.size
            )

        }

        override fun back(player: Player) {
            menu.open(player)
        }

        override fun backName(player: Player): String {
            return "Variables Menu"
        }
    }
}