package llc.redstone.playground.menu.invui

import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.window.AnvilWindow


abstract class AnvilMenu(
    title: Component,
    displayName: Component = title
): AbstractMenu(title, displayName) {
    lateinit var anvilWindow: AnvilWindow
    // Player gui
    lateinit var lowerGUI: Gui

    override fun open(player: Player) {
        lowerGUI = initPlayerGUI(player)?: return
        anvilWindow = AnvilWindow.split().setTitle(title)
            .setUpperGui(initAnvilGUI(player)?: return)
            .setLowerGui(lowerGUI)
            .addCloseHandler {
                onClose(player)
            }
            .addOpenHandler {
                onOpen(player)
            }
            .addRenameHandler {
                onRename(player, it)
            }
            .setViewer(player)
            .build()

        anvilWindow.open()
    }

    open fun initAnvilGUI(player: Player): Gui? {
        return Gui.normal()
            .setStructure(
                "# # #"
            ) // where the anvil input should be
            .build()
    }
    abstract fun initPlayerGUI(player: Player): Gui?
    abstract fun onRename(player: Player, name: String)

    override fun onClose(player: Player) {

    }


    override fun onOpen(player: Player) {

    }
}