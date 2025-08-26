package llc.redstone.playground.action.properties

import llc.redstone.playground.action.ActionProperty
import llc.redstone.playground.utils.isInteger
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import llc.redstone.playground.action.displayName
import llc.redstone.playground.action.getFieldFromAnnotation

import llc.redstone.playground.database.Sandbox
import llc.redstone.playground.menu.invui.AbstractMenu
import llc.redstone.playground.menu.invui.NormalMenu
import llc.redstone.playground.utils.err
import llc.redstone.playground.utils.openChat
import net.minestom.server.inventory.click.Click
import java.lang.reflect.Field

class DoubleProperty : ActionProperty<Double>(
    DoublePropertyAnnotation::class
) {
    override fun runnable(
        field: Field,
        obj: Any,
        event: Click,
        sandbox: Sandbox,
        player: Player,
        menu: AbstractMenu
    ) {
        player.openChat(value(obj, field).toString(), field.displayName()) { message ->
            if (!message.isInteger()) {
                player.err("The value provided is not a number.")
                return@openChat
            }

            val min = field.getFieldFromAnnotation<Double>("min") ?: Double.MIN_VALUE
            val max = field.getFieldFromAnnotation<Double>("max") ?: Double.MAX_VALUE
            val valueTemp = message.toDoubleOrNull()
            if (valueTemp != null && (valueTemp < min || valueTemp > max)) {
                player.err("The value must be between $min and $max")
                return@openChat
            }

            value(obj, field, valueTemp!!, player)
            menu.open(player)
        }
    }
}

@Target(AnnotationTarget.FIELD)
annotation class DoublePropertyAnnotation(
    val material: String = "BOOK",
    val min: Double = Double.MIN_VALUE,
    val max: Double = Double.MAX_VALUE,
)