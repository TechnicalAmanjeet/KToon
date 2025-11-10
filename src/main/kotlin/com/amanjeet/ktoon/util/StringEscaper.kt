package com.amanjeet.ktoon.util

/**
 * Handles string escaping for TOON format.
 * Escapes special characters that need protection in quoted strings.
 */
object StringEscaper {
    /**
     * Escapes special characters in a string.
     * Handles backslashes, quotes, and control characters.
     *
     * @param value The string to escape
     * @return The escaped string
     */
    fun escape(value: String): String = value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")
}
