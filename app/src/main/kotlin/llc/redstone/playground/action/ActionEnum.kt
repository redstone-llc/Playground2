package llc.redstone.playground.action

import llc.redstone.playground.action.ActionCategory.*
import llc.redstone.playground.action.actions.*

enum class ActionEnum(
    val id: String,
    val clazz: Class<out Action>,
    val category: ActionCategory = MISC, // Default category is MISC
    val isVisible: (Action) -> Boolean = { true } // Default predicate to always return true
) {
    CANCEL_EVENT("cancel_event", CancelEvent::class.java),
    CHAT_MESSAGE("chat_action", ChatMessage::class.java, PLAYER),
    ACTIONBAR("actionbar", ActionBar::class.java, PLAYER),
    POTION_EFFECT("potion_effect", PotionEffect::class.java, PLAYER),
    DAMAGE("damage", Damage::class.java, ENTITY),
    CHANGE_VELOCITY("change_velocity", ChangeVelocity::class.java, ENTITY),
    WAIT("wait", WaitAction::class.java),
    PLAYER_VAR("player_var", PlayerVar::class.java, PLAYER),
    GLOBAL_VAR("global_var", GlobalVar::class.java, SANDBOX),

    FUNCTION_RETURN("return", Return::class.java),
    ;

    companion object {
        fun fromId(id: String): ActionEnum? {
            return entries.firstOrNull { it.id == id }
        }
    }
}

enum class ActionCategory() {
    PLAYER, ENTITY, SANDBOX, MISC, ALL
}