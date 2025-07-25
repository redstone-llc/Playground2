package llc.redstone.playground.action.properties

import llc.redstone.playground.utils.PaginationList
import llc.redstone.playground.action.ActionProperty
import llc.redstone.playground.menu.MenuItem
import llc.redstone.playground.menu.PaginationMenu
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.inventory.click.Click.Left
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.Material
import llc.redstone.playground.menu.Menu
import llc.redstone.playground.menu.menuItem
import llc.redstone.playground.database.Sandbox
import llc.redstone.playground.utils.EnumMaterial
import java.lang.reflect.Field

class EnumProperty: ActionProperty<Enum<*>>(
    EnumPropertyAnnotation::class
) {
    override fun runnable(field: Field, obj: Any, event: InventoryPreClickEvent, sandbox: Sandbox, player: Player, menu: Menu) {
        // imo this doesn't need an entire class dedicated to it so object whatever it's called thingy it is!
        val menu = object: PaginationMenu("Select Option") {
            override fun paginationList(player: Player): PaginationList<MenuItem> {
                val menuItems = ArrayList<MenuItem>()
                for (item in field.type.enumConstants) {
                    if (item !is Enum<*>) continue
                    item as? EnumMaterial ?: continue
                    val builder = menuItem(item.material?: Material.PAPER)
                    {
                        if (event.click is Left) value(obj, field, item)
                        menu.open(player)
                    }
                    builder.name("<yellow>${item.name}")
                    builder.action(ClickType.LEFT_CLICK, "to add")
                    menuItems.add(builder)
                }
                return PaginationList(menuItems, slots.size)
            }
        }

        menu.open(player)
    }
}

@Target(AnnotationTarget.FIELD)
annotation class EnumPropertyAnnotation(
    val material: String,
)