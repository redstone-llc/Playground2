package llc.redstone.playground.action.actions

import com.ezylang.evalex.data.EvaluationValue
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.item.Material
import llc.redstone.playground.action.Action
import llc.redstone.playground.action.ActionEnum
import llc.redstone.playground.action.DisplayName
import llc.redstone.playground.action.properties.AnyPropertyAnnotation
import llc.redstone.playground.action.properties.anyValue
import llc.redstone.playground.feature.evalex.PGExpression
import llc.redstone.playground.database.Sandbox
import org.everbuild.celestia.orion.core.packs.withEmojis

class Return(
    @DisplayName("Return Value", ":return:", "<mach_mauve>")
    @AnyPropertyAnnotation
    val returnValue: String? = null
) : Action(
    ActionEnum.FUNCTION_RETURN,
    "Return",
    "Returns from a function, or from the action chain if not in a function. Does not support return value if not in a function.",
    Material.ARROW
) {
    override fun syncExecute(
        entity: Entity?, player: Player?, sandbox: Sandbox, event: Event?,
        expression: (String) -> PGExpression
    ): EvaluationValue? {
        if (returnValue == null) {
            return EvaluationValue.NULL_VALUE
        }

        return anyValue(expression(returnValue))
    }

    override suspend fun execute(
        entity: Entity?,
        player: Player?,
        sandbox: Sandbox,
        event: Event?,
        expression: (String) -> PGExpression
    ) {

    }
}