package llc.redstone.playground.utils.logging

interface StatusUpdater {
    /**
     * Updates the progress bar without changing the text message.
     * @param progress the progress, between 0 and 1
     */
    fun progress(progress: Double)

    /**
     * Updates the text message without changing the progress bar.
     * @param message the new message
     */
    fun message(message: String)

    fun message(message: String, progress: Double)
}