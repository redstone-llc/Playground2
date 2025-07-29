package llc.redstone.playground.feature.npc.menu

import net.minestom.server.entity.Player
import net.minestom.server.item.Material
import llc.redstone.playground.feature.npc.NpcEntity
import llc.redstone.playground.feature.npc.events.NpcEventActionsMenu
import llc.redstone.playground.database.Sandbox
import llc.redstone.playground.menu.PItem
import llc.redstone.playground.menu.items.CloseItem
import llc.redstone.playground.utils.colorize
import llc.redstone.playground.menu.invui.NormalMenu
import xyz.xenondevs.invui.gui.Gui

class NpcEditMenu(
    private val npc: NpcEntity,
    private val sandbox: Sandbox
) : NormalMenu(
    colorize("Edit NPC: ${npc.name}"),
) {

    override fun initTopGUI(player: Player): Gui {
        return Gui.normal()
            .setStructure(
                "# # # # # # # # #",
                "# # # # i # # # #",
                "# # # # # # # # #",
                "# # e # p # # # #",
                "# # # # # # # # #",
                "# # # # b # # # #"
            )
            .addIngredient('b', CloseItem())
            .addIngredient('i', npc.distinctMenuItem().build())
            .addIngredient(
                'e', PItem(Material.GRASS_BLOCK)
                    .name("<green>Event Actions")
                    .description("Click to edit npc event actions")
                    .leftClick("edit") { _, _ ->
                        NpcEventActionsMenu(npc, sandbox).open(player)
                        null
                    }.build()
            )
            .addIngredient(
                'p', PItem(Material.REDSTONE)
                    .name("<green>Properties")
                    .description("Click to edit npc properties")
                    .leftClick("edit properties") { _, _ ->
                        NpcPropertiesMenu(npc, sandbox).open(player)
                        null
                    }.build()
            )
            .build()
    }
}