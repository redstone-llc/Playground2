package llc.redstone.playground.utils

import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Collectors
import java.util.stream.Stream


fun String.toTitleCase(): String = this.lowercase().replaceFirstChar { c -> c.titlecase() }
fun String.splitToWords(): String = this.split('_', ' ').joinToString(" ") { it.toTitleCase() }
fun String.isInteger(): Boolean = this.toIntOrNull() != null
fun String.formatCapitalize(): String = this.replace("_", " ").split(" ").joinToString(" ") { it.toTitleCase() }

fun String.isValidIdentifier(): Boolean {
    if (this.isEmpty() || !this[0].isLetter()) return false
    return this.all { it.isLetterOrDigit() || it == '_' || it == '.' }
}

// Thank you https://www.baeldung.com/java-camel-case-title-case-to-words for some of this code
object StringUtils {
    var WORD_FINDER: Pattern = Pattern.compile("(([A-Z]?[a-z]+)|([A-Z]))")

    var STOP_WORDS: Set<String> = Stream.of(
        "a", "an", "the", "and",
        "but", "for", "at", "by", "to", "or"
    ).collect(Collectors.toSet())

    fun randomString(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }

    fun findWordsInMixedCase(text: String): List<String> {
        val matcher: Matcher = WORD_FINDER.matcher(text)
        val words: MutableList<String> = ArrayList()
        while (matcher.find()) {
            words.add(matcher.group(0))
        }
        return words
    }

    private fun capitalizeFirst(word: String): String {
        return (word.substring(0, 1).uppercase(Locale.getDefault())
                + word.substring(1).lowercase(Locale.getDefault()))
    }

    fun mixedCaseToTitleCase(text: String, joiner: String = " "): String {
        val capitalized: MutableList<String> = ArrayList()
        val words: List<String> = findWordsInMixedCase(text)
        for (i in words.indices) {
            val currentWord: String = words[i]
            if (i == 0 ||
                !STOP_WORDS.contains(currentWord.lowercase())
            ) {
                capitalized.add(capitalizeFirst(currentWord));
            }
        }
        return capitalized.joinToString(joiner)
    }
}