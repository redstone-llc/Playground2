package llc.redstone.playground.feature.commands

import llc.redstone.playground.action.Action
import llc.redstone.playground.action.ActionExecutor
import llc.redstone.playground.action.ActionProperties
import llc.redstone.playground.action.ActionProperty
import llc.redstone.playground.action.DisplayName
import llc.redstone.playground.action.displayValue
import llc.redstone.playground.database.Sandbox
import llc.redstone.playground.feature.commands.arguments.PlayerArg
import llc.redstone.playground.feature.evalex.PGExpression
import llc.redstone.playground.managers.getSandbox
import llc.redstone.playground.menu.PItem
import llc.redstone.playground.utils.colorize
import llc.redstone.playground.utils.err
import llc.redstone.playground.utils.toTitleCase
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.command.builder.condition.CommandCondition
import net.minestom.server.entity.Player
import net.minestom.server.item.Material
import org.everbuild.celestia.orion.core.packs.withEmojis
import org.everbuild.celestia.orion.platform.minestom.api.command.Kommand
import java.lang.reflect.Field


class Command(
    var name: String,
    var description: String = "",
    var actions: MutableList<Action> = mutableListOf(),
    var arguments: MutableList<CommandArg> = mutableListOf(),
) {
    @Transient
    var commandRegistry: CommandRegistry? = null
    val usage: String
        get() {
            if (arguments.isEmpty()) return "/$name"
            return "/$name " + arguments.joinToString(" ") { arg -> "<${arg.name}>" }
        }

    fun registerCommand(sandbox: Sandbox) {
        unregisterCommand(sandbox)

        commandRegistry = CommandRegistry(this, sandbox)
        commandRegistry!!.register()

        sandbox.instance!!.players.forEach { player -> player.refreshCommands() }
    }

    fun unregisterCommand(sandbox: Sandbox) {
        commandRegistry?.let { MinecraftServer.getCommandManager().unregister(it) }
        commandRegistry = null

        sandbox.instance!!.players.forEach { player -> player.refreshCommands() }
    }

    fun createDisplayItem(): PItem {
        return PItem(Material.COMMAND_BLOCK)
            .name(
                name.toTitleCase()
            )
            .info("Command")
            .description(description)
            .data("Actions", "${actions.size} actions", null)
    }
}

class CommandRegistry(
    val command: Command,
    val sandbox: Sandbox,
) : Kommand(command.name) {
    init {
        condition = CommandCondition { sender, _ ->
            if (sender !is Player) {
                return@CommandCondition false
            }

            val player = sender
            val playerSandbox = player.getSandbox()
            if (playerSandbox == null || playerSandbox != sandbox) {
                return@CommandCondition false
            }

            true
        }
        default { sender, _ ->
            if (command.arguments.isNotEmpty()) {
                sender.err("Usage: ${command.usage}")
                return@default
            }
            ActionExecutor(player, player, sandbox, null, ActionExecutor.ActionScope.PLAYER, command.actions).execute()
        }

        if (command.arguments.isNotEmpty()) {
            val cmdArgs = command.arguments.map {
                CustomArgument(it.name, it)
            }

            executes(*cmdArgs.toTypedArray()) {
                try {
                    val values = hashMapOf(*command.arguments.mapIndexed { index, arg ->
                        val obj = args[cmdArgs[index]]
                        val value = arg.value(player, sandbox, obj)
                        Pair(arg.name, value)
                    }.toTypedArray())

                    ActionExecutor(
                        player,
                        player,
                        sandbox,
                        null,
                        ActionExecutor.ActionScope.PLAYER,
                        command.actions
                    ).execute { str ->
                        PGExpression(str, player, player, sandbox, null)
                            .withValues(values)
                    }
                } catch (e: Exception) {
                    player.err("Error executing command: ${e.message ?: e.javaClass.simpleName}")
                }
            }
        }
    }
}


enum class CommandArgType(
    val clazz: Class<out CommandArg>,
    val greedy: Boolean = false,
) {
    PLAYER(PlayerArg::class.java),
}

abstract class CommandArg(
    var name: String,
    var enum: CommandArgType,
) {
    val properties: List<Pair<Field, ActionProperty<*>>>
        get() {
            val properties = mutableListOf<Pair<Field, ActionProperty<*>>>()
            for (field in this.javaClass.declaredFields) {
                for (property in ActionProperties.entries) {
                    if (field.isAnnotationPresent(property.annotation.java)) {
                        properties.add(Pair(field, property))
                    }
                }
            }
            return properties
        }

    fun List<Pair<Field, ActionProperty<*>>>.withValue(obj: Any?): Pair<Field, ActionProperty<*>> {
        return this.first {
            it.first.isAccessible = true
            val value = it.first.get(this@CommandArg)
            value == obj
        }
    }

    //See if the argument is valid for the given object
    abstract fun evaluate(sender: Player, sandbox: Sandbox, input: String): Boolean

    //Evaluate the argument and return the value
    abstract fun value(
        sender: Player,
        sandbox: Sandbox,
        input: String,
        expression: (String) -> PGExpression = { PGExpression(it, sender, sender, sandbox, null) }
    ): String

    //Completion for the argument
    open fun complete(sender: Player, sandbox: Sandbox, input: String): List<String> {
        return emptyList()
    }

    //Create a display item for the argument
    fun createDisplayItem(): PItem {
        return PItem(Material.PAPER)
            .name(name.toTitleCase())
            .info("Command Argument")
            .data("Type", enum.name, NamedTextColor.AQUA)
            .apply {
                if (properties.isNotEmpty()) {
                    data("<yellow>Settings", "", null)

                    for (field in this.javaClass.declaredFields) {
                        val displayName = field.getAnnotation(DisplayName::class.java)
                        if (displayName != null) {
                            val emoji = displayName.emoji.ifBlank { displayName.value }
                            data(emoji.withEmojis(), field.displayValue(this), colorize(displayName.color).color())
                        }
                    }
                }
            }
    }
}

