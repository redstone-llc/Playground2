package llc.redstone.playground.utils.logging

class LoadingImpl private constructor(
    val parent: LoadingImpl?,
    private val loadingBar: LoadingBar?,
    var level: Level,
) : Loading {
    companion object {
        @JvmStatic
        var CURRENT: LoadingImpl = LoadingImpl(null, null, Level.INFO)
    }

    private val started: Long = System.currentTimeMillis()
    private val progress: Double = 0.0

    @Synchronized
    fun waitTask(name: String) {
        Logger.logger().level(level).nextLine()
        val loading = if (loadingBar == null) {
            LoadingImpl(this, LoadingBar.logger(name, Logger.logger().level(level)), level)
        } else {
            LoadingImpl(this, loadingBar.subTask(name), level)
        }
        CURRENT = loading
    }

    @Synchronized
    fun finishTask() {
        if (loadingBar == null) throw IllegalStateException("Cannot finish root task")
        loadingBar.updater().progress(1.0)
        requireNotNull(parent)
        Logger.logger().level(parent.level).printf("took %dms%n", System.currentTimeMillis() - started)
        CURRENT = parent
    }

    @Synchronized
    fun getUpdater(): StatusUpdater {
        if (loadingBar == null) throw IllegalStateException("Cannot get updater for root task")
        return loadingBar.updater()
    }
}