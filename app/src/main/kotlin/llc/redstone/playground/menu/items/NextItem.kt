package llc.redstone.playground.menu.items

import llc.redstone.playground.menu.PItem
import llc.redstone.playground.utils.item
import org.everbuild.asorda.resources.data.items.GlobalIcons
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.ItemWrapper
import xyz.xenondevs.invui.item.impl.controlitem.PageItem

class NextItem(
    val item: PItem = PItem(GlobalIcons.empty.item())
) : PageItem(true) {
    override fun getItemProvider(gui: PagedGui<*>): ItemProvider? {
        item.name("<green>Next page")
            .description(
                if (gui.currentPage < gui.pageAmount - 1)
                    "Go to page ${gui.currentPage + 1}/${gui.pageAmount}"
                else "You can't go further forward"
            )
        return ItemWrapper(item.build())
    }
}