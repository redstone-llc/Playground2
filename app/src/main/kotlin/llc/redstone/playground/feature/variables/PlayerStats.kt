package llc.redstone.playground.feature.variables

import net.minestom.server.entity.Player
import llc.redstone.playground.feature.evalex.PGExpression
import llc.redstone.playground.managers.getSandbox
import llc.redstone.playground.database.Sandbox

fun Player.getVar(key: String, sandbox: Sandbox? = this.getSandbox()): Any? {
    if (sandbox == null) return null
    val playerVars = sandbox.playerVariables.getOrPut(this.uuid.toString()) { mutableMapOf() }
    return playerVars.getOrElse(key) { 0 }
}

fun Player.setVar(key: String, value: PGExpression, sandbox: Sandbox? = this.getSandbox()) {
    if (sandbox == null) return
    val playerVars = sandbox.playerVariables.getOrPut(this.uuid.toString()) { mutableMapOf() }
    playerVars[key] = value.evaluate().value
}

fun Player.listVars(sandbox: Sandbox? = this.getSandbox()): Set<String>? {
    if (sandbox == null) return null
    val playerVars = sandbox.playerVariables.getOrPut(this.uuid.toString()) { mutableMapOf() }
    return playerVars.keys.toSet()
}