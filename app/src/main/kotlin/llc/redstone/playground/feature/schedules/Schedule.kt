package llc.redstone.playground.feature.schedules

import llc.redstone.playground.action.Action
import llc.redstone.playground.action.ActionExecutor
import llc.redstone.playground.action.ActionExecutor.ActionScope.*
import llc.redstone.playground.database.Sandbox
import llc.redstone.playground.menu.PItem
import llc.redstone.playground.utils.toTitleCase
import net.minestom.server.MinecraftServer
import net.minestom.server.item.Material
import net.minestom.server.timer.Scheduler
import net.minestom.server.timer.Task
import net.minestom.server.timer.TaskSchedule
import net.minestom.server.utils.time.TimeUnit.*
import java.time.temporal.TemporalUnit


class Schedule(
    var name: String,
    var scope: ActionExecutor.ActionScope,
    var description: String = "",
    var actions: MutableList<Action> = mutableListOf(),
    var duration: Long = 5L,
    var unit: TimeUnit = TimeUnit.SECONDS,
) {
    @Transient
    var task: Task? = null

    fun createScheduleTask(sandbox: Sandbox): Task {
        stopSchedule()

        val scheduler: Scheduler = MinecraftServer.getSchedulerManager()

        return scheduler.submitTask {
            when (scope) {
                SANDBOX -> {
                    ActionExecutor(null, null, sandbox, null, SANDBOX, actions).execute()
                }
                ENTITY -> {
                    sandbox.instance!!.entities.forEach { entity ->
                        ActionExecutor(entity, null, sandbox, null, ENTITY, actions).execute()
                    }
                }
                PLAYER -> {
                    sandbox.instance!!.players.forEach { player ->
                        ActionExecutor(player, player, sandbox, null, PLAYER, actions).execute()
                    }
                }
            }

            TaskSchedule.duration(duration, unit.unit)
        }
    }

    fun stopSchedule() {
        task?.cancel()
        task = null
    }

    fun createDisplayItem(): PItem {
        return PItem(Material.COMMAND_BLOCK)
            .name(
                when (scope) {
                    SANDBOX -> "<green>" // Run once
                    ENTITY -> "<blue>" // Run for each entity in the instance
                    PLAYER -> "<aqua>" // Run for each player in the instance
                } + name.toTitleCase()
            )
            .info(scope.name.toTitleCase() + " Schedule")
            .description(description)
            .data("Actions", "${actions.size} actions", null)
            .data("Duration", "$duration ${unit.display}", null)
    }
}

enum class TimeUnit(
    val unit: TemporalUnit,
    val display: String
) {
    MILLISECONDS(MILLISECOND, "ms"),
    TICKS(SERVER_TICK, "ticks"),
    SECONDS(SECOND, "s"),
    MINUTES(MINUTE, "min"),
    HOURS(HOUR, "h"),
    ;
}