package llc.redstone.playground.feature.npc.menu

import net.minestom.server.entity.Player
import llc.redstone.playground.feature.npc.NpcEntity
import llc.redstone.playground.menu.PaginationMenu
import llc.redstone.playground.database.Sandbox
import llc.redstone.playground.menu.PItem
import llc.redstone.playground.utils.PaginationList

class NpcPropertiesMenu(
    private val npc: NpcEntity,
    private val sandbox: Sandbox
) : PaginationMenu("Properties", NpcEditMenu(npc, sandbox)) {
    override fun list(player: Player): MutableList<PItem> {
        return npc.properties.map {
            val item = it.second.getDisplayItem(npc, it.first)
            item.leftClick { event -> it.second.runnable(it.first, npc, event, sandbox, player, this) }
            item
        }.toMutableList()
    }
}