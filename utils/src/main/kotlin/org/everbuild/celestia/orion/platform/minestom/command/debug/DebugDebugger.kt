package org.everbuild.celestia.orion.platform.minestom.command.debug

import net.minestom.server.entity.Player

object DebugDebugger : Debugger {
    override val identifier: String = "orion/debug"

    @Debuggable
    fun testArguments(player: Player, int: Int, orionPlayer: Player) {
        player.sendMessage("Got args: $int, ${orionPlayer.name}")
    }
}