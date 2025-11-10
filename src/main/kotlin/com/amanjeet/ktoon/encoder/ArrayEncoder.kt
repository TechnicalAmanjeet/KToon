package com.amanjeet.ktoon.encoder

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.amanjeet.ktoon.EncodeOptions
import com.amanjeet.ktoon.util.Constants.LIST_ITEM_PREFIX
import com.amanjeet.ktoon.util.Constants.SPACE

/**
 * Handles encoding of JSON arrays to TOON format.
 * Orchestrates array encoding by detecting array types and delegating to specialized encoders.
 */
object ArrayEncoder {
    /**
     * Main entry point for array encoding.
     * Detects array type and delegates to appropriate encoding method.
     *
     * @param key     Optional key prefix
     * @param value   ArrayNode to encode
     * @param writer  LineWriter for output
     * @param depth   Indentation depth
     * @param options Encoding options
     */
    fun encodeArray(
        key: String?,
        value: ArrayNode,
        writer: LineWriter,
        depth: Int,
        options: EncodeOptions
    ) {
        if (value.isEmpty) {
            val header = PrimitiveEncoder.formatHeader(
                0,
                key,
                null,
                options.delimiter.value,
                options.lengthMarker
            )
            writer.push(depth, header)
            return
        }

        // Primitive array
        if (isArrayOfPrimitives(value)) {
            encodeInlinePrimitiveArray(key, value, writer, depth, options)
            return
        }

        // Array of arrays (all primitives)
        if (isArrayOfArrays(value)) {
            val allPrimitiveArrays = value.all { item ->
                item.isArray && isArrayOfPrimitives(item)
            }

            if (allPrimitiveArrays) {
                encodeArrayOfArraysAsListItems(key, value, writer, depth, options)
                return
            }
        }

        // Array of objects
        if (isArrayOfObjects(value)) {
            val header = TabularArrayEncoder.detectTabularHeader(value)
            if (header.isNotEmpty()) {
                TabularArrayEncoder.encodeArrayOfObjectsAsTabular(key, value, header, writer, depth, options)
            } else {
                encodeMixedArrayAsListItems(key, value, writer, depth, options)
            }
            return
        }

        // Mixed array: fallback to expanded format
        encodeMixedArrayAsListItems(key, value, writer, depth, options)
    }

    /**
     * Checks if an array contains only primitive values.
     */
    fun isArrayOfPrimitives(array: JsonNode): Boolean {
        if (!array.isArray) {
            return false
        }
        return array.all { it.isValueNode }
    }

    /**
     * Checks if an array contains only arrays.
     */
    fun isArrayOfArrays(array: JsonNode): Boolean {
        if (!array.isArray) {
            return false
        }
        return array.all { it.isArray }
    }

    /**
     * Checks if an array contains only objects.
     */
    fun isArrayOfObjects(array: JsonNode): Boolean {
        if (!array.isArray) {
            return false
        }
        return array.all { it.isObject }
    }

    /**
     * Encodes a primitive array inline: key[N]: v1,v2,v3
     */
    private fun encodeInlinePrimitiveArray(
        prefix: String?,
        values: ArrayNode,
        writer: LineWriter,
        depth: Int,
        options: EncodeOptions
    ) {
        val formatted = formatInlineArray(values, options.delimiter.value, prefix, options.lengthMarker)
        writer.push(depth, formatted)
    }

    /**
     * Formats an inline primitive array with header and values.
     */
    fun formatInlineArray(
        values: ArrayNode,
        delimiter: String,
        prefix: String?,
        lengthMarker: Boolean
    ): String {
        val valueList = values.toList()

        val header = PrimitiveEncoder.formatHeader(values.size(), prefix, null, delimiter, lengthMarker)
        val joinedValue = PrimitiveEncoder.joinEncodedValues(valueList, delimiter)

        // Only add space if there are values
        return if (values.isEmpty) {
            header
        } else {
            "$header$SPACE$joinedValue"
        }
    }

    /**
     * Encodes an array of primitive arrays as list items.
     */
    private fun encodeArrayOfArraysAsListItems(
        prefix: String?,
        values: ArrayNode,
        writer: LineWriter,
        depth: Int,
        options: EncodeOptions
    ) {
        val header = PrimitiveEncoder.formatHeader(
            values.size(),
            prefix,
            null,
            options.delimiter.value,
            options.lengthMarker
        )
        writer.push(depth, header)

        for (arr in values) {
            if (arr.isArray && isArrayOfPrimitives(arr)) {
                val inline = formatInlineArray(
                    arr as ArrayNode,
                    options.delimiter.value,
                    null,
                    options.lengthMarker
                )
                writer.push(depth + 1, "$LIST_ITEM_PREFIX$inline")
            }
        }
    }

    /**
     * Encodes a mixed array (non-uniform) as list items.
     */
    private fun encodeMixedArrayAsListItems(
        prefix: String?,
        items: ArrayNode,
        writer: LineWriter,
        depth: Int,
        options: EncodeOptions
    ) {
        val header = PrimitiveEncoder.formatHeader(
            items.size(),
            prefix,
            null,
            options.delimiter.value,
            options.lengthMarker
        )
        writer.push(depth, header)

        for (item in items) {
            when {
                item.isValueNode -> {
                    // Direct primitive as list item
                    writer.push(
                        depth + 1,
                        "$LIST_ITEM_PREFIX${PrimitiveEncoder.encodePrimitive(item, options.delimiter.value)}"
                    )
                }
                item.isArray -> {
                    // Direct array as list item
                    if (isArrayOfPrimitives(item)) {
                        val inline = formatInlineArray(
                            item as ArrayNode,
                            options.delimiter.value,
                            null,
                            options.lengthMarker
                        )
                        writer.push(depth + 1, "$LIST_ITEM_PREFIX$inline")
                    }
                }
                item.isObject -> {
                    // Object as list item - delegate to ListItemEncoder
                    ListItemEncoder.encodeObjectAsListItem(
                        item as com.fasterxml.jackson.databind.node.ObjectNode,
                        writer,
                        depth + 1,
                        options
                    )
                }
            }
        }
    }
}

