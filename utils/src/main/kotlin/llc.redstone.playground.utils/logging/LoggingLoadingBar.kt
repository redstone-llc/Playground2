package llc.redstone.playground.utils.logging

import java.util.function.Consumer

class LoggingLoadingBar(
    initialMessage: String,
    private val out: Consumer<String>
) : LoadingBar {

    companion object {
        private val PROGRESS_BAR_WIDTH = System.getProperty("vri.loadingBarWidth", "20").toInt()
        private val MESSAGE_COLOR = Color.valueOf(System.getProperty("vri.loadingBarMessageColor", "BLUE_BOLD"))
    }

    private var message: String = initialMessage
    private var progress: Double = 0.0
    private val updater: StatusUpdater = UpdaterImpl()
    private val depth: Int = 0

    override fun subTask(task: String): LoadingBar = SubTaskLoadingBar(this, task, depth + 1)
    override fun updater(): StatusUpdater = updater
    override fun message(): String = message

    private fun renderThis() {
        out.accept("\r")
        render(message, progress, out)
    }

    private fun render(message: String, progress: Double, out: Consumer<String>) {
        val sb = StringBuilder()
        sb.append(MESSAGE_COLOR)
        sb.append(message)
        sb.append(" ")
        accumulate(progress * PROGRESS_BAR_WIDTH, PROGRESS_BAR_WIDTH.toDouble(), sb)
        sb.append(" ")
        out.accept(sb.toString())
    }

    private fun accumulate(width: Double, total: Double, out: StringBuilder) {
        out.append(Color.RESET)
        out.append(Color.BLUE_BOLD)
        out.append("|")
        out.append(Color.RESET)
        out.append(Color.CYAN)
        var remaining = total - width
        var w = width
        while (w > 1) {
            w -= 1
            out.append("=")
        }
        out.append(">")
        out.append(Color.RESET)
        out.append(Color.WHITE_UNDERLINED)
        while (remaining > 1) {
            remaining -= 1
            out.append(" ")
        }
        out.append(Color.RESET)
        out.append(Color.BLUE_BOLD)
        out.append("|")
        out.append(Color.RESET)
    }

    private inner class UpdaterImpl : StatusUpdater {
        @Synchronized
        override fun progress(progress: Double) {
            if (this@LoggingLoadingBar.progress != progress) {
                this@LoggingLoadingBar.progress = progress
                renderThis()
            }
        }

        @Synchronized
        override fun message(message: String) {
            if (this@LoggingLoadingBar.message != message) {
                this@LoggingLoadingBar.message = message
                renderThis()
            }
        }

        @Synchronized
        override fun message(message: String, progress: Double) {
            if (this@LoggingLoadingBar.message != message &&
                this@LoggingLoadingBar.progress != progress
            ) {
                this@LoggingLoadingBar.message = message
                this@LoggingLoadingBar.progress = progress
                renderThis()
            }
        }
    }

    inner class SubTaskLoadingBar(
        private val parent: LoadingBar,
        private var message: String,
        private val depth: Int
    ) : LoadingBar {

        private var progress: Double = 0.0
        private val updater: StatusUpdater = UpdaterImpl()

        override fun subTask(task: String): LoadingBar = SubTaskLoadingBar(this, task, depth + 1)

        private fun printIndent(bar: LoadingBar) {
            if (bar is SubTaskLoadingBar) {
                printIndent(bar.parent)
            } else {
                out.accept(Color.YELLOW_BRIGHT.toString())
            }
            out.accept("|   ")
        }

        private fun renderThis() {
            out.accept("\r")
            printIndent(parent)
            out.accept(MESSAGE_COLOR.toString())
            render(message, progress, out)
        }

        override fun updater(): StatusUpdater = updater
        override fun message(): String = message

        private inner class UpdaterImpl : StatusUpdater {
            override fun progress(progress: Double) {
                if (this@SubTaskLoadingBar.progress != progress) {
                    this@SubTaskLoadingBar.progress = progress
                    renderThis()
                }
            }

            override fun message(message: String) {
                if (this@SubTaskLoadingBar.message != message) {
                    this@SubTaskLoadingBar.message = message
                    renderThis()
                }
            }

            override fun message(message: String, progress: Double) {
                if (this@SubTaskLoadingBar.message != message &&
                    this@SubTaskLoadingBar.progress != progress
                ) {
                    this@SubTaskLoadingBar.progress = progress
                    this@SubTaskLoadingBar.message = message
                    renderThis()
                }
            }
        }
    }
}