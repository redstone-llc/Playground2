package llc.redstone.playground.feature.commands

import llc.redstone.playground.managers.getSandbox
import net.minestom.server.command.ArgumentParserType
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.arguments.Argument
import net.minestom.server.command.builder.arguments.ArgumentString
import net.minestom.server.command.builder.suggestion.SuggestionCallback
import net.minestom.server.command.builder.suggestion.SuggestionEntry
import net.minestom.server.entity.Player

class CustomArgument (
    id: String,
    val commandArg: CommandArg,
): ArgumentString(id) {
    override fun allowSpace(): Boolean {
        return commandArg.enum.greedy
    }

    override fun getSuggestionCallback(): SuggestionCallback? {
        return SuggestionCallback { sender, context, suggestion ->
            if (sender !is Player) return@SuggestionCallback
            val sandbox = sender.getSandbox() ?: return@SuggestionCallback
            val currentArg = context.input
            val completions = commandArg.complete(sender, sandbox, currentArg)
            completions.forEach { arg ->
                suggestion.addEntry(SuggestionEntry(arg))
            }
        }
    }

    override fun parser(): ArgumentParserType? {
        return ArgumentParserType.STRING
    }
}