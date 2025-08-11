package llc.redstone.playground.feature.functions

import com.ezylang.evalex.data.EvaluationValue
import com.ezylang.evalex.functions.FunctionParameterDefinition
import com.ezylang.evalex.functions.FunctionParameterDefinition.*
import com.ezylang.evalex.parser.Token
import net.minestom.server.item.Material
import llc.redstone.playground.action.Action
import llc.redstone.playground.action.ActionExecutor
import llc.redstone.playground.feature.evalex.AbstractFunction
import llc.redstone.playground.feature.evalex.PGExpression
import llc.redstone.playground.menu.PItem

class Function(
    var name: String = "",
    var description: String = "",
    icon: Material = Material.MAP,
    var actions: MutableList<Action> = mutableListOf(),
    arguments: MutableList<FunctionParameterDefinitionBuilder> = mutableListOf()
) {
    var icon: String = icon.name()
    var arguments: MutableList<FunctionParameterDefinition> = arguments.map {
        it.build()
    }.toMutableList()
    @Transient val evalFunction = EvalFunction(this.arguments, this)

    fun createDisplayItem(): PItem {
        return PItem(Material.fromKey(icon) ?: Material.MAP)
            .name("<green>$name")
            .description(description)
            .data("Actions", "${actions.size} actions", null)
            .data("", "", null)
            .data("<yellow>Variables", "", null)
            .apply {
                arguments.forEach {
                    this.data("", "<gray>${it.name}", null)
                }
            }
    }

    fun evaluate(
        expression: PGExpression?,
        token: Token?,
        vararg parameterValues: EvaluationValue?
    ): EvaluationValue? {
        val event = FunctionEvent(this)
        val executor = ActionExecutor(
            expression?.entity ?: return EvaluationValue.NULL_VALUE,
            expression.player,
            expression.sandbox,
            event,
            actions,
        ).execute {
            PGExpression(it, expression.entity, expression.player, expression.sandbox, event).withValues(
                hashMapOf(
                    *this.arguments.mapIndexed { index, it ->
                        Pair(it.name, parameterValues[index])
                    }.toTypedArray()
                )
            )
        }

        return executor
    }
}

class EvalFunction(
    variables: MutableList<FunctionParameterDefinition>,
    private val func: Function
) : AbstractFunction() {
    init {
        for (def in variables) {
            functionParameterDefinitions.add(def)
        }
    }

    override fun evaluate(
        expression: PGExpression?,
        token: Token?,
        vararg parameterValues: EvaluationValue?
    ): EvaluationValue {
        return func.evaluate(expression, token, *parameterValues) ?: EvaluationValue.NULL_VALUE
    }
}