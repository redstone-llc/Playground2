@file:JvmName("PaletteGen")

package llc.redstone.playground.utils

import com.catppuccin.Flavor
import com.catppuccin.Palette
import kotlin.math.*
import java.awt.*
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.File

val catppuccinPalettes = listOf(
    Palette.LATTE,
    Palette.FRAPPE,
    Palette.MACCHIATO,
    Palette.MOCHA,
)

fun hex(hex: String): Color {
    if (hex.startsWith("#")) {
        return Color.decode(hex)
    }
    return Color.decode("#$hex")
}

fun Int.hexString(): String {
    return String.format("#%06X", 0xFFFFFF and this)
}

@OptIn(ExperimentalStdlibApi::class)
fun convertCatppuccinPalette(
    image: BufferedImage,
    from: Flavor,
    to: Flavor
): BufferedImage {
    val width = image.width
    val height = image.height
    val result = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

    // Assuming Palette exposes a map of color names to hex values
    val fromColors = hashMapOf(*from.toList().map { it.key() to "#${it.value().hex()}" }.toTypedArray()) // Map<String, String>
    val toColors = hashMapOf(*to.toList().map { it.key() to "#${it.value().hex()}"}.toTypedArray()) // Map<String, String>

    // Build a mapping from fromPalette RGB to toPalette RGB
    val colorMap = fromColors.keys.associate { name ->
        println("Mapping color $name: ${fromColors[name]} -> ${toColors[name]}")
        val fromRgb = Color.decode(fromColors[name])
        val toRgb = Color.decode(toColors[name])
        fromRgb.rgb to toRgb.rgb
    }.toMutableMap()

    val map = colorMap.toMap()
    for ((key, value) in map) {
        val keyColors = makePalette(key.hexString())
        val valueColors = makePalette(value.hexString())
        println("$keyColors -> $valueColors")
        for ((i, keyColor) in keyColors.withIndex()) {
            colorMap.put(hex(keyColor).rgb, hex(valueColors[i]).rgb)
        }
    }

    for (y in 0 until height) {
        for (x in 0 until width) {
            val pixel = image.getRGB(x, y)
            val newPixel = colorMap[pixel] ?: pixel
            result.setRGB(x, y, newPixel)
        }
    }
    return result
}

fun main() {
    print("Enter the file path of the image to convert: ")
    val file = File(readLine()?:return)
    if (!file.exists()) {
        println("File does not exist.")
        return
    }
    val image = ImageIO.read(file)
    print("Enter the name of the Catppuccin palette to convert from (latte, frappe, macchiato, mocha): ")
    val fromInput = readLine() ?: return
    val from = catppuccinPalettes.find { it.name().equals(fromInput, true) } ?: run {
        println("Invalid palette name.")
        return
    }
    print("Enter the name of the Catppuccin palette to convert to (latte, frappe, macchiato, mocha): ")
    val toInput = readLine() ?: return
    val to = catppuccinPalettes.find { it.name().equals(toInput, true) } ?: run {
        println("Invalid palette name.")
        return
    }

    val convertedImage = convertCatppuccinPalette(image, from, to)
    val outputFile = File("converted_${file.nameWithoutExtension}_${to.name().lowercase()}.${file.extension}")
    ImageIO.write(convertedImage, file.extension, outputFile)
    println("Converted image saved to ${outputFile.absolutePath}")
}
