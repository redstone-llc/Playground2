package llc.redstone.playground.utils.logging

import java.io.OutputStream
import java.io.PrintStream
import java.util.*
import java.util.regex.Pattern

class LoggerImpl(private val level: Level) : Logger {
    companion object {
        val LOG_LEVEL: Level = Level.valueOf(System.getenv("minestom.vri.log-level")?.uppercase() ?: "SETUP")
        val DEFAULT = LoggerImpl(LOG_LEVEL)

        private var lastLogger: LoggerImpl = DEFAULT
        private var loggerWasLast: Boolean = true
        private var newLine: Boolean = true
        private val printLock = Any()
        private val sysOut: PrintStream = System.out

        init {
            System.setOut(object : PrintStream(object : OutputStream() {
                override fun write(b: Int) {
                    synchronized(printLock) {
                        if (loggerWasLast) {
                            loggerWasLast = false
                            if (!newLine) lastLogger.newLine()
                        }
                        sysOut.write(b)
                    }
                }
            }, false) {})
        }
    }

    override fun level(level: Level): Logger {
        return if (this.level == level) this else LoggerImpl(level)
    }

    private fun consolePrint(str: String) {
        sysOut.print(str)
        loggerWasLast = true
        lastLogger = this
    }

    private fun newLine() {
        consolePrint(System.lineSeparator())
        newLine = true
    }

    override fun print(message: String): Logger {
        synchronized(printLock) {
            if (LOG_LEVEL.ordinal > level.ordinal) return this
            if (loggerWasLast && lastLogger != this && !newLine) {
                newLine()
            }
            if (newLine) {
                consolePrint(preparePrefix())
                newLine = false
            }
            val lines = message.split(Pattern.quote(System.lineSeparator()).toRegex())
            if (lines.size == 1 && message != System.lineSeparator()) {
                printNonNewLine(message)
                return this
            }
            for ((i, line) in lines.withIndex()) {
                if (i != 0) newLine()
                if (line.isNotEmpty()) print(line)
            }
            return this
        }
    }

    private fun printNonNewLine(message: String) {
        if (!message.contains("\r")) {
            consolePrint(message)
            return
        }
        val split = message.split("\r")
        consolePrint("\r")
        consolePrint(preparePrefix())
        consolePrint(split.last())
    }

    private fun preparePrefix(): String {
        val date = Calendar.getInstance()
        val seconds = date.get(Calendar.SECOND)
        val minutes = date.get(Calendar.MINUTE)
        val hours = date.get(Calendar.HOUR_OF_DAY)
        val day = date.get(Calendar.DAY_OF_MONTH)
        val month = date.get(Calendar.MONTH) + 1
        val year = date.get(Calendar.YEAR)
        return "%s[%s/%s/%s %s:%s:%02d]%s %s -> ".format(
            Color.GREEN,
            day, month, year,
            hours, minutes, seconds,
            Color.RESET,
            prepareLevelPrefix()
        )
    }

    override fun nextLine(): Logger {
        synchronized(printLock) {
            if (LOG_LEVEL.ordinal < level.ordinal) return this
            if (!newLine) newLine()
            return this
        }
    }

    private fun prepareLevelPrefix(): String {
        val color = when (level) {
            Level.TRACE -> Color.WHITE
            Level.DEBUG -> Color.BLUE
            Level.SETUP -> Color.MAGENTA
            Level.INFO -> Color.CYAN
            Level.WARN -> Color.YELLOW
            Level.ERROR -> Color.RED_BOLD_BRIGHT
        }
        return "%s(%s)%s".format(color, level, Color.RESET)
    }

    override fun printf(message: String, vararg args: Any?): Logger {
        return print(message.format(*args))
    }

    override fun throwable(throwable: Throwable, vararg args: Any?): Logger {
        val info = when {
            args.size == 1 -> " -> (${args[0]})"
            args.size > 1 -> " -> (${args[0].toString().format(args[1])})"
            else -> ""
        }
        return print(throwable.message + info)
    }

    override fun level(): Level = level

    override fun equals(other: Any?): Boolean {
        return other is LoggerImpl && level == other.level
    }

    override fun hashCode(): Int = level.hashCode()
}