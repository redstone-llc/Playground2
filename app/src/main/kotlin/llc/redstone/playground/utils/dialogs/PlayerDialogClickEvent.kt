package llc.redstone.playground.utils.dialogs

import net.kyori.adventure.key.Key
import net.kyori.adventure.nbt.BinaryTag
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.nbt.StringBinaryTag
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerCustomClickEvent

fun PlayerCustomClickEvent.get(key: String): BinaryTag? {
    return (payload as CompoundBinaryTag)[key]
}

fun PlayerCustomClickEvent.getString(key: String): String? {
    return ((payload as CompoundBinaryTag)[key] as StringBinaryTag?)?.value()
}