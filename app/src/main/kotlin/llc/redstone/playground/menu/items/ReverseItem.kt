package llc.redstone.playground.menu.items

import llc.redstone.playground.utils.colorize
import llc.redstone.playground.utils.item
import org.everbuild.asorda.resources.data.items.GlobalIcons
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.impl.controlitem.PageItem

class ReverseItem : PageItem(false) {
    override fun getItemProvider(gui: PagedGui<*>): ItemProvider? {
        val item = GlobalIcons.iconArrowLeftRightGreen.item()
        item.withCustomName(colorize("Previous page"))
            .withLore(colorize(
                if (gui.currentPage > 0)
                    "Go to page ${gui.currentPage - 1}/${gui.pageAmount}"
                else "You can't go further back")
            )
        return ItemBuilder(item)
    }

}