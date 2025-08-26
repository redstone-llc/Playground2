package llc.redstone.playground.action

import com.ezylang.evalex.data.EvaluationValue
import com.github.shynixn.mccoroutine.minestom.launch
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import llc.redstone.playground.feature.evalex.PGExpression
import llc.redstone.playground.database.Sandbox
import llc.redstone.playground.menu.PItem
import llc.redstone.playground.utils.colorize
import llc.redstone.playground.utils.minecraftServer
import org.everbuild.celestia.orion.core.packs.withEmojis
import org.everbuild.celestia.orion.platform.minestom.api.Mc
import java.lang.reflect.Field

abstract class Action(
    val enum: ActionEnum,
    @Transient val name: String,
    @Transient val description: String,
    @Transient val icon: Material
) {
    var comment = ""

    val properties: List<Pair<Field, ActionProperty<*>>>
        get() {
            val properties = mutableListOf<Pair<Field, ActionProperty<*>>>()
            for (field in this.javaClass.declaredFields) {
                for (property in ActionProperties.entries) {
                    if (field.isAnnotationPresent(property.annotation.java)) {
                        properties.add(Pair(field, property))
                    }
                }
            }
            return properties
        }

    fun List<Pair<Field, ActionProperty<*>>>.withValue(obj: Any?): Pair<Field, ActionProperty<*>> {
        return this.first {
            it.first.isAccessible = true
            val value = it.first.get(this@Action)
            value == obj
        }
    }

    open fun syncExecute(
        entity: Entity?, player: Player?, sandbox: Sandbox, event: Event?,
        expression: (String) -> PGExpression = { PGExpression(it, entity, player, sandbox, event) }
    ): EvaluationValue? {
        minecraftServer.launch {
            execute(entity, player, sandbox, event, expression)
        }
        return null
    }

    abstract suspend fun execute(
        entity: Entity?, player: Player?, sandbox: Sandbox, event: Event?, expression: (String) -> PGExpression = {
            PGExpression(it, entity, player, sandbox, event)
        }
    )

    fun createDisplayItem(): PItem {
        val builder = PItem(ItemStack.of(icon))
        builder.name("<yellow>$name")
        builder.description(comment)
        if (properties.isNotEmpty()) {
            builder.data("<yellow>Settings", "", null)

            for (field in this.javaClass.declaredFields) {
                val displayName = field.getAnnotation(DisplayName::class.java)
                if (displayName != null) {
                    val emoji = displayName.emoji.ifBlank { displayName.value }
                    builder.data(emoji.withEmojis(), field.displayValue(this), colorize(displayName.color).color())
                }
            }
        }
        return builder
    }

    fun createAddDisplayItem(): PItem {
        val builder = PItem(ItemStack.of(icon))
        builder.name("<yellow>$name")
        builder.description(description)
        return builder
    }

    fun comment(comment: String): Action {
        this.comment = comment
        return this
    }

    open fun requiresEvent(): Boolean {
        return false
    }

    fun clone(): Action {
        val cloned = this::class.java.getDeclaredConstructor().newInstance()
        for (field in this.javaClass.declaredFields) {
            field.isAccessible = true
            try {
                field.set(cloned, field.get(this))
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
        cloned.comment = this.comment
        return cloned
    }
}
