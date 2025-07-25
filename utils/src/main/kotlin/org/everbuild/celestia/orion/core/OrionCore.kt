package org.everbuild.celestia.orion.core

import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import org.everbuild.celestia.orion.core.autoconfigure.SharedPropertyConfig
import org.everbuild.celestia.orion.core.chat.ChatImageTextureResolver
import org.everbuild.celestia.orion.core.chatmanager.BadWordConfig
import org.everbuild.celestia.orion.core.packs.OrionPacks
import org.everbuild.celestia.orion.core.platform.OrionPlatform
import org.everbuild.celestia.orion.core.translation.Translator
import org.everbuild.celestia.orion.core.util.globalOrion

@Suppress("LeakingThis")
open class OrionCore<Platform : OrionPlatform>(val platform: Platform) {
    init {
        globalOrion = this
        javaClass.getResourceAsStream("/resources.lock.json")!!.copyTo(File("resources.lock.json").outputStream())

        SharedPropertyConfig
        BadWordConfig
        Translator
        OrionPacks
    }

    val chatTextureResolver = ChatImageTextureResolver(this)

    companion object {
        val backgroundThreadPool: ExecutorService = Executors.newScheduledThreadPool(5)
        val executorService = backgroundThreadPool.asCoroutineDispatcher()
        val scope = CoroutineScope(executorService)
    }
}