package com.amanjeet.ktoon.encoder

import com.amanjeet.ktoon.util.Constants.CLOSE_BRACE
import com.amanjeet.ktoon.util.Constants.CLOSE_BRACKET
import com.amanjeet.ktoon.util.Constants.COLON
import com.amanjeet.ktoon.util.Constants.COMMA
import com.amanjeet.ktoon.util.Constants.OPEN_BRACE
import com.amanjeet.ktoon.util.Constants.OPEN_BRACKET

/**
 * Formats headers for arrays and tables in TOON format.
 */
object HeaderFormatter {
    /**
     * Configuration for header formatting.
     *
     * @param length       Array or table length
     * @param key          Optional key prefix
     * @param fields       Optional field names for tabular format
     * @param delimiter    The delimiter being used
     * @param lengthMarker Whether to include # marker before length
     */
    data class HeaderConfig(
        val length: Int,
        val key: String?,
        val fields: List<String>?,
        val delimiter: String,
        val lengthMarker: Boolean
    )

    /**
     * Formats a header for arrays and tables.
     *
     * @param config Header configuration
     * @return Formatted header string
     */
    fun format(config: HeaderConfig): String = buildString {
        appendKeyIfPresent(config.key)
        appendArrayLength(config.length, config.delimiter, config.lengthMarker)
        appendFieldsIfPresent(config.fields, config.delimiter)
        append(COLON)
    }

    /**
     * Legacy method for backward compatibility.
     * Delegates to the data class-based format method.
     */
    fun format(
        length: Int,
        key: String?,
        fields: List<String>?,
        delimiter: String,
        lengthMarker: Boolean
    ): String {
        val config = HeaderConfig(length, key, fields, delimiter, lengthMarker)
        return format(config)
    }

    private fun StringBuilder.appendKeyIfPresent(key: String?) {
        if (key != null) {
            append(PrimitiveEncoder.encodeKey(key))
        }
    }

    private fun StringBuilder.appendArrayLength(
        length: Int,
        delimiter: String,
        lengthMarker: Boolean
    ) {
        append(OPEN_BRACKET)

        if (lengthMarker) {
            append("#")
        }

        append(length)
        appendDelimiterIfNotDefault(delimiter)
        append(CLOSE_BRACKET)
    }

    private fun StringBuilder.appendDelimiterIfNotDefault(delimiter: String) {
        if (delimiter != COMMA) {
            append(delimiter)
        }
    }

    private fun StringBuilder.appendFieldsIfPresent(
        fields: List<String>?,
        delimiter: String
    ) {
        if (fields.isNullOrEmpty()) {
            return
        }

        append(OPEN_BRACE)
        val quotedFields = formatFields(fields, delimiter)
        append(quotedFields)
        append(CLOSE_BRACE)
    }

    private fun formatFields(fields: List<String>, delimiter: String): String =
        fields.joinToString(delimiter) { PrimitiveEncoder.encodeKey(it) }
}
