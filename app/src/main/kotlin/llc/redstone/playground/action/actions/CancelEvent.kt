package llc.redstone.playground.action.actions

import com.ezylang.evalex.data.EvaluationValue
import net.minestom.server.entity.Entity
import llc.redstone.playground.action.Action
import llc.redstone.playground.action.ActionEnum
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.trait.CancellableEvent
import net.minestom.server.item.Material
import llc.redstone.playground.feature.evalex.PGExpression
import llc.redstone.playground.database.Sandbox

class CancelEvent : Action(
    ActionEnum.CANCEL_EVENT,
    "Cancel Event",
    "Stops an event from executing.",
    Material.TNT
) {

    override fun syncExecute(
        entity: Entity?, player: Player?, sandbox: Sandbox, event: Event?,
        expression: (String) -> PGExpression
    ): EvaluationValue? {
        if (event == null || event !is CancellableEvent) {
            return null
        }
        event.isCancelled = true

        return null
    }

    override suspend fun execute(
        entity: Entity?,
        player: Player?,
        sandbox: Sandbox,
        event: Event?,
        expression: (String) -> PGExpression
    ) {
        //Do nothing, because if its async then it will not be able to cancel the event
    }

    override fun requiresEvent(): Boolean {
        return true
    }
}