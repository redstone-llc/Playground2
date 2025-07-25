package org.everbuild.celestia.orion.core.platform

import net.minestom.server.entity.Player
import org.everbuild.celestia.orion.core.util.FullPosition

interface TeleportationFactory {
    fun to(player: Player): Teleportation
    fun to(pos: FullPosition): Teleportation
}