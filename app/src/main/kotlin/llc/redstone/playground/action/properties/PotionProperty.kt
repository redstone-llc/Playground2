package llc.redstone.playground.action.properties

import net.minestom.server.component.DataComponents
import llc.redstone.playground.utils.PaginationList
import llc.redstone.playground.action.ActionProperty
import llc.redstone.playground.menu.PaginationMenu
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.inventory.click.Click.Left
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.item.component.PotionContents
import net.minestom.server.potion.PotionEffect
import net.minestom.server.potion.PotionType

import llc.redstone.playground.database.Sandbox
import llc.redstone.playground.menu.PItem
import llc.redstone.playground.menu.invui.AbstractMenu
import net.minestom.server.inventory.click.Click
import java.lang.reflect.Field

class PotionProperty: ActionProperty<PotionEffect>(
    PotionPropertyAnnotation::class
) {
    override fun runnable(
        field: Field,
        obj: Any,
        event: Click,
        sandbox: Sandbox,
        player: Player,
        menu: AbstractMenu
    ) {
        val menu = object: PaginationMenu("Select Potion", menu) {
            override fun list(player: Player): MutableList<PItem> {
                val menuItems = ArrayList<PItem>()
                for (effect in PotionEffect.values()) {
                    val builder = PItem(ItemStack.of(Material.POTION)
                        .with(DataComponents.POTION_CONTENTS, PotionContents(PotionType.fromKey(effect.name())?: PotionType.WATER))
                    )
                    builder.name("<yellow>${effect.name()}")
                    builder.leftClick("add") {
                        value(obj, field, effect)
                        menu.open(player)
                    }
                    menuItems.add(builder)
                }
                return menuItems
            }
        }

        menu.open(player)
    }

    override fun displayValue(value: PotionEffect?): String? {
        if (value == null) {
            return "&cNone"
        }
        return value.name()
    }
}

@Target(AnnotationTarget.FIELD)
annotation class PotionPropertyAnnotation(
    val material: String = "POTION",
)