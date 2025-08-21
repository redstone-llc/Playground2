package org.everbuild.asorda.resources.data

import net.kyori.adventure.key.Key
import org.everbuild.asorda.resources.data.api.meta.ResourcePackMetadata
import org.everbuild.asorda.resources.data.api.Track
import org.everbuild.asorda.resources.data.api.maybeAsordaKey
import org.everbuild.asorda.resources.data.api.minecraftKey
import org.everbuild.asorda.resources.data.api.pathKey
import org.everbuild.asorda.resources.data.api.resource
import org.everbuild.asorda.resources.data.api.texture.Texture
import org.everbuild.asorda.resources.data.api.withFileType
import org.everbuild.asorda.resources.data.font.GlyphCharacters
import org.everbuild.asorda.resources.data.font.MenuCharacters
import org.everbuild.asorda.resources.data.font.createSpacingProvider
import team.unnamed.creative.ResourcePack
import team.unnamed.creative.base.Vector2Float
import team.unnamed.creative.font.Font
import team.unnamed.creative.font.FontProvider

suspend fun addFontData(pack: ResourcePack, metadata: ResourcePackMetadata) {
    val codepointTrack = Track.char('\ue000')

    val spacing = createSpacingProvider(-260..160, codepointTrack, metadata)

    val elements = listOf(
        spacing,
        *MenuCharacters.getSegments(pack, codepointTrack, metadata).toTypedArray(),
        *GlyphCharacters.getSegments(pack, codepointTrack, metadata).toTypedArray()
    )

    pack.font(Font.font(minecraftKey("default"), elements))


    pack.unknownFile("assets/playground/font/mc-five-lowercase.ttf", resource("font/mc-five-lowercase.ttf"))
    pack.font(
        Font.font(
            Key.key("playground:mcfive"),
            FontProvider.trueType().size(5.5f).shift(Vector2Float(0f, -1f))
                .file(Key.key("playground:mc-five-lowercase.ttf")).build()
        )
    )

    pack.unknownFile("assets/playground/font/minecraft.ttf", resource("font/minecraft.ttf"))
    val nums = arrayOf(14, 15, 16, 17)
    for (i in nums) {
        pack.font(
            Font.font(
                Key.key("playground:mc_${i}"),
                FontProvider.trueType().size(5.5f).shift(Vector2Float(0f, i * 10f))
                    .file(Key.key("playground:mc-five-lowercase.ttf")).build()
            )
        )
    }
}
