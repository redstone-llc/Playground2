package org.everbuild.asorda.resources.data.items

import org.everbuild.asorda.resources.data.api.ContentList
import org.everbuild.asorda.resources.data.api.item.ItemResource
import org.everbuild.asorda.resources.data.api.texture.Texture
import team.unnamed.creative.base.CubeFace
import team.unnamed.creative.base.Vector3Float
import team.unnamed.creative.model.Element
import team.unnamed.creative.model.ElementFace
import team.unnamed.creative.model.ItemTransform
import team.unnamed.creative.model.Model
import team.unnamed.creative.texture.TextureUV

object FunctionIcons : ContentList("function") {
    val allSelected = items("all")
    val playerSelected = items("player")
    val entitySelected = items("entity")
    val sandboxSelected = items("sandbox")
    val miscSelected = items("misc")

    val addArgument = basicItem("add_argument")
    val selectedArgument = items("selected_argument", 18, 18, 0f, 0f)

    private fun basicItem(name: String) =
        createItem(name) {
            model(createModel {
                parent(Model.ITEM_GENERATED)
                textures("layer0" to Texture("items/function/$name"))
            })
        }

    private fun items(
        name: String,
        width: Int = 18,
        height: Int = 16,
        tX: Float = -1f,
        tY: Float = 0f
    ): ItemResource {
        return createItem(name) {
            val texture = Texture("items/function/$name")
            val relWidth = width - 16
            val relHeight = height - 16
            model(createModel {
//                parent(Model.ITEM_GENERATED)
                textures("layer0" to texture)
                guiLight(Model.GuiLight.FRONT)
                ambientOcclusion(false)
                display(
                    ItemTransform.Type.GUI to ItemTransform.transform(
                        Vector3Float(0.0f, 0.0f, 0.0f),
                        Vector3Float(tX, tY, 0.0f),
                        Vector3Float(1f, 1f, 1f)
                    )
                )
                elements(
                    Element.element()
                        .from(Vector3Float((relWidth / 2) * -1f, (relHeight / 2) * -1f, 0f))     // stretched geometry
                        .to(Vector3Float(16f + (relWidth / 2), 16f + (relHeight / 2), 0f))       // still a flat quad (same Z)
                        .faces(
                            mapOf(
                                CubeFace.SOUTH to ElementFace.face()
                                    .uv(TextureUV.uv(0f, 0f, 1f, 1f)) // DO NOT use 18 here
                                    .texture("#layer0")                 // reference the key
                                    .build()
                            )
                        )
                        .build()
                )
            })
        }
    }

    fun getIcon(category: String): ItemResource {
        return when (category) {
            "ALL" -> allSelected
            "PLAYER" -> playerSelected
            "ENTITY" -> entitySelected
            "SANDBOX" -> sandboxSelected
            "MISC" -> miscSelected
            else -> allSelected // Default to all if category is unknown
        }
    }

}