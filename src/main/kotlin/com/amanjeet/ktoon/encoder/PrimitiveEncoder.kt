package com.amanjeet.ktoon.encoder

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.amanjeet.ktoon.util.Constants.DOUBLE_QUOTE
import com.amanjeet.ktoon.util.Constants.NULL_LITERAL
import com.amanjeet.ktoon.util.StringEscaper
import com.amanjeet.ktoon.util.StringValidator

/**
 * Encodes primitive values and object keys for TOON format.
 * Delegates validation to StringValidator, escaping to StringEscaper,
 * and header formatting to HeaderFormatter.
 */
object PrimitiveEncoder {
    /**
     * Encodes a primitive JsonNode value.
     */
    fun encodePrimitive(value: JsonNode, delimiter: String): String = when (value.nodeType) {
        JsonNodeType.BOOLEAN -> value.asBoolean().toString()
        JsonNodeType.NUMBER -> value.asText()
        JsonNodeType.STRING -> encodeStringLiteral(value.asText(), delimiter)
        else -> NULL_LITERAL
    }

    /**
     * Encodes a string literal, quoting if necessary.
     * Delegates validation to StringValidator and escaping to StringEscaper.
     */
    fun encodeStringLiteral(value: String, delimiter: String): String {
        if (StringValidator.isSafeUnquoted(value, delimiter)) {
            return value
        }

        return "$DOUBLE_QUOTE${StringEscaper.escape(value)}$DOUBLE_QUOTE"
    }

    /**
     * Encodes an object key, quoting if necessary.
     * Delegates validation to StringValidator and escaping to StringEscaper.
     */
    fun encodeKey(key: String): String {
        if (StringValidator.isValidUnquotedKey(key)) {
            return key
        }

        return "$DOUBLE_QUOTE${StringEscaper.escape(key)}$DOUBLE_QUOTE"
    }

    /**
     * Joins encoded primitive values with the specified delimiter.
     */
    fun joinEncodedValues(values: List<JsonNode>, delimiter: String): String =
        values.joinToString(delimiter) { encodePrimitive(it, delimiter) }

    /**
     * Formats a header for arrays and tables.
     * Delegates to HeaderFormatter for implementation.
     *
     * @param length       Array length
     * @param key          Optional key prefix
     * @param fields       Optional field names for tabular format
     * @param delimiter    The delimiter being used
     * @param lengthMarker Whether to include # marker before length
     * @return Formatted header string
     */
    fun formatHeader(
        length: Int,
        key: String?,
        fields: List<String>?,
        delimiter: String,
        lengthMarker: Boolean
    ): String = HeaderFormatter.format(length, key, fields, delimiter, lengthMarker)
}

