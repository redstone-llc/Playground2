package org.everbuild.asorda.resources.data

import org.everbuild.asorda.resources.data.api.resource
import team.unnamed.creative.ResourcePack

suspend fun addMetadata(pack: ResourcePack) {
    pack.packMeta(63, "Playground Pack")
    pack.icon(resource("pack.png"))
}