package llc.redstone.playground.menu.invui

import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player

abstract class AbstractMenu(
    val title: Component
) {
    abstract fun open(player: Player)
    open fun onClose(player: Player) {

    }
    open fun onOpen(player: Player) {

    }
}