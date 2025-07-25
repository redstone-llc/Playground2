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

class Function(
    var name: String = "",
    var description: String = "",
    icon: Material = Material.MAP,
    var actions: MutableList<Action> = mutableListOf(),
    arguments: MutableList<Pair<FunctionParameterDefinitionBuilder, EvaluationValue.DataType>> = mutableListOf()
) {
    var icon: String = icon.name()
    var arguments: MutableList<Pair<FunctionParameterDefinition, EvaluationValue.DataType>> = arguments.map {
        Pair(it.first.build(), it.second)
    }.toMutableList()
    @Transient val evalFunction = EvalFunction(this.arguments, this)

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
                        Pair(it.first.name, parameterValues[index])
                    }.toTypedArray()
                )
            )
        }

        return executor
    }
}

class EvalFunction(
    variables: MutableList<Pair<FunctionParameterDefinition, EvaluationValue.DataType>>,
    private val func: Function
) : AbstractFunction() {
    init {
        for ((def, type) in variables) {
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