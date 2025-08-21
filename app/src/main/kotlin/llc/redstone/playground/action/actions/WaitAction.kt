package llc.redstone.playground.action.actions

import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.item.Material
import llc.redstone.playground.action.Action
import llc.redstone.playground.action.ActionEnum
import llc.redstone.playground.action.Description
import llc.redstone.playground.action.DisplayName
import llc.redstone.playground.action.properties.ExpressionPropertyAnnotation
import llc.redstone.playground.action.properties.doubleValue
import llc.redstone.playground.feature.evalex.PGExpression
import llc.redstone.playground.database.Sandbox

data class WaitAction(
    @DisplayName("Duration") @Description("The duration to wait in ticks.")
    @ExpressionPropertyAnnotation
    val duration: String = "5.0",
): Action(
    ActionEnum.WAIT,
    "Wait",
    "Pauses action execution for a specified amount of time.",
    Material.CLOCK
) {

    override suspend fun execute(
        entity: Entity?,
        player: Player?,
        sandbox: Sandbox,
        event: Event?,
        expression: (String) -> PGExpression
    ) {
        val duration = this.doubleValue(expression(duration))
        val waitTime = (duration * 50).toLong() // 1 tick = 50 milliseconds
        kotlinx.coroutines.delay(waitTime)
    }
}