package llc.redstone.playground.feature.variables

import llc.redstone.playground.feature.evalex.PGExpression
import llc.redstone.playground.database.Sandbox


fun Sandbox.getVar(key: String): Any? {
    return this.globalVariables.getOrElse(key) { 0 }
}

fun Sandbox.setVar(key: String, value: PGExpression) {
    this.globalVariables[key] = value.evaluate().value
}

fun Sandbox.listVars(): Set<String> {
    return this.globalVariables.keys.toSet()
}