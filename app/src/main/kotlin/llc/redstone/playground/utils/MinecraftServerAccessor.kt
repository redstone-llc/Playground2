package llc.redstone.playground.utils

import com.github.shynixn.mccoroutine.minestom.addSuspendingListener
import kotlinx.coroutines.asCoroutineDispatcher
import llc.redstone.playground.managers.saveAllSandboxes
import llc.redstone.playground.menu.DefaultItems
import llc.redstone.playground.utils.logging.Logger
import net.minestom.server.MinecraftServer
import net.minestom.server.event.Event
import net.minestom.server.event.EventListener
import net.minestom.server.event.EventNode
import net.minestom.server.event.trait.PlayerEvent
import xyz.xenondevs.invui.InvUI
import xyz.xenondevs.invui.gui.structure.Structure

lateinit var minecraftServer: MinecraftServer
val minecraftDispatcher = MinecraftServer.getSchedulerManager().asCoroutineDispatcher()

fun initMinecraftServer(server: MinecraftServer) {
    minecraftServer = server
    // Additional initialization logic can be added here if needed
    InvUI.getInstance()
//    Structure.addGlobalIngredient('#', DefaultItems.INV_PART)

    MinecraftServer.getSchedulerManager().buildShutdownTask {
        Logger.info("Shutting down...")
        saveAllSandboxes()
    }
}

class EventList<E : Event>(
    private val eventType: Class<out Event>,
    private val listener: (E, EventListener<E>) -> Unit
): EventListener<E> {
    override fun eventType(): Class<E> {
        return eventType as Class<E>
    }

    override fun run(event: E): EventListener.Result {
        return try {
            listener(event, this)
            EventListener.Result.SUCCESS
        } catch (e: Exception) {
            Logger.error("Error in event listener for ${eventType.simpleName}", e)
            EventListener.Result.EXCEPTION
        }
    }
}

fun <E : Event> addListener(
    eventType: Class<out E>,
    listener: (E, EventListener<E>) -> Unit
): EventListener<E> {
    val eventNode = MinecraftServer.getGlobalEventHandler()
    val eventListener = EventList(eventType, listener)
    eventNode.addListener(eventListener)
    return eventListener
}

fun <E : Event> EventNode<E>.susListener(
    eventType: Class<out E>,
    listener: suspend (E) -> Unit
) {
    this.addSuspendingListener(minecraftServer, eventType) { listener(it) }
}