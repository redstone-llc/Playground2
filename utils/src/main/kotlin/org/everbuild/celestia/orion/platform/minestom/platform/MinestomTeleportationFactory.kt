package org.everbuild.celestia.orion.platform.minestom.platform

import net.minestom.server.entity.Player
import org.everbuild.celestia.orion.core.platform.Teleportation
import org.everbuild.celestia.orion.core.platform.TeleportationFactory
import org.everbuild.celestia.orion.core.util.FullPosition
import org.everbuild.celestia.orion.platform.minestom.util.toFull

class MinestomTeleportationFactory : TeleportationFactory {
    override fun to(player: Player): Teleportation {
        return object : Teleportation {
            override fun toLocation(): FullPosition = player.position.toFull()
        }
    }

    override fun to(pos: FullPosition): Teleportation {
        return object : Teleportation {
            override fun toLocation(): FullPosition = pos
        }
    }
}