package llc.redstone.playground.action.properties

import llc.redstone.playground.action.ActionProperty
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import llc.redstone.playground.menu.Menu
import llc.redstone.playground.menu.MenuItem
import llc.redstone.playground.database.Sandbox
import java.lang.reflect.Field

class BooleanProperty : ActionProperty<Boolean>(
    BooleanPropertyAnnotation::class
) {
    override fun runnable(
        field: Field,
        obj: Any,
        event: InventoryPreClickEvent,
        sandbox: Sandbox,
        player: Player,
        menu: Menu
    ) {
        value(obj, field, !(value(obj, field) ?: false), player)
        menu.open(player)
    }

    override fun getDisplayItem(obj: Any, field: Field): MenuItem {
        return super.getDisplayItem(obj, field)
            .itemStack(
                ItemStack.of(
                    if (value(obj, field) == true) {
                        Material.LIME_DYE
                    } else {
                        Material.RED_DYE
                    }
                )
            )
    }
}

@Target(AnnotationTarget.FIELD)
annotation class BooleanPropertyAnnotation(
    val material: String = "LIME_DYE",
)