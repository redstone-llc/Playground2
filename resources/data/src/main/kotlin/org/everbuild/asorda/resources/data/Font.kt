package org.everbuild.asorda.resources.data

import org.everbuild.asorda.resources.data.api.meta.ResourcePackMetadata
import org.everbuild.asorda.resources.data.api.Track
import org.everbuild.asorda.resources.data.api.minecraftKey
import org.everbuild.asorda.resources.data.font.GlyphCharacters
import org.everbuild.asorda.resources.data.font.MenuCharacters
import org.everbuild.asorda.resources.data.font.createSpacingProvider
import team.unnamed.creative.ResourcePack
import team.unnamed.creative.font.Font

suspend fun addFontData(pack: ResourcePack, metadata: ResourcePackMetadata) {
    val codepointTrack = Track.char('\ue000')

    val spacing = createSpacingProvider(-160..160, codepointTrack, metadata)

    val elements = listOf(
        spacing,
        *MenuCharacters.getSegments(pack, codepointTrack, metadata).toTypedArray(),
        *GlyphCharacters.getSegments(pack, codepointTrack, metadata).toTypedArray()
    )

    pack.font(Font.font(minecraftKey("default"), elements))
}
