package com.amanjeet.ktoon.util

import com.amanjeet.ktoon.util.Constants.COLON
import com.amanjeet.ktoon.util.Constants.FALSE_LITERAL
import com.amanjeet.ktoon.util.Constants.LIST_ITEM_MARKER
import com.amanjeet.ktoon.util.Constants.NULL_LITERAL
import com.amanjeet.ktoon.util.Constants.TRUE_LITERAL

/**
 * Validates strings for safe unquoted usage in TOON format.
 * Uses guard clauses and early returns for clarity.
 */
object StringValidator {
    private val NUMERIC_PATTERN = Regex("^-?\\d+(?:\\.\\d+)?(?:e[+-]?\\d+)?$", RegexOption.IGNORE_CASE)
    private val OCTAL_PATTERN = Regex("^0\\d+$")
    private val UNQUOTED_KEY_PATTERN = Regex("^[A-Z_][\\w.]*$", RegexOption.IGNORE_CASE)
    private val STRUCTURAL_CHARS = Regex("[\\[\\]{}]")
    private val CONTROL_CHARS = Regex("[\\n\\r\\t]")

    /**
     * Checks if a string can be safely written without quotes.
     * Uses guard clauses and early returns for clarity.
     */
    fun isSafeUnquoted(value: String, delimiter: String): Boolean {
        if (isNullOrEmpty(value)) return false
        if (isPaddedWithWhitespace(value)) return false
        if (looksLikeKeyword(value)) return false
        if (looksLikeNumber(value)) return false
        if (containsColon(value)) return false
        if (containsQuotesOrBackslash(value)) return false
        if (containsStructuralCharacters(value)) return false
        if (containsControlCharacters(value)) return false
        if (containsDelimiter(value, delimiter)) return false
        if (startsWithListMarker(value)) return false
        return true
    }

    /**
     * Checks if a key can be used without quotes.
     */
    fun isValidUnquotedKey(key: String): Boolean =
        UNQUOTED_KEY_PATTERN.matches(key)

    private fun isNullOrEmpty(value: String): Boolean =
        value.isEmpty()

    private fun isPaddedWithWhitespace(value: String): Boolean =
        value != value.trim()

    private fun looksLikeKeyword(value: String): Boolean =
        value == TRUE_LITERAL || value == FALSE_LITERAL || value == NULL_LITERAL

    private fun looksLikeNumber(value: String): Boolean =
        NUMERIC_PATTERN.matches(value) || OCTAL_PATTERN.matches(value)

    private fun containsColon(value: String): Boolean =
        value.contains(COLON)

    private fun containsQuotesOrBackslash(value: String): Boolean =
        value.contains('"') || value.contains('\\')

    private fun containsStructuralCharacters(value: String): Boolean =
        STRUCTURAL_CHARS.containsMatchIn(value)

    private fun containsControlCharacters(value: String): Boolean =
        CONTROL_CHARS.containsMatchIn(value)

    private fun containsDelimiter(value: String, delimiter: String): Boolean =
        value.contains(delimiter)

    private fun startsWithListMarker(value: String): Boolean =
        value.startsWith(LIST_ITEM_MARKER)
}
