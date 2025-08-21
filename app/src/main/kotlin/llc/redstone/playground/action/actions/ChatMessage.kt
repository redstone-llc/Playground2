package llc.redstone.playground.action.actions

import net.minestom.server.entity.Entity
import llc.redstone.playground.action.Action
import llc.redstone.playground.action.ActionEnum
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.item.Material
import llc.redstone.playground.action.Description
import llc.redstone.playground.action.DisplayName
import llc.redstone.playground.action.properties.StringPropertyAnnotation
import llc.redstone.playground.feature.evalex.PGExpression
import llc.redstone.playground.database.Sandbox
import llc.redstone.playground.utils.colorize

data class ChatMessage(
    @DisplayName("Message") @Description("The message to send to the player.")
    @StringPropertyAnnotation
    var message: String = "\"<yellow>Hello, world!\""
): Action(
    ActionEnum.CHAT_MESSAGE,
    "Display Chat Message",
    "Sends a chat message to the player.",
    Material.PAPER
) {
    override suspend fun execute(
        entity: Entity?,
        player: Player?,
        sandbox: Sandbox,
        event: Event?,
        expression: (String) -> PGExpression
    ) {
        player?.sendMessage(colorize(expression(message).evaluate().stringValue))
    }
}
