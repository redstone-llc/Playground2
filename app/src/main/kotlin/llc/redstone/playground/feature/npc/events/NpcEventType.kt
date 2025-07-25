package llc.redstone.playground.feature.npc.events

import llc.redstone.playground.menu.MenuItem
import llc.redstone.playground.menu.menuItem
import net.minestom.server.event.player.PlayerEntityInteractEvent
import net.minestom.server.event.trait.EntityEvent
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.Material

enum class NpcEventType(
    val clazz: Class<out EntityEvent>,
    val menuItem: MenuItem,
    val cooldown: Int = 0
) {
    INTERACT(PlayerEntityInteractEvent::class.java, createMenuItem(Material.BONE, "Interact", "a player has interacted (right clicked) with the NPC"), 100),
//    ATTACK(PrepareAttackEvent::class.java, createMenuItem(Material.DIAMOND_SWORD, "Attack", "an NPC gets hit.")),

    ;
}

fun createMenuItem(material: Material, name: String, description: String = "<red>No description set!"): MenuItem {
    return menuItem(
        material
    ) {

    }.name("<green>${name}")
     .description("Execute actions when $description")
     .action(ClickType.LEFT_CLICK, "to edit")
}