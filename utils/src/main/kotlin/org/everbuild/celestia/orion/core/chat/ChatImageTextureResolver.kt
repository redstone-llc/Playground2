package org.everbuild.celestia.orion.core.chat

import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import org.everbuild.celestia.orion.core.OrionCore

class ChatImageTextureResolver(private val orionCore: OrionCore<*>) {
    private val cache = mutableMapOf<String, CachedChatTexture>()

    fun getSkin(orionPlayer: Player): CachedChatTexture? {
        return cache.getOrPut(orionPlayer.uuid.toString()) { CachedChatTexture(
            orionCore.platform.texture(orionPlayer) ?: return null,
            listOf(
                ChatTexViewBox(8, 8),
                ChatTexViewBox(40, 8),
            )
        ) }
    }

    fun skinComponent(orionPlayer: Player): Component {
        val skin = getSkin(orionPlayer) ?: return Component.empty()
        return skin.chatText
    }
}