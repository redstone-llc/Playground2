package llc.redstone.playground.utils.logging

interface LoadingBar {
    companion object {
        fun console(initialMessage: String): LoadingBar =
            LoggingLoadingBar(initialMessage) { print(it) }

        fun logger(initialMessage: String, logger: Logger): LoadingBar =
            LoggingLoadingBar(initialMessage) { logger.print(it) }
    }

    fun subTask(task: String): LoadingBar
    fun updater(): StatusUpdater
    fun message(): String
}