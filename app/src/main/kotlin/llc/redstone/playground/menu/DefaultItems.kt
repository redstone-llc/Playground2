package llc.redstone.playground.menu

import llc.redstone.playground.utils.colorize
import llc.redstone.playground.utils.item
import net.minestom.server.component.DataComponents
import net.minestom.server.item.Material
import net.minestom.server.item.component.TooltipDisplay
import org.everbuild.asorda.resources.data.items.GlobalIcons

object DefaultItems {
    val EMPTY = GlobalIcons.empty.item().withCustomName(colorize(""))
        .with(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay(true, setOf()))
}