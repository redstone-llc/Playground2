package llc.redstone.playground.menu.invui

import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
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
        val window = if (bottomGui == null) {
             Window.single()
                .setTitle(title)
                .setViewer(player)
                .addCloseHandler {
                    onClose(player)
                }
                .setGui(topGui!!)
                .addOpenHandler {
                    onOpen(player)
                }
        } else {
            Window.split()
                .setTitle(title)
                .setViewer(player)
                .addCloseHandler {
                    onClose(player)
                }
                .setUpperGui(topGui!!)
                .setLowerGui(bottomGui!!)
                .addOpenHandler {
                    onOpen(player)
                }
        }
        this.window = window.build()
        this.window.open()
    }

    abstract fun initTopGUI(player: Player): Gui
    open fun initBottomGUI(player: Player): Gui? {
        return null
    }

}