package llc.redstone.playground.database

import com.gestankbratwurst.ambrosia.impl.mongodb.collections.MongoMap
import llc.redstone.playground.Playground
import net.minestom.server.instance.InstanceContainer
import llc.redstone.playground.action.Action
import llc.redstone.playground.feature.functions.Function
import llc.redstone.playground.feature.npc.NpcEntity
import llc.redstone.playground.feature.schedules.Schedule
import java.util.concurrent.ConcurrentHashMap

object SandboxManager {
    private const val COL_NAME = "sandboxes"
    private val mongoAmbrosia = Playground.mongoAmbrosia

    private val liveMap: MutableMap<String, Sandbox> = ConcurrentHashMap()
    private val dbMap: MongoMap<String, Sandbox> = mongoAmbrosia.createMapView(COL_NAME, String::class.java, Sandbox::class.java)


    fun createSandbox(name: String, sandboxUUID: String, ownerUUID: String): Sandbox {
        val sandbox = dbMap[sandboxUUID] ?: Sandbox(name, sandboxUUID, ownerUUID).also {
            dbMap.fastPut(sandboxUUID, it)
        }
        liveMap[sandboxUUID] = sandbox
        return sandbox
    }

    fun getAllSandboxes(): List<Sandbox> {
        return dbMap.values.toList()
    }

    fun getAllSandboxes(sandboxes: List<String>): List<Sandbox> {
        return dbMap.values.filter { it.sandboxUUID in sandboxes }.toList()
    }

    fun loadSandbox(sandboxUUID: String): Sandbox? {
        return dbMap[sandboxUUID]?.also { liveMap[sandboxUUID] = it }
    }

    fun saveSandbox(sandbox: Sandbox) {
        dbMap.fastPut(sandbox.sandboxUUID, sandbox)
        liveMap[sandbox.sandboxUUID] = sandbox
    }
}

val default = Sandbox(
    name = "Default Sandbox",
    sandboxUUID = "default-sandbox-uuid",
    ownerUUID = "default-owner-uuid"
)

fun migrateSandbox(sandbox: Sandbox) {
    for (field in Sandbox::class.java.declaredFields) {
        field.isAccessible = true
        if (field.get(sandbox) == null && field.get(default) != null) {
            field.set(sandbox, field.get(default))
            println("Migrated field ${field.name} in sandbox ${sandbox.sandboxUUID}")
        }
    }
    SandboxManager.saveSandbox(sandbox)
}

data class Sandbox(
    val name: String,
    val sandboxUUID: String,
    val ownerUUID: String,
    var events: MutableMap<String, MutableList<Action>> = mutableMapOf(),
    var playerVariables: MutableMap<String, MutableMap<String, Any?>> = mutableMapOf(),
    var globalVariables: MutableMap<String, Any?> = mutableMapOf(),
    var functions: MutableList<Function> = mutableListOf(),
    var schedules: MutableList<Schedule> = mutableListOf(),
    var npcs: MutableList<NpcEntity> = mutableListOf(),
    ) {
    @Transient var instance: InstanceContainer? = null
}