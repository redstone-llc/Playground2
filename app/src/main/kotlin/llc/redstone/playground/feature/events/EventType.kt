package llc.redstone.playground.feature.events

import llc.redstone.playground.menu.PItem
import net.minestom.server.event.player.PlayerBlockBreakEvent
import net.minestom.server.event.player.PlayerEntityInteractEvent
import net.minestom.server.event.trait.PlayerEvent
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.Material

enum class EventType(
    val clazz: Class<out PlayerEvent>,
    val menuItem: PItem,
) {
    BLOCK_BREAK(PlayerBlockBreakEvent::class.java,
        createMenuItem(
            Material.DIAMOND_PICKAXE,
            "Block Break",
            "a block is broken by a player"
        )
    ),
    INTERACT_PLAYER(PlayerEntityInteractEvent::class.java,
        createMenuItem(
            Material.RABBIT_STEW,
            "Player Interact",
            "a player interacts with another player"
        )
    ),

    ;
}

fun createMenuItem(material: Material, name: String, description: String = "<red>No description set!"): PItem {
    return PItem(
        material
    ).name("<green>${name}")
     .description("Execute actions when $description")
}