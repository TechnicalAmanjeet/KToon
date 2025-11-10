package com.amanjeet.ktoon.encoder

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.amanjeet.ktoon.EncodeOptions

/**
 * Core encoding orchestrator for converting JsonNode values to TOON format.
 * Delegates to specialized encoders based on node type.
 */
object ValueEncoder {
    /**
     * Encodes a normalized JsonNode value to TOON format.
     *
     * @param value   The JsonNode to encode
     * @param options Encoding options (indent, delimiter, length marker)
     * @return The TOON-formatted string
     */
    fun encodeValue(value: JsonNode, options: EncodeOptions): String {
        // Handle primitive values directly
        if (value.isValueNode) {
            return PrimitiveEncoder.encodePrimitive(value, options.delimiter.value)
        }

        // Complex values need a LineWriter for indentation
        val writer = LineWriter(options.indent)

        when {
            value.isArray -> {
                ArrayEncoder.encodeArray(null, value as ArrayNode, writer, 0, options)
            }
            value.isObject -> {
                ObjectEncoder.encodeObject(value as ObjectNode, writer, 0, options)
            }
        }

        return writer.toString()
    }
}

