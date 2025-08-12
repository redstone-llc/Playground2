@file:JvmName("PaletteConv")

package llc.redstone.playground.utils

import com.catppuccin.Palette
import kotlin.math.*
import java.awt.*
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.File

data class RGB(val r: Int, val g: Int, val b: Int)
data class HSL(val h: Double, val s: Double, val l: Double)

fun clamp01(x: Double) = x.coerceIn(0.0, 1.0)

fun hexToRgb(hex: String): RGB {
    val s = hex.trim().removePrefix("#")
    require(s.length == 6) { "Hex must be 6 chars like A3B5C8" }
    val r = s.substring(0, 2).toInt(16)
    val g = s.substring(2, 4).toInt(16)
    val b = s.substring(4, 6).toInt(16)
    return RGB(r, g, b)
}

fun rgbToHex(rgb: RGB): String = "%02X%02X%02X".format(rgb.r, rgb.g, rgb.b)

fun rgbToHsl(rgb: RGB): HSL {
    val r = rgb.r / 255.0
    val g = rgb.g / 255.0
    val b = rgb.b / 255.0
    val max = max(r, max(g, b))
    val min = min(r, min(g, b))
    val l = (max + min) / 2.0
    val d = max - min
    val s = if (d == 0.0) 0.0 else d / (1.0 - abs(2 * l - 1.0))
    val h = when {
        d == 0.0 -> 0.0
        max == r -> ((g - b) / d + (if (g < b) 6 else 0)) / 6.0
        max == g -> ((b - r) / d + 2) / 6.0
        else     -> ((r - g) / d + 4) / 6.0
    }
    return HSL(h, s, l)
}

fun hslToRgb(hsl: HSL): RGB {
    val h = (hsl.h % 1.0 + 1.0) % 1.0
    val s = clamp01(hsl.s)
    val l = clamp01(hsl.l)

    fun hue2rgb(p: Double, q: Double, tIn: Double): Double {
        var t = tIn
        if (t < 0) t += 1.0
        if (t > 1) t -= 1.0
        return when {
            t < 1.0 / 6.0 -> p + (q - p) * 6.0 * t
            t < 1.0 / 2.0 -> q
            t < 2.0 / 3.0 -> p + (q - p) * (2.0 / 3.0 - t) * 6.0
            else -> p
        }
    }

    val (r, g, b) = if (s == 0.0) {
        Triple(l, l, l)
    } else {
        val q = if (l < 0.5) l * (1 + s) else l + s - l * s
        val p = 2 * l - q
        Triple(
            hue2rgb(p, q, h + 1.0 / 3.0),
            hue2rgb(p, q, h),
            hue2rgb(p, q, h - 1.0 / 3.0)
        )
    }
    return RGB((r * 255).roundToInt(), (g * 255).roundToInt(), (b * 255).roundToInt())
}

/**
 * Palette rules (same as before, tuned to hit your A3B5C8 mapping):
 *  - Light:  S += 0.057, L += 0.11
 *  - Dark:   H += 10°,   S -= 0.09,  L -= 0.23
 *  - Accent: H += 8°,    S -= 0.01,  L -= 0.095
 */
fun makePalette(hex: String): List<String> {
    val baseHsl = rgbToHsl(hexToRgb(hex))

    fun adjust(hDeg: Double, sAdd: Double, lAdd: Double): String {
        val h = baseHsl.h + hDeg / 360.0
        val s = clamp01(baseHsl.s + sAdd)
        val l = clamp01(baseHsl.l + lAdd)
        return rgbToHex(hslToRgb(HSL(h, s, l)))
    }

    val light  = adjust(0.0,  +0.057, +0.11)
    val dark   = adjust(10.0, -0.09,  -0.23)
    val accent = adjust(8.0,  -0.01,  -0.095)
    return listOf(light, dark, accent)
}

fun drawPalettePng(
    colors: List<Pair<com.catppuccin.Pair<String, com.catppuccin.Color>, List<String>>>,
    file: File = File("LATTE.png")
) {
    val height = 18 * colors.size + 2 // 16px per color w/ padding + 2px for top/bottom border
    val width = 90
    val img = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val g = img.createGraphics()

    // Rendering hints for nicer text/edges
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

    var y = 1
    colors.forEach { (pair, colors) ->
        val width = 16
        val height = 16
        val inputHex = pair.value().hex()
        val colorName = pair.key()

        // Draw the color name text
        g.color = Color.WHITE
        g.drawString(colorName, 18, y + 12)

        // Background is inputHex
        g.color = Color.decode("#$inputHex")
        g.fillRect(1, y, width, height)

        // Out-most border is color index 1
        g.color = Color.decode("#${colors[1]}")
        g.drawRect(1, y, width - 1, height - 1)

        //Starting at 1,1 draw an outline that is 14x13
        g.color = Color.decode("#${colors[0]}")
        g.drawRect(2, y + 1, width - 3, height - 4)

//    //Draw a line from 1,14 to 14,14 in color index 2
        g.color = Color.decode("#${colors[2]}")
        g.drawLine(2, y + 14, width - 1, y + 14)

        y += 18
    }


    g.dispose()
    ImageIO.write(img, "png", file)
}

fun main() {

    val test = Palette.LATTE.toList()!!.map { pair ->
        val key = pair.key()
        val value = pair.value()

        val pal = makePalette(value.hex())
//        println("hex: ${value.hex()} -> $pal")
        Pair(pair, pal)
    }

    drawPalettePng(
        test,
        File("LATTE.png")
    )

    return

    val input = readLine()?: return
    val palette = makePalette(input)


    println("input: ${input.uppercase()}")
    println("output: ${palette.joinToString(", ")}")

}
