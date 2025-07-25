package llc.redstone.playground.utils

import net.kyori.adventure.text.Component
import net.minestom.server.command.CommandSender
import net.minestom.server.entity.Player

object MessageUtils {
    fun info(message: String): Component {
        checkNotNull(primaryColor)
        return colorize(
            "<color:" + brightness(
                primaryColor,
                ICON_BRIGHTNESS_FACTOR
            ).asHexString() + ">ℹ</color> <primary>" + message
        )
    }

    fun success(message: String): Component {
        checkNotNull(successColor)
        return colorize(
            "<color:" + brightness(
                successColor,
                ICON_BRIGHTNESS_FACTOR
            ).asHexString() + ">⭐</color> <success>" + message
        )
    }

    fun warning(message: String): Component {
        checkNotNull(warningColor)
        return colorize(
            "<color:" + brightness(
                warningColor,
                ICON_BRIGHTNESS_FACTOR
            ).asHexString() + ">⚠</color> <warning>" + message
        )
    }

    fun err(message: String): Component {
        checkNotNull(errorColor)
        return colorize(
            "<color:" + brightness(
                errorColor,
                ICON_BRIGHTNESS_FACTOR
            ).asHexString() + ">❌</color> <error>" + message
        )
    }
}

fun Player.send(message: String): Component {
    val msg: Component = colorize(message)
    this.sendMessage(msg)
    return msg
}

fun CommandSender.send(message: String): Component {
    val msg: Component = colorize(message)
    this.sendMessage(msg)
    return msg
}

fun Player.info(message: String) {
    this.sendMessage(MessageUtils.info(message))
}

fun Player.success(message: String) {
    this.sendMessage(MessageUtils.success(message))
}

fun Player.warning(message: String) {
    this.sendMessage(MessageUtils.warning(message))
}

fun Player.err(message: String) {
    this.sendMessage(MessageUtils.err(message))
}

fun CommandSender.info(message: String) {
    this.sendMessage(MessageUtils.info(message))
}

fun CommandSender.success(message: String) {
    this.sendMessage(MessageUtils.success(message))
}

fun CommandSender.warning(message: String) {
    this.sendMessage(MessageUtils.warning(message))
}

fun CommandSender.err(message: String) {
    this.sendMessage(MessageUtils.err(message))
}