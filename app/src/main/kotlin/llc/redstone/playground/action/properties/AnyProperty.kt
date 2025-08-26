package llc.redstone.playground.action.properties

import com.ezylang.evalex.data.EvaluationValue
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import llc.redstone.playground.action.Action
import llc.redstone.playground.action.ActionProperty
import llc.redstone.playground.action.displayName
import llc.redstone.playground.feature.evalex.PGExpression
import llc.redstone.playground.database.Sandbox
import llc.redstone.playground.menu.invui.AbstractMenu
import llc.redstone.playground.menu.invui.NormalMenu
import llc.redstone.playground.utils.openChat
import net.minestom.server.inventory.click.Click
import java.lang.reflect.Field

class AnyProperty : ActionProperty<String>(
    AnyPropertyAnnotation::class
) {
    override fun runnable(
        field: Field,
        obj: Any,
        event: Click,
        sandbox: Sandbox,
        player: Player,
        menu: AbstractMenu
    ) {
        player.openChat(value(obj, field) ?: "", field.displayName()) { message ->
            value(obj, field, message, player)
            menu.open(player)
        }
    }
}

fun Action.anyValue(expression: PGExpression): EvaluationValue {
    return expression.evaluate()
}

@Target(AnnotationTarget.FIELD)
annotation class AnyPropertyAnnotation(
    val material: String = "MAP",
)