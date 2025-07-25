package llc.redstone.playground.utils

import com.ezylang.evalex.data.EvaluationValue
import com.ezylang.evalex.functions.FunctionParameterDefinition
import net.minestom.server.item.Material

object VariableUtils {
    fun argTypeToMaterial(type: EvaluationValue.DataType): Material? {
        return when (type) {
            EvaluationValue.DataType.STRING -> Material.STRING
            EvaluationValue.DataType.NUMBER -> Material.ANVIL
            EvaluationValue.DataType.BOOLEAN -> Material.LIME_DYE
            EvaluationValue.DataType.ARRAY -> Material.WRITABLE_BOOK
            else -> null
        }
    }

    fun FunctionParameterDefinition.toBuilder(): FunctionParameterDefinition.FunctionParameterDefinitionBuilder {
        return FunctionParameterDefinition.builder()
            .name(this.name)
            .isLazy(this.isLazy)
            .isVarArg(this.isVarArg)
            .nonNegative(this.isNonNegative)
            .nonZero(this.isNonZero)
    }
}