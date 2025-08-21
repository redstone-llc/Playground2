package llc.redstone.playground.utils.dialogs

import llc.redstone.playground.utils.Error
import llc.redstone.playground.utils.colorize
import llc.redstone.playground.utils.error
import llc.redstone.playground.utils.noError
import net.kyori.adventure.dialog.DialogLike
import net.kyori.adventure.key.Key
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.minestom.server.dialog.Dialog
import net.minestom.server.dialog.DialogAction.DynamicCustom
import net.minestom.server.dialog.DialogActionButton
import net.minestom.server.dialog.DialogBody.PlainMessage
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerCustomClickEvent

abstract class DialogBuilder <T, D : DialogLike> {
    open var title: String = "Input Dialog"
    open var context: String? = null
    open var previous: T? = null
    open var message: String = "Enter your desired value ${
        if (context.isNullOrBlank()) "" else "for <success>$context</success>"
    } in the box below:"
    open var visualizer: ((T) -> Component)? = null
    open var width = 200;
    open var validator: (T) -> Error = { error }
    open var action: (T) -> Unit = {}

    abstract fun toDialog(): D
    abstract fun handleInput(player: Player, dialog: D, event: PlayerCustomClickEvent): Boolean

    fun message(input: T, error: Error = noError): PlainMessage {
        var comp = Component.empty()
        if (visualizer != null) {
            comp = comp.append(colorize("<yellow>Current input:\n")).append(visualizer!!(input).append(text("\n\n")))
        }
        comp = comp.append(colorize("<green>${message}"))
        if (error()) {
            comp = comp.append(colorize("\n\n<error>${error}</error>"))
        }

        return PlainMessage(comp, 200)
    }

    fun button(
        key: String,
        label: String,
        width: Int = 100
    ): DialogActionButton {
        return DialogActionButton(
            Component.text(label),
            null,
            width,
            DynamicCustom(Key.key(key), CompoundBinaryTag.builder().build())
        )
    }
}