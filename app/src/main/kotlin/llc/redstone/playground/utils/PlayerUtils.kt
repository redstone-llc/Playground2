package llc.redstone.playground.utils

import llc.redstone.playground.feature.evalex.styleExpression
import llc.redstone.playground.utils.dialogs.DialogBuilder
import llc.redstone.playground.utils.dialogs.MultiSelectDialogBuilder
import llc.redstone.playground.utils.dialogs.TextDialogBuilder
import llc.redstone.playground.utils.dialogs.get
import llc.redstone.playground.utils.dialogs.getString
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

//String, Expression
fun Player.openTextInput(
    dbInit: TextDialogBuilder.() -> Unit,
) {
    val db = TextDialogBuilder().apply(dbInit)

    val dialog = db.toDialog()

    addListener(PlayerCustomClickEvent::class.java) { it, listener ->
        if (it.player != this) return@addListener

        // Handle the input
        if (db.handleInput(it.player, dialog, it)) {
            MinecraftServer.getGlobalEventHandler().removeListener(listener)
        }
    }

    this.sendPacket(ShowDialogPacket(dialog))
}

fun Player.openMultiInput(
    dbInit: MultiSelectDialogBuilder.() -> Unit,
) {
    val db = MultiSelectDialogBuilder().apply(dbInit)

    val dialog = db.toDialog()

    addListener(PlayerCustomClickEvent::class.java) { it, listener ->
        if (it.player != this) return@addListener

        // Handle the input
        if (db.handleInput(it.player, dialog, it)) {
            MinecraftServer.getGlobalEventHandler().removeListener(listener)
        }
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