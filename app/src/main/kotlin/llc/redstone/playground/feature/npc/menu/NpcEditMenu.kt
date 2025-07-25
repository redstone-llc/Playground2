package llc.redstone.playground.feature.npc.menu

import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.Material
import llc.redstone.playground.feature.npc.NpcEntity
import llc.redstone.playground.feature.npc.events.NpcEventActionsMenu
import llc.redstone.playground.menu.Menu
import llc.redstone.playground.menu.menuItem
import llc.redstone.playground.database.Sandbox

class NpcEditMenu(
    private val npc: NpcEntity,
    private val sandbox: Sandbox
) : Menu(
    "Edit NPC: ${npc.name}",
    InventoryType.CHEST_6_ROW
) {
    override fun setupItems(player: Player) {
        addItem(center - 9, npc.distinctMenuItem())
        addItem(center + 9 - 2,
            menuItem(Material.GRASS_BLOCK) {
            NpcEventActionsMenu(npc, sandbox).open(player)
        }.name("<green>Event Actions")
            .description("Click to edit npc event actions")
            .action(ClickType.LEFT_CLICK, "to edit")
        )
        addItem(center + 9,
            menuItem(Material.REDSTONE) {
                NpcPropertiesMenu(npc, sandbox).open(player)
            }.name("<green>Properties")
                .description("Click to edit npc properties")
                .action(ClickType.LEFT_CLICK, "to edit")
        )
    }
}