package llc.redstone.playground.menu

import llc.redstone.playground.utils.colorize
import llc.redstone.playground.utils.item
import net.minestom.server.item.Material
import org.everbuild.asorda.resources.data.items.GlobalIcons

object DefaultItems {
    val CLEAR = GlobalIcons.iconDeleteGray.item().withCustomName(colorize(""))
}