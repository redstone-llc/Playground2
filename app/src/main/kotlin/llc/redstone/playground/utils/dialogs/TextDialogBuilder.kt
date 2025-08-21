package llc.redstone.playground.utils.dialogs

import llc.redstone.playground.feature.evalex.styleExpression
import llc.redstone.playground.utils.Error
import llc.redstone.playground.utils.colorize
import llc.redstone.playground.utils.dialogs.getString
import llc.redstone.playground.utils.err
import llc.redstone.playground.utils.noError
import llc.redstone.playground.utils.serialize
import llc.redstone.playground.utils.wrapMcFiveLoreLines
import net.kyori.adventure.dialog.DialogLike
import net.kyori.adventure.key.Key
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.minestom.server.MinecraftServer
import net.minestom.server.dialog.Dialog
import net.minestom.server.dialog.DialogAction.DynamicCustom
import net.minestom.server.dialog.DialogActionButton
import net.minestom.server.dialog.DialogAfterAction
import net.minestom.server.dialog.DialogBody
import net.minestom.server.dialog.DialogBody.PlainMessage
import net.minestom.server.dialog.DialogInput
import net.minestom.server.dialog.DialogMetadata
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerCustomClickEvent
import net.minestom.server.network.packet.server.common.ShowDialogPacket

class TextDialogBuilder: DialogBuilder<String, Dialog.MultiAction>() {
    var expression: Boolean = false

    fun input(input: String): DialogInput.Text {
        return DialogInput.Text(
            "input",
            width,
            Component.empty(),
            false,
            input,
            1024,
            null
        )
    }

    override fun handleInput(player: Player, dialog: Dialog.MultiAction, event: PlayerCustomClickEvent): Boolean {
        val value = event.getString("input") ?: return false
        if (event.key == Key.key("playground.submit_input")) {
            val error = validator(value)
            if (error()) {
                dialog.metadata.body[0] = message(value, error)
                dialog.metadata.inputs[0] = input(value)

                player.sendPacket(ShowDialogPacket(dialog))
                return false
            }

            player.closeDialog()
            action(value)
            return true
        } else if (event.key == Key.key("playground.visualize")) {
            dialog.metadata.body[0] = message(value)
            dialog.metadata.inputs[0] = input(value)

            player.sendPacket(ShowDialogPacket(dialog))
            return false
        } else if (event.key == Key.key("playground.cancel")) {
            player.closeDialog()
            player.err("Input cancelled")
            return true
        }
        return false
    }

    override fun toDialog(): Dialog.MultiAction {
        return Dialog.MultiAction(
            DialogMetadata(
                colorize("<primary>${title}</primary>"),
                null,
                true,
                false,
                DialogAfterAction.NONE,
                mutableListOf<DialogBody>(
                    message(previous?:"")
                ),
                mutableListOf<DialogInput>(
                    input(previous?:""),
                )
            ),
            mutableListOf(
                button("playground.cancel", "Cancel", 100),
            ).apply {
                if (visualizer != null) {
                    add(button("playground.visualize", "Visualize", 100))
                }
                add(button("playground.submit_input", "Submit", 100))
            },
            null,
            2
        )
    }

    companion object {
        val defaultVisualizer = { input: String ->
            colorize("<primary>$input")
        }
        val expressionVisualizer = { input: String ->
            colorize("<error>${input.styleExpression()}")
        }
        val mcFiveVisualizer = { input: String ->
            val lines = wrapMcFiveLoreLines(colorize(input), 160)
            var comp = Component.empty()
            for ((i, line) in lines.withIndex()) {
                var str = serialize(line).trim()
                if (i == 3) {
                    str += "..."
                }
                comp =
                    comp.append(colorize("<font:playground:mcfive>${str}</font>\n").shadowColor(null))
            }
            comp
        }
    }
}