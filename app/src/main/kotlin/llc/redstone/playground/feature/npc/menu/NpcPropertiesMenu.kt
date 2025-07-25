package llc.redstone.playground.feature.npc.menu

import net.minestom.server.entity.Player
import llc.redstone.playground.feature.npc.NpcEntity
import llc.redstone.playground.menu.Backable
import llc.redstone.playground.menu.MenuItem
import llc.redstone.playground.menu.PaginationMenu
import llc.redstone.playground.database.Sandbox
import llc.redstone.playground.utils.PaginationList

class NpcPropertiesMenu(
    private val npc: NpcEntity,
    private val sandbox: Sandbox
): PaginationMenu("Properties"), Backable {
    override fun paginationList(player: Player): PaginationList<MenuItem>? {
        return PaginationList(
            npc.properties.map {
                val item = it.second.getDisplayItem(npc, it.first)
                item.action = { event -> it.second.runnable(it.first, npc, event, sandbox, player, this) }
                item
            },
            slots.size
        )
    }

    override fun back(player: Player) {
        NpcEditMenu(
            npc,
            sandbox
        ).open(player)
    }

    override fun backName(player: Player): String {
        return "Edit NPC: ${npc.name}"
    }
}