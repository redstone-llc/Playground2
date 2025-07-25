package llc.redstone.playground.utils.logging

interface Loading {
    companion object {
        fun start(name: String) {
            LoadingImpl.CURRENT.waitTask(name)
        }
        fun updater(): StatusUpdater {
            return LoadingImpl.CURRENT.getUpdater()
        }
        fun finish() {
            LoadingImpl.CURRENT.finishTask()
        }
        fun level(level: Level) {
            LoadingImpl.CURRENT.level = level
        }

        fun start(name: String, block: (StatusUpdater.() -> Unit)) {
            start(name)
            try {
                block(LoadingImpl.CURRENT.getUpdater())
            } finally {
                finish()
            }
        }
    }
}