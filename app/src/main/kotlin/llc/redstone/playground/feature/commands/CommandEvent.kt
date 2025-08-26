package llc.redstone.playground.feature.commands

import net.minestom.server.event.Event

class CommandEvent(
    val schedule: Command
) : Event