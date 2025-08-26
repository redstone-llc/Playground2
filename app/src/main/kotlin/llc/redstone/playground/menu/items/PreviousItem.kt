package llc.redstone.playground.menu.items

import llc.redstone.playground.menu.PItem
import llc.redstone.playground.utils.item
import org.everbuild.asorda.resources.data.items.GlobalIcons
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.ItemWrapper
import xyz.xenondevs.invui.item.impl.controlitem.PageItem

class PreviousItem(
    val item: PItem = PItem(GlobalIcons.empty.item()).name("<green>Previous page")
) : PageItem(false) {
    override fun getItemProvider(gui: PagedGui<*>): ItemProvider? {
        item.description(
                if (gui.currentPage > 1)
                    "Go to page ${gui.currentPage - 1}/${gui.pageAmount}"
                else "You can't go further backwards"
            )
        return ItemWrapper(item.build())
    }
}