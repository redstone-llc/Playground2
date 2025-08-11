package llc.redstone.playground.utils

import llc.redstone.playground.feature.evalex.styleExpression
import net.kyori.adventure.key.Key
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.nbt.StringBinaryTag
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.dialog.*
import net.minestom.server.dialog.DialogAction.DynamicCustom
import net.minestom.server.dialog.DialogBody.PlainMessage
import net.minestom.server.entity.Player
import net.minestom.server.event.EventListener
import net.minestom.server.event.player.PlayerChatEvent
import net.minestom.server.event.player.PlayerCommandEvent
import net.minestom.server.event.player.PlayerCustomClickEvent
import net.minestom.server.instance.Instance
import net.minestom.server.network.packet.server.common.ShowDialogPacket


data class TempStorageForListener(
    var listener: EventListener<PlayerChatEvent>?,
    var listener2: EventListener<PlayerCommandEvent>?
) {
    fun remove() {
        MinecraftServer.getGlobalEventHandler().removeListener(listener ?: return)
        MinecraftServer.getGlobalEventHandler().removeListener(listener2 ?: return)
    }
}

class DialogBuilder {
    var title: String = "Input Dialog"
    var context: String? = null
    var message: String = "Enter your desired value ${
        if (context.isNullOrBlank()) "" else "for <success>$context</success>"
    } in the box below:"
    var previous: String = ""
    var expression: Boolean = false
    var visualizer: ((String) -> Component)? = null
    var width = 200;
    var validator: (String) -> Error = { error }
    var action: (String) -> Unit = {}

    companion object {
        val defaultVisualizer = { input: String ->
            colorize("<primary>$input")
        }
        val expressionVisualizer = { input: String ->
            colorize("<error>${input.styleExpression()}")
        }
    }
}

//String, Expression
fun Player.openDialogInput(
    dbInit: DialogBuilder.() -> Unit,
) {
    val db = DialogBuilder().apply(dbInit)
    fun input(input: String): DialogInput.Text {
        return DialogInput.Text(
            "input",
            db.width,
            Component.empty(),
            false,
            input,
            1024,
            null
        )
    }

    fun message(input: String, error: Error = noError): PlainMessage {
        var comp = Component.empty()
        if (db.visualizer != null) {
            comp = comp.append(colorize("<yellow>Current input:\n")).append(db.visualizer!!(input).append(text("\n\n")))
        }
        comp = comp.append(colorize("<green>${db.message}"))
        if (error()) {
            comp = comp.append(colorize("\n\n<error>${error}</error>"))
        }

        return PlainMessage(comp, 200)
    }

    val dialog = Dialog.MultiAction(
        DialogMetadata(
            colorize("<primary>${db.title}</primary>"),
            null,
            true,
            false,
            DialogAfterAction.NONE,
            mutableListOf<DialogBody>(
                message(db.previous)
            ),
            mutableListOf<DialogInput>(
                input(db.previous),
            )
        ),
        mutableListOf(
            DialogActionButton(
                text("Cancel"),
                null,
                100,
                DynamicCustom(Key.key("playground.cancel"), CompoundBinaryTag.builder().build())
            )
        ).apply {
            if (db.visualizer != null) {
                add(
                    DialogActionButton(
                        text("Visualize"),
                        null,
                        100,
                        DynamicCustom(Key.key("playground.visualize"), CompoundBinaryTag.builder().build())
                    )
                )
            }
            add(
                DialogActionButton(
                    text("Submit"),
                    null,
                    100,
                    DynamicCustom(Key.key("playground.submit_input"), CompoundBinaryTag.builder().build())
                )
            )
        },
        null,
        2
    )

    val eventListener = addListener(PlayerCustomClickEvent::class.java) { it, listener ->
        val value = ((it.payload as CompoundBinaryTag)["input"] as StringBinaryTag).value();
        if (it.key == Key.key("playground.submit_input")) {
            val error = db.validator(value)
            if (error()) {
                dialog.metadata.body[0] = message(value, error)
                dialog.metadata.inputs[0] = input(value)

                this.sendPacket(ShowDialogPacket(dialog))
                return@addListener
            }

            this.closeDialog()
            db.action(value)
            MinecraftServer.getGlobalEventHandler().removeListener(listener)
        } else if (it.key == Key.key("playground.visualize")) {
            dialog.metadata.body[0] = message(value)
            dialog.metadata.inputs[0] = input(value)

            this.sendPacket(ShowDialogPacket(dialog))
        } else if (it.key == Key.key("playground.cancel")) {
            this.closeDialog()
            MinecraftServer.getGlobalEventHandler().removeListener(listener)
            this.err("Input cancelled")
        }
    }

    MinecraftServer.getGlobalEventHandler().addListener(PlayerCustomClickEvent::class.java) {

    }

    this.sendPacket(ShowDialogPacket(dialog))
}

fun Player.openChat(
    previous: String,
    context: String? = null,
    action: (String) -> Unit,
) {
    this.closeInventory()

    var clazz = TempStorageForListener(null, null)

    checkNotNull(primaryColor)
    this.info(
        "Enter your desired value ${
            if (context.isNullOrBlank()) " " else "for <color:${
                brightness(
                    primaryColor!!,
                    VARIABLE_BRIGHTNESS_FACTOR
                ).asHexString()
            }>$context</color>"
        } in chat.\n" +
                " <click:suggest_command:'${previous}'><hover:show_text:'<aqua>Click to paste previous value'><aqua>[PREVIOUS]</aqua></hover></click>" +
                " <click:run_command:'/cancelinput'><hover:show_text:'<red>Click to cancel'><red>[CANCEL]</red></hover></click>"
    )

    var listener = EventListener.of(PlayerChatEvent::class.java) { event ->
        if (event.player != this) return@of
        event.isCancelled = true
        action(event.rawMessage)
        clazz.remove()
    }
    var listener2 = EventListener.of(PlayerCommandEvent::class.java) { event ->
        if (event.player != this) return@of
        event.isCancelled = true
        clazz.remove()
        this.err("Input cancelled")
        action(previous)
    }

    clazz.listener = listener
    clazz.listener2 = listener2

    MinecraftServer.getGlobalEventHandler().addListener(listener)
    MinecraftServer.getGlobalEventHandler().addListener(listener2)
}

/**
 * Teleports the player to the position if they are already in the instance instead of throwing an exception.
 * @see Player.setInstance
 * @see Player.teleport
 */
fun Player.setInstanceSafe(
    instance: Instance,
    position: Pos?
) {
    if (this.instance == instance) {
        this.teleport(position ?: this.position)
    } else {
        this.setInstance(instance, position ?: this.position)
    }
}