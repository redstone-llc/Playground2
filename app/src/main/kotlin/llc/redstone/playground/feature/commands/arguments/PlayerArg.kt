package llc.redstone.playground.feature.commands.arguments

import llc.redstone.playground.database.Sandbox
import llc.redstone.playground.feature.commands.CommandArg
import llc.redstone.playground.feature.commands.CommandArgType
import llc.redstone.playground.feature.evalex.PGExpression
import net.minestom.server.entity.Player

class PlayerArg(
    name: String
): CommandArg(
    name = name,
    enum = CommandArgType.PLAYER
) {
    override fun evaluate(
        sender: Player,
        sandbox: Sandbox,
        input: String
    ): Boolean {
        return sandbox.instance!!.players.find { it.username.equals(input, true) } != null
    }

    override fun value(
        sender: Player,
        sandbox: Sandbox,
        input: String,
        expression: (String) -> PGExpression
    ): String {
        return sandbox.instance!!.players.find { it.username.equals(input, ignoreCase = true) }?.username
            ?: throw IllegalArgumentException("Player not found: $input")
    }

    override fun complete(sender: Player, sandbox: Sandbox, input: String): List<String> {
        if (input.isBlank()) return emptyList()
        return sandbox.instance!!.players
            .filter { it.username.startsWith(input, ignoreCase = true) }
            .map { it.username }
            .sorted()
    }
}