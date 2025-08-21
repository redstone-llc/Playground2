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

class MultiSelectDialogBuilder : DialogBuilder<String, Dialog.MultiAction>() {
    var options = mutableListOf<String>()
    override fun handleInput(player: Player, dialog: Dialog.MultiAction, event: PlayerCustomClickEvent): Boolean {
        val key = event.key.asString()
        if (key.startsWith("playground:multi_select_")) {
            val value = key.removePrefix("playground:multi_select_")
            if (value in options) {
                player.closeDialog()
                action(value)
                return true
            } else {
                player.sendMessage(colorize("<error>Invalid option selected.</error>"))
                return false
            }
        } else if (key == "playground:cancel") {
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
                    message(previous ?: "")
                ),
                mutableListOf<DialogInput>()
            ),
            options.map {
                button("playground:multi_select_${it}", it)
            }.plus(
                listOf(
                    button("playground:blank", " "),
                    button("playground:cancel", "Cancel")
                )
            ),
            null,
            1
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