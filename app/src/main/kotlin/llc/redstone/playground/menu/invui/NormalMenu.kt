package llc.redstone.server.menu

import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import llc.redstone.playground.menu.invui.AbstractMenu
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.window.Window

abstract class NormalMenu(
    title: Component
): AbstractMenu(title) {
    // 9x(1-6)
    var topGui: Gui? = null
    // Player gui
    var bottomGui: Gui? = null
    lateinit var window: Window
    override fun open(player: Player) {
        topGui = initTopGUI(player)
        bottomGui = initBottomGUI(player)
        val window = Window.split()
            .setTitle(title)
            .setViewer(player)
            .addCloseHandler {
                onClose(player)
            }
            .setUpperGui(topGui!!)
            .addOpenHandler {
                onOpen(player)
            }
        if (bottomGui != null) {
            window.setLowerGui(topGui!!)
        }
        this.window = window.build()
        this.window.open()
    }

    abstract fun initTopGUI(player: Player): Gui
    open fun initBottomGUI(player: Player): Gui? {
        return null
    }

}