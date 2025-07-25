package llc.redstone.playground.feature.evalex

import com.catppuccin.Palette
import com.ezylang.evalex.config.ExpressionConfiguration
import com.ezylang.evalex.parser.Token.TokenType.*
import com.ezylang.evalex.parser.Tokenizer
import llc.redstone.playground.utils.color

fun String.styleExpression(config: ExpressionConfiguration = expressionConfiguration): String {
    try {
        val tokens = Tokenizer(this, config).parse()
        val flavor = Palette.MACCHIATO //TODO: Make this configurable

        return tokens.map { token ->
            token.apply {
                when (type) {
                    NUMBER_LITERAL, ARRAY_INDEX -> {
                        return@map value.color(flavor.peach().hex()) + " "
                    }

                    STRING_LITERAL -> {
                        return@map "\"$value\"".color(flavor.green().hex())
                    }

                    FUNCTION -> {
                        return@map value.color(flavor.blue().hex())
                    }

                    FUNCTION_PARAM_START -> {
                        return@map value.color(flavor.maroon().hex())
                    }

                    INFIX_OPERATOR, PREFIX_OPERATOR, POSTFIX_OPERATOR -> {
                        return@map value.color(flavor.sky().hex()) + " "
                    }

                    BRACE_OPEN, ARRAY_OPEN -> {
                        return@map value.color(flavor.overlay2().hex())
                    }

                    BRACE_CLOSE, ARRAY_CLOSE -> {
                        return@map value.color(flavor.overlay2().hex()) + " "
                    }


                    else -> {
                        return@map "<white>$value</white> "
                    }
                }
            }
        }.joinToString("")
    } catch (e: Exception) {
        // If the expression is invalid, return it as is
        return this
    }
}