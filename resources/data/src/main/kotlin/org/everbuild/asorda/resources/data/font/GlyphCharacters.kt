package org.everbuild.asorda.resources.data.font

import org.everbuild.asorda.resources.data.api.ContentList
import org.everbuild.asorda.resources.data.api.texture.Texture

val mcFiveCharWidths = hashMapOf(
        "A" to 5, "B" to 5, "C" to 5, "D" to 5, "E" to 5, "F" to 5, "G" to 5, "H" to 5, "I" to 4, "J" to 5, "K" to 5, "L" to 5, "M" to 5, "N" to 5, "O" to 5, "P" to 5, "Q" to 5, "R" to 5, "S" to 5, "T" to 5, "U" to 5, "V" to 5, "W" to 5, "X" to 5, "Y" to 5, "Z" to 5,
        "a" to 5, "b" to 5, "c" to 5, "d" to 5, "e" to 5, "f" to 5, "g" to 5, "h" to 5, "i" to 2, "j" to 5, "k" to 5, "l" to 2, "m" to 5, "n" to 5, "o" to 5, "p" to 5, "q" to 5, "r" to 4, "s" to 5, "t" to 5, "u" to 5, "v" to 5, "w" to 5, "x" to 5, "y" to 5, "z" to 5,
        "0" to 5, "1" to 4, "2" to 5, "3" to 5, "4" to 5, "5" to 5, "6" to 5, "7" to 5, "8" to 5, "9" to 5,
        "!" to 2, "@" to 5, "#" to 5, "$" to 5, "%" to 5, "^" to 4, "&" to 5, "*" to 4, "(" to 3, ")" to 3, "_" to 5, "+" to 4, "[" to 3, "]" to 3, "{" to 4, "}" to 4, "|" to 2, ";" to 2, ":" to 2, "'" to 2, "," to 2, "." to 2, "<" to 4, ">" to 4, "?" to 5, "/" to 5, "~" to 5, "`" to 3
    )

fun mcFiveCalcWidth(str: String): Int {
    return str.sumOf { mcFiveCharWidths[it.toString()] ?: 6 } + (str.length - 1) // +1 for the space between characters}
}

object GlyphCharacters : ContentList() {
    val empty = createBitmapCharacter("empty") {
        texture(Texture("font/utils/empty"))
        height(20)
        ascent(0)
    }

    val return_glyph = createBitmapCharacter("return") {
        texture(Texture("font/glyphs/return"))
        height(8)
        ascent(7)
    }
}