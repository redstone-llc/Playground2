package llc.redstone.playground.action.properties

import llc.redstone.playground.action.ActionProperty
import llc.redstone.playground.menu.PaginationMenu
import net.minestom.server.entity.Player
import net.minestom.server.item.Material
import llc.redstone.playground.database.Sandbox
import llc.redstone.playground.menu.PItem
import llc.redstone.playground.menu.invui.AbstractMenu
import llc.redstone.playground.utils.EnumMaterial
import net.minestom.server.inventory.click.Click
import java.lang.reflect.Field

class EnumProperty: ActionProperty<Enum<*>>(
    EnumPropertyAnnotation::class
) {
    override fun runnable(
        field: Field,
        obj: Any,
        event: Click,
        sandbox: Sandbox,
        player: Player,
        menu: AbstractMenu
    ) {
        // imo this doesn't need an entire class dedicated to it so object whatever it's called thingy it is!
        val menu = object: PaginationMenu("Select Option", menu) {
            override fun list(player: Player): MutableList<PItem> {
                val menuItems = ArrayList<PItem>()
                for (item in field.type.enumConstants) {
                    if (item !is Enum<*>) continue
                    item as? EnumMaterial ?: continue
                    val builder = PItem(item.material?: Material.PAPER)
                    builder.name("<yellow>${item.name}")
                    builder.leftClick("add") {
                        value(obj, field, item)
                        open(player)
                    }
                    menuItems.add(builder)
                }
                return menuItems
            }
        }

        menu.open(player)
    }
}

@Target(AnnotationTarget.FIELD)
annotation class EnumPropertyAnnotation(
    val material: String,
)