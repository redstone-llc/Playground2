package llc.redstone.playground.managers

import llc.redstone.playground.action.ActionExecutor
import llc.redstone.playground.database.Sandbox
import llc.redstone.playground.database.SandboxInstanceManager
import llc.redstone.playground.database.SandboxManager
import llc.redstone.playground.database.migrateSandbox
import llc.redstone.playground.feature.events.EventType
import llc.redstone.playground.feature.housingMenu.PlaygroundMenu
import llc.redstone.playground.sandbox.createTemplatePlatform
import llc.redstone.playground.utils.success
import net.hollowcube.polar.*
import net.minestom.server.MinecraftServer
import net.minestom.server.advancements.AdvancementAction
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerEntityInteractEvent
import net.minestom.server.event.player.PlayerPacketEvent
import net.minestom.server.event.player.PlayerPacketOutEvent
import net.minestom.server.event.player.PlayerPickBlockEvent
import net.minestom.server.event.player.PlayerRespawnEvent
import net.minestom.server.event.trait.PlayerEvent
import net.minestom.server.instance.IChunkLoader
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.LightingChunk
import net.minestom.server.network.ConnectionState
import net.minestom.server.network.packet.client.play.ClientAdvancementTabPacket
import java.nio.file.Path
import java.util.*

/**
 * Map of currently loaded sandboxes by sandbox UUID
 */
val loadedSandboxes = mutableMapOf<String, Sandbox>()

/**
 * Creates a new sandbox with the specified owner
 */
fun createSandbox(player: Player): Sandbox {
    try {
        var sandbox = SandboxManager.createSandbox(
            player.username + "'s Sandbox",
            UUID.randomUUID().toString(),
            player.uuid.toString(),
        )

        loadedSandboxes[sandbox.sandboxUUID] = sandbox
        sandbox.instance = createInstance(PolarLoader(PolarWorld()))
        sandbox.instance?.let { instance ->
            createTemplatePlatform(instance)
            instance.saveChunksToStorage()
        }

        SandboxInstanceManager.createSandbox(
            sandbox.sandboxUUID,
            PolarWriter.write((sandbox.instance!!.chunkLoader as PolarLoader).world())
        )

        return sandbox

    } catch (e: Exception) {
        e.printStackTrace()
        MinecraftServer.LOGGER.error("Error creating sandbox for player ${player.username}", e)
        throw RuntimeException("Failed to create sandbox for player ${player.username}", e)
    }
}

/**
 * Loads the instance data from database
 */
fun Sandbox.loadInstance() {
    val sbInstance = SandboxInstanceManager.loadSandbox(this.sandboxUUID) ?: return
    this.instance = createInstance(PolarLoader(PolarReader.read(sbInstance.instanceArray)))

    this.commands.forEach { it.registerCommand(this) }
    this.schedules.forEach { it.createScheduleTask(this) }
}

fun createInstance(chunkLoader: IChunkLoader): InstanceContainer {
    val instanceManager = MinecraftServer.getInstanceManager()
    val instanceContainer = instanceManager.createInstanceContainer()

    instanceContainer.chunkLoader = chunkLoader
    instanceContainer.setChunkSupplier(::LightingChunk)

    return instanceContainer
}

fun Sandbox.spawnNPCs() {
    for (npc in this.npcs) {
        if (npc.spawned) continue
        npc.spawn(this)
    }
}

fun Sandbox.startEventListener() {
    var handler = MinecraftServer.getGlobalEventHandler();
    val node: EventNode<PlayerEvent> = EventNode.value(sandboxUUID, EventFilter.PLAYER) {
        it.instance == instance
    }
    handler.addChild(node)
    for (event in EventType.entries) {
        node.addListener(event.clazz) { e: PlayerEvent ->
            val actions = this.events[event.name] ?: return@addListener
            ActionExecutor(
                if (e is PlayerEntityInteractEvent) e.target else e.entity,
                if (e is PlayerEntityInteractEvent) e.player else e.entity,
                this,
                e,
                if (e is PlayerEntityInteractEvent) ActionExecutor.ActionScope.ENTITY else ActionExecutor.ActionScope.PLAYER,
                actions
            ).execute()
        }
    }
    node.addListener(PlayerRespawnEvent::class.java) { e ->
        if (e.player.instance == instance) {
            e.respawnPosition = Pos(0.0, 61.0, 0.0)
        }
    }
    node.addListener(PlayerPickBlockEvent::class.java) {
        handlePickBlock(it)
    }
    node.addListener(PlayerPacketEvent::class.java) { e ->
        val packet = e.packet
        if (packet !is ClientAdvancementTabPacket) {
            return@addListener
        }
        if (packet.action == AdvancementAction.OPENED_TAB) PlaygroundMenu().open(e.player)
    }
}

/**
 * Saves the instance data to database
 */
fun Sandbox.saveInstance() {
    instance?.saveChunksToStorage() ?: return // Ensure instance is not null before saving
    val chunkLoader = instance?.chunkLoader ?: return // Ensure chunk loader is not null before saving
    val instanceArray = PolarWriter.write((chunkLoader as PolarLoader).world())

    SandboxInstanceManager.updateSandbox(sandboxUUID, instanceArray)
}

fun Sandbox.save() {
    saveInstance()

    SandboxManager.saveSandbox(
        this
    )
}

fun loadSandboxes(sandboxes: List<String>) {
    val sandboxes = SandboxManager.getAllSandboxes(sandboxes)
    for (sandbox in sandboxes) {
        loadedSandboxes[sandbox.sandboxUUID] = sandbox
        migrateSandbox(sandbox)
        sandbox.loadInstance()
    }
}

fun saveAllSandboxes() {
    for (sandbox in loadedSandboxes.values) {
        sandbox.save()
    }
}

fun Player.getSandbox(): Sandbox? {
    return loadedSandboxes.values.firstOrNull { it.instance == this.instance }
}