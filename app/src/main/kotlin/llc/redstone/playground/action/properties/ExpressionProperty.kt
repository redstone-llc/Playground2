package llc.redstone.playground.action.properties

import llc.redstone.playground.action.ActionProperty
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import llc.redstone.playground.action.Action
import llc.redstone.playground.action.displayName
import llc.redstone.playground.feature.evalex.PGExpression
import llc.redstone.playground.feature.evalex.styleExpression
import llc.redstone.playground.menu.Menu
import llc.redstone.playground.database.Sandbox
import llc.redstone.playground.utils.openChat
import java.lang.reflect.Field

class NumberProperty : ActionProperty<String>(
    ExpressionPropertyAnnotation::class
) {
    override fun displayValue(value: String?): String? {
        return value?.styleExpression()
    }

    override fun runnable(field: Field, obj: Any, event: InventoryPreClickEvent, sandbox: Sandbox, player: Player, menu: Menu) {
        player.openChat(value(obj, field).toString(), field.displayName()) { message ->
//            PGExpression(message, player, player, sandbox, null).
//            if (field.getAnnotation(ExpressionPropertyAnnotation::class.java).isNumber) {
//
//            }

            value(obj, field, message, player)
            menu.open(player)
        }
    }
}

fun Action.doubleValue(expression: PGExpression): Double {
    val numProp = this.properties.withValue(expression.expressionString).first.getAnnotation(ExpressionPropertyAnnotation::class.java)
    val value = expression.evaluate().numberValue.toDouble()

    if (value < numProp.min || value > numProp.max) {
        throw IllegalArgumentException("Value $value is out of bounds [${numProp.min}, ${numProp.max}]")
    }

    return value
}

@Target(AnnotationTarget.FIELD)
annotation class ExpressionPropertyAnnotation(
    val isNumber: Boolean = false,
    val material: String = "BOOK",
    val min: Double = Double.MIN_VALUE,
    val max: Double = Double.MAX_VALUE,
)