package org.everbuild.celestia.orion.core.platform

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import org.everbuild.celestia.orion.core.luckperms.LuckPermsData
import java.net.URI

interface OrionPlatform {
    fun registerJoinEvent(handler: (PlatformIndependentJoinEvent) -> Unit)
    fun registerLeaveEvent(handler: (Player) -> Unit)
    fun getAdventureAudience(player: Player): Audience
    fun getLuckPermsData(player: Player): LuckPermsData
    fun isOnline(player: Player): Boolean
    fun isProxy(): Boolean
    fun getLogicalMaxPlayerCount(): Long
    fun isReady(): Boolean
    fun getPlayers(): List<Player>
    fun executeServerCommand(command: String)
    fun executeCommandAs(player: Player, command: String)
    fun sendTo(player: Player, to: String)
    fun teleportationFactory(): TeleportationFactory
    fun teleport(player: Player, teleportation: Teleportation)
    fun texture(player: Player): URI?
}