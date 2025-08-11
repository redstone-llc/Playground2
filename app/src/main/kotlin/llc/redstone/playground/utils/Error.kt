package llc.redstone.playground.utils

class Error(
    var message: String? = null,
    var value: Boolean = false
): Comparable<Boolean> {
    override fun compareTo(other: Boolean): Int {
        return if (value == other) 0 else if (value) 1 else -1
    }

    operator fun invoke(): Boolean {
        return value
    }

    override fun toString(): String {
        return if (value) "$message" else "No Error"
    }
}

fun error(message: String, value: Boolean = true): Error {
    return Error(message, value)
}
val error = Error("An error occurred", true)
val noError = Error("No error", false)