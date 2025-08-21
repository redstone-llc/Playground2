package llc.redstone.playground.action.actions

import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.item.Material
import llc.redstone.playground.action.Action
import llc.redstone.playground.action.ActionEnum
import llc.redstone.playground.action.Description
import llc.redstone.playground.action.DisplayName
import llc.redstone.playground.action.properties.StringPropertyAnnotation
import llc.redstone.playground.feature.evalex.PGExpression
import llc.redstone.playground.feature.variables.getVar
import llc.redstone.playground.feature.variables.setVar
import llc.redstone.playground.database.Sandbox

data class GlobalVar(
    @DisplayName("Name") @Description("The name of the variable to modify.")
    @StringPropertyAnnotation
    val key: String = "Kills",
    @DisplayName("Value") @Description("The value to change the variable to.")
    @StringPropertyAnnotation
    val value: String = "1",
) : Action(
    ActionEnum.GLOBAL_VAR,
    "Change Global Variable",
    "Modify the value of a global variable.",
    Material.FEATHER
) {
    override suspend fun execute(
        entity: Entity?,
        player: Player?,
        sandbox: Sandbox,
        event: Event?,
        expression: (String) -> PGExpression
    ) {
        sandbox.setVar(
            key,
            expression(value).withValues(
                mutableMapOf(
                    "this" to sandbox.getVar(key),
                    "value" to sandbox.getVar(key),
                    "val" to sandbox.getVar(key)
                )
            )
        )
    }
}