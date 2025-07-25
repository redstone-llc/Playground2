package org.everbuild.celestia.orion.platform.minestom.platform

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerDisconnectEvent
import org.everbuild.celestia.orion.core.chat.Textures
import org.everbuild.celestia.orion.core.platform.OrionPlatform
import org.everbuild.celestia.orion.core.luckperms.LuckPermsData
import org.everbuild.celestia.orion.core.platform.PlatformIndependentJoinEvent
import org.everbuild.celestia.orion.core.platform.Teleportation
import org.everbuild.celestia.orion.core.platform.TeleportationFactory
import org.everbuild.celestia.orion.platform.minestom.globalServer
import org.everbuild.celestia.orion.platform.minestom.luckperms.MinestomLuckPermsData
import org.everbuild.celestia.orion.platform.minestom.util.listen
import org.everbuild.celestia.orion.platform.minestom.util.toPos
import java.net.URI
import java.util.*
import net.minestom.server.event.player.AsyncPlayerPreLoginEvent
import org.everbuild.celestia.orion.platform.minestom.api.Mc

class MinestomOrionPlatform : OrionPlatform {
    override fun registerJoinEvent(handler: (PlatformIndependentJoinEvent) -> Unit) {
        listen<AsyncPlayerPreLoginEvent> {
            handler(
                PlatformIndependentJoinEvent(
                    playerName = it.gameProfile.name,
                    playerUUID = it.gameProfile.uuid,
                    playerLocale = "en"
                )
            )
        }
    }

    override fun registerLeaveEvent(handler: (Player) -> Unit) {
        listen<PlayerDisconnectEvent> {
            handler(it.player)
        }
    }

    override fun getAdventureAudience(player: Player): Audience = player
    override fun getLuckPermsData(player: Player): LuckPermsData = MinestomLuckPermsData(player)
    override fun isOnline(player: Player): Boolean =
        MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(player.uuid) != null

    override fun isProxy(): Boolean = false
    override fun getLogicalMaxPlayerCount(): Long = globalServer.maxPlayers.toLong()
    override fun isReady(): Boolean = globalServer.online
    override fun getPlayers(): List<Player> = MinecraftServer.getConnectionManager().onlinePlayers.map { it }

    override fun executeServerCommand(command: String) {
        MinecraftServer.getCommandManager().executeServerCommand(command)
    }

    override fun executeCommandAs(player: Player, command: String) {
        // not supported
    }

    override fun sendTo(player: Player, to: String) {
        // not supported
    }

    override fun teleportationFactory(): TeleportationFactory = MinestomTeleportationFactory()

    override fun teleport(player: Player, teleportation: Teleportation) {
        player.teleport(teleportation.toLocation().toPos())
    }

    override fun texture(player: Player): URI? {
        val texs = Textures.de(player.skin?.textures() ?: return null) ?: return null
        val tex = texs.textures.getOrDefault("SKIN", null) ?: return null
        return URI(tex.url)
    }
}