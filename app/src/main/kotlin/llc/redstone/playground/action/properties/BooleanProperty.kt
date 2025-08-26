package llc.redstone.playground.action.properties

import llc.redstone.playground.action.ActionProperty
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material

import llc.redstone.playground.database.Sandbox
import llc.redstone.playground.menu.PItem
import llc.redstone.playground.menu.invui.AbstractMenu
import llc.redstone.playground.menu.invui.NormalMenu
import net.minestom.server.inventory.click.Click
import java.lang.reflect.Field

class BooleanProperty : ActionProperty<Boolean>(
    BooleanPropertyAnnotation::class
) {
    override fun runnable(
        field: Field,
        obj: Any,
        event: Click,
        sandbox: Sandbox,
        player: Player,
        menu: AbstractMenu
    ) {
        value(obj, field, !(value(obj, field) ?: false), player)
        menu.open(player)
    }

    override fun getDisplayItem(obj: Any, field: Field): PItem {
        val item = super.getDisplayItem(obj, field)
        item.builder = ItemStack.of(
            if (value(obj, field) == true) {
                Material.LIME_DYE
            } else {
                Material.RED_DYE
            }
        )
        return item
    }
}

@Target(AnnotationTarget.FIELD)
annotation class BooleanPropertyAnnotation(
    val material: String = "LIME_DYE",
)