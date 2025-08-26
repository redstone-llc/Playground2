package llc.redstone.playground.action.properties

import llc.redstone.playground.action.ActionProperty
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import llc.redstone.playground.action.displayName

import llc.redstone.playground.database.Sandbox
import llc.redstone.playground.menu.invui.AbstractMenu
import llc.redstone.playground.utils.openChat
import net.minestom.server.inventory.click.Click
import java.lang.reflect.Field

class StringProperty : ActionProperty<String>(
    StringPropertyAnnotation::class
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

@Target(AnnotationTarget.FIELD)
annotation class StringPropertyAnnotation(
    val material: String = "STRING",
)