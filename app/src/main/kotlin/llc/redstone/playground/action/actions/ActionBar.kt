package llc.redstone.playground.action.actions

import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.item.Material
import llc.redstone.playground.action.Action
import llc.redstone.playground.action.ActionEnum
import llc.redstone.playground.action.Description
import llc.redstone.playground.action.DisplayName
import llc.redstone.playground.action.properties.StringPropertyAnnotation
import llc.redstone.playground.feature.evalex.PGExpression
import llc.redstone.playground.database.Sandbox
import llc.redstone.playground.utils.colorize

data class ActionBar(
    @DisplayName("Message") @Description("The message to show to the player.")
    @StringPropertyAnnotation
    var message: String = "<yellow>Hello, world!"
): Action(
    ActionEnum.ACTIONBAR,
    "Display Action Bar",
    "Sends a message through the action bar to the player.",
    Material.NAME_TAG,
) {
    override suspend fun execute(
        entity: Entity,
        player: Player?,
        sandbox: Sandbox,
        event: Event?,
        expression: (String) -> PGExpression
    ) {
        player?.sendActionBar(colorize(expression(message).evaluate().stringValue))
    }
}