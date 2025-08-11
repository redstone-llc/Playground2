package llc.redstone.playground.menu.items

import llc.redstone.playground.menu.PItem
import llc.redstone.playground.utils.colorize
import llc.redstone.playground.utils.item
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.inventory.click.Click
import net.minestom.server.item.ItemStack
import org.everbuild.asorda.resources.data.items.GlobalIcons
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.gui.ScrollGui
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.ItemWrapper
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.impl.controlitem.ControlItem
import xyz.xenondevs.invui.item.impl.controlitem.PageItem
import xyz.xenondevs.invui.item.impl.controlitem.ScrollItem

class DownItem(
    val item: PItem = PItem(GlobalIcons.empty.item())
) : ControlItem<ScrollGui<*>>() {
    override fun getItemProvider(gui: ScrollGui<*>): ItemProvider? {
        item.name("<green>Down line")
            .description(
                if (gui.canScroll(1))
                    ""
                else "You can't go further down"
            )
        return ItemWrapper(item.build())
    }

    override fun handleClick(
        clickType: Click,
        player: Player,
        event: InventoryPreClickEvent
    ) {
        gui.scroll(1)
    }
}