package com.amanjeet.ktoon.encoder

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.amanjeet.ktoon.EncodeOptions
import com.amanjeet.ktoon.util.Constants.COLON
import com.amanjeet.ktoon.util.Constants.SPACE

/**
 * Handles encoding of JSON objects to TOON format.
 * Recursively encodes nested objects and delegates arrays to ArrayEncoder.
 */
object ObjectEncoder {
    /**
     * Encodes an ObjectNode to TOON format.
     *
     * @param value   The ObjectNode to encode
     * @param writer  LineWriter for accumulating output
     * @param depth   Current indentation depth
     * @param options Encoding options
     */
    fun encodeObject(value: ObjectNode, writer: LineWriter, depth: Int, options: EncodeOptions) {
        val fieldNames = mutableListOf<String>()
        value.fieldNames().forEach { fieldNames.add(it) }

        for (key in fieldNames) {
            val fieldValue = value[key]
            encodeKeyValuePair(key, fieldValue, writer, depth, options)
        }
    }

    /**
     * Encodes a key-value pair in an object.
     */
    fun encodeKeyValuePair(
        key: String,
        value: JsonNode,
        writer: LineWriter,
        depth: Int,
        options: EncodeOptions
    ) {
        val encodedKey = PrimitiveEncoder.encodeKey(key)

        when {
            value.isValueNode -> {
                writer.push(
                    depth,
                    "$encodedKey$COLON$SPACE${PrimitiveEncoder.encodePrimitive(value, options.delimiter.value)}"
                )
            }
            value.isArray -> {
                ArrayEncoder.encodeArray(key, value as ArrayNode, writer, depth, options)
            }
            value.isObject -> {
                val objValue = value as ObjectNode
                if (objValue.isEmpty(null)) {
                    writer.push(depth, "$encodedKey$COLON")
                } else {
                    writer.push(depth, "$encodedKey$COLON")
                    encodeObject(objValue, writer, depth + 1, options)
                }
            }
        }
    }
}

