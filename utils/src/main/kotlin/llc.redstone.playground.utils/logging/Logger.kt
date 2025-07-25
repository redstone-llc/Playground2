package llc.redstone.playground.utils.logging

interface Logger {
    companion object {
        fun logger(): Logger = LoggerImpl.DEFAULT

        fun debug(): Logger = logger().level(Level.DEBUG)
        fun debug(message: String, vararg args: Any?): Logger =
            if (args.isEmpty()) debug().println(message) else debug().printf(message, *args).println()
        fun debug(throwable: Throwable, vararg args: Any?): Logger = debug().throwable(throwable, *args)

        fun setup(): Logger = logger().level(Level.SETUP)
        fun setup(message: String, vararg args: Any?): Logger =
            if (args.isEmpty()) setup().println(message) else setup().printf(message, *args)
        fun setup(throwable: Throwable, vararg args: Any?): Logger = setup().throwable(throwable, *args)

        fun info(): Logger = logger().level(Level.INFO)
        fun info(message: String, vararg args: Any?): Logger =
            if (args.isEmpty()) info().println(message) else info().printf(message, *args).println()
        fun info(throwable: Throwable, vararg args: Any?): Logger = info().throwable(throwable, *args)

        fun warn(): Logger = logger().level(Level.WARN)
        fun warn(message: String, vararg args: Any?): Logger =
            if (args.isEmpty()) warn().println(message) else warn().printf(message, *args)
        fun warn(throwable: Throwable, vararg args: Any?): Logger = warn().throwable(throwable, *args)

        fun error(): Logger = logger().level(Level.ERROR)
        fun error(message: String, vararg args: Any?): Logger =
            if (args.isEmpty()) error().println(message) else error().printf(message, *args)
        fun error(throwable: Throwable, vararg args: Any?): Logger = error().throwable(throwable, *args)
    }

    fun level(level: Level): Logger
    fun level(): Level

    fun print(message: String): Logger
    fun println(message: String): Logger = print(message).println()
    fun println(): Logger = print(System.lineSeparator())
    fun printf(message: String, vararg args: Any?): Logger
    fun throwable(throwable: Throwable, vararg args: Any?): Logger

    fun nextLine(): Logger
}