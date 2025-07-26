package llc.redstone.playground.database

import com.gestankbratwurst.ambrosia.impl.mongodb.collections.MongoMap
import llc.redstone.playground.Playground
import net.minestom.server.instance.InstanceContainer
import llc.redstone.playground.action.Action
import llc.redstone.playground.feature.functions.Function
import llc.redstone.playground.feature.npc.NpcEntity
import java.util.concurrent.ConcurrentHashMap

object SandboxInstanceManager {
    private const val COL_NAME = "sandboxInstances"
    private val mongoAmbrosia = Playground.mongoAmbrosia

    private val liveMap: MutableMap<String, SandboxInstance> = ConcurrentHashMap()
    private val dbMap: MongoMap<String, SandboxInstance> = mongoAmbrosia.createMapView(COL_NAME, String::class.java, SandboxInstance::class.java)


    fun createSandbox(sandboxUUID: String, instanceArray: ByteArray): SandboxInstance {
        val sandbox = SandboxInstance(sandboxUUID, instanceArray)
        dbMap[sandboxUUID] = sandbox
        liveMap[sandboxUUID] = sandbox
        return sandbox
    }

    fun loadSandbox(sandboxUUID: String): SandboxInstance? {
        return dbMap[sandboxUUID]?.also { liveMap[sandboxUUID] = it }
    }

    fun getSandbox(sandboxUUID: String): SandboxInstance? {
        return liveMap[sandboxUUID] ?: dbMap[sandboxUUID]?.also { liveMap[sandboxUUID] = it }
    }

    fun updateSandbox(sandboxUUID: String, instanceArray: ByteArray) {
        val sandbox = liveMap[sandboxUUID] ?: return
        val updatedSandbox = SandboxInstance(sandbox.sandboxUUID, instanceArray)
        liveMap[sandboxUUID] = updatedSandbox
        dbMap[sandboxUUID] = updatedSandbox
    }
}

data class SandboxInstance(
    val sandboxUUID: String,
    val instanceArray: ByteArray,
    ) {
}