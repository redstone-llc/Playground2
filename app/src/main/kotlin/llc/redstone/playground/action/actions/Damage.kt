package llc.redstone.playground.action.actions

import net.minestom.server.entity.Entity
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.entity.damage.Damage
import net.minestom.server.event.Event
import net.minestom.server.item.Material
import llc.redstone.playground.action.Action
import llc.redstone.playground.action.ActionEnum
import llc.redstone.playground.action.Description
import llc.redstone.playground.action.DisplayName
import llc.redstone.playground.action.properties.ExpressionPropertyAnnotation
import llc.redstone.playground.action.properties.doubleValue
import llc.redstone.playground.feature.evalex.PGExpression
import llc.redstone.playground.database.Sandbox

data class Damage(
    @DisplayName("Damage") @Description("The amount of damage to deal to the entity.")
    @ExpressionPropertyAnnotation(true)
    val damage: String = "1.0",
) : Action(
    ActionEnum.DAMAGE,
    "Damage",
    "Damages the entity.",
    Material.DIAMOND_SWORD
) {
    override suspend fun execute(
        entity: Entity?,
        player: Player?,
        sandbox: Sandbox,
        event: Event?,
        expression: (String) -> PGExpression
    ) {
        if (entity is LivingEntity) {
            entity.damage(
                Damage.fromEntity(
                    entity,
                    this.doubleValue(expression(damage)).toFloat()
                )
            )
        }
    }
}