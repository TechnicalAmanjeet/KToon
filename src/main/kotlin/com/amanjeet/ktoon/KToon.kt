package com.amanjeet.ktoon

import com.fasterxml.jackson.databind.JsonNode
import com.amanjeet.ktoon.encoder.ValueEncoder
import com.amanjeet.ktoon.normalizer.JsonNormalizer

/**
 * Main API for encoding Kotlin/Java objects and JSON to KToon format.
 *
 * KToon is a structured text format that represents JSON-like data in a more
 * human-readable way, with support for tabular arrays and inline formatting.
 *
 * ## Usage Examples:
 *
 * ```kotlin
 * // Encode a Kotlin object with default options
 * val result = KToon.encode(myObject)
 *
 * // Encode with custom options
 * val options = EncodeOptions(indent = 4, delimiter = Delimiter.PIPE, lengthMarker = true)
 * val result = KToon.encode(myObject, options)
 *
 * // Encode pre-parsed JSON
 * val json: JsonNode = objectMapper.readTree(jsonString)
 * val result = KToon.encode(json)
 *
 * // Encode a plain JSON string directly
 * val result = KToon.encodeJson("""{"id":123,"name":"Ada"}""")
 * ```
 */
object KToon {
    /**
     * Encodes a Kotlin/Java object to TOON format using default options.
     *
     * The object is first normalized (Java/Kotlin types are converted to JSON-compatible
     * representations), then encoded to TOON format.
     *
     * @param input The object to encode (can be null)
     * @return The TOON-formatted string
     */
    fun encode(input: Any?): String = encode(input, EncodeOptions.DEFAULT)

    /**
     * Encodes a Kotlin/Java object to TOON format using custom options.
     *
     * The object is first normalized (Java/Kotlin types are converted to JSON-compatible
     * representations), then encoded to TOON format.
     *
     * @param input   The object to encode (can be null)
     * @param options Encoding options (indent, delimiter, length marker)
     * @return The TOON-formatted string
     */
    fun encode(input: Any?, options: EncodeOptions): String {
        val normalizedValue: JsonNode = JsonNormalizer.normalize(input)
        return ValueEncoder.encodeValue(normalizedValue, options)
    }

    /**
     * Encodes a plain JSON string to TOON format using default options.
     *
     * This is a convenience overload that parses the JSON string and encodes it
     * without requiring callers to create a [JsonNode] or intermediate objects.
     *
     * @param json The JSON string to encode (must be valid JSON)
     * @return The TOON-formatted string
     * @throws IllegalArgumentException if the input is not valid JSON
     */
    fun encodeJson(json: String): String = encodeJson(json, EncodeOptions.DEFAULT)

    /**
     * Encodes a plain JSON string to TOON format using custom options.
     *
     * Parsing is delegated to [JsonNormalizer.parse] to maintain
     * separation of concerns.
     *
     * @param json    The JSON string to encode (must be valid JSON)
     * @param options Encoding options (indent, delimiter, length marker)
     * @return The TOON-formatted string
     * @throws IllegalArgumentException if the input is not valid JSON
     */
    fun encodeJson(json: String, options: EncodeOptions): String {
        val parsed: JsonNode = JsonNormalizer.parse(json)
        return ValueEncoder.encodeValue(parsed, options)
    }
}

