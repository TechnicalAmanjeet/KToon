package com.amanjeet.ktoon.encoder

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.amanjeet.ktoon.EncodeOptions
import com.amanjeet.ktoon.util.Constants.CLOSE_BRACKET
import com.amanjeet.ktoon.util.Constants.COLON
import com.amanjeet.ktoon.util.Constants.LIST_ITEM_MARKER
import com.amanjeet.ktoon.util.Constants.LIST_ITEM_PREFIX
import com.amanjeet.ktoon.util.Constants.OPEN_BRACKET
import com.amanjeet.ktoon.util.Constants.SPACE

/**
 * Handles encoding of objects as list items in non-uniform arrays.
 * Implements the complex logic for placing the first field on the "- " line
 * and indenting remaining fields.
 */
object ListItemEncoder {
    /**
     * Encodes an object as a list item.
     * First key-value appears on the "- " line, remaining fields are indented.
     *
     * @param obj     The object to encode
     * @param writer  LineWriter for output
     * @param depth   Indentation depth
     * @param options Encoding options
     */
    fun encodeObjectAsListItem(
        obj: ObjectNode,
        writer: LineWriter,
        depth: Int,
        options: EncodeOptions
    ) {
        val keys = mutableListOf<String>()
        obj.fieldNames().forEach { keys.add(it) }

        if (keys.isEmpty()) {
            writer.push(depth, LIST_ITEM_MARKER)
            return
        }

        // First key-value on the same line as "- "
        val firstKey = keys[0]
        val firstValue = obj[firstKey]
        encodeFirstKeyValue(firstKey, firstValue, writer, depth, options)

        // Remaining keys on indented lines
        for (i in 1 until keys.size) {
            val key = keys[i]
            ObjectEncoder.encodeKeyValuePair(key, obj[key], writer, depth + 1, options)
        }
    }

    /**
     * Encodes the first key-value pair of a list item.
     * Handles special formatting for arrays and objects.
     */
    private fun encodeFirstKeyValue(
        key: String,
        value: JsonNode,
        writer: LineWriter,
        depth: Int,
        options: EncodeOptions
    ) {
        val encodedKey = PrimitiveEncoder.encodeKey(key)

        when {
            value.isValueNode -> {
                encodeFirstValueAsPrimitive(encodedKey, value, writer, depth, options)
            }
            value.isArray -> {
                encodeFirstValueAsArray(key, encodedKey, value as ArrayNode, writer, depth, options)
            }
            value.isObject -> {
                encodeFirstValueAsObject(encodedKey, value as ObjectNode, writer, depth, options)
            }
        }
    }

    private fun encodeFirstValueAsPrimitive(
        encodedKey: String,
        value: JsonNode,
        writer: LineWriter,
        depth: Int,
        options: EncodeOptions
    ) {
        writer.push(
            depth,
            "$LIST_ITEM_PREFIX$encodedKey$COLON$SPACE${PrimitiveEncoder.encodePrimitive(value, options.delimiter.value)}"
        )
    }

    private fun encodeFirstValueAsArray(
        key: String,
        encodedKey: String,
        arrayValue: ArrayNode,
        writer: LineWriter,
        depth: Int,
        options: EncodeOptions
    ) {
        when {
            ArrayEncoder.isArrayOfPrimitives(arrayValue) -> {
                encodeFirstArrayAsPrimitives(key, arrayValue, writer, depth, options)
            }
            ArrayEncoder.isArrayOfObjects(arrayValue) -> {
                encodeFirstArrayAsObjects(key, encodedKey, arrayValue, writer, depth, options)
            }
            else -> {
                encodeFirstArrayAsComplex(encodedKey, arrayValue, writer, depth, options)
            }
        }
    }

    private fun encodeFirstArrayAsPrimitives(
        key: String,
        arrayValue: ArrayNode,
        writer: LineWriter,
        depth: Int,
        options: EncodeOptions
    ) {
        val formatted = ArrayEncoder.formatInlineArray(
            arrayValue,
            options.delimiter.value,
            key,
            options.lengthMarker
        )
        writer.push(depth, "$LIST_ITEM_PREFIX$formatted")
    }

    private fun encodeFirstArrayAsObjects(
        key: String,
        encodedKey: String,
        arrayValue: ArrayNode,
        writer: LineWriter,
        depth: Int,
        options: EncodeOptions
    ) {
        val header = TabularArrayEncoder.detectTabularHeader(arrayValue)
        if (header.isNotEmpty()) {
            val headerStr = PrimitiveEncoder.formatHeader(
                arrayValue.size(),
                key,
                header,
                options.delimiter.value,
                options.lengthMarker
            )
            writer.push(depth, "$LIST_ITEM_PREFIX$headerStr")
            // Write just the rows, header was already written above
            TabularArrayEncoder.writeTabularRows(arrayValue, header, writer, depth + 1, options)
        } else {
            writer.push(
                depth,
                "$LIST_ITEM_PREFIX$encodedKey$OPEN_BRACKET${arrayValue.size()}$CLOSE_BRACKET$COLON"
            )
            for (item in arrayValue) {
                if (item.isObject) {
                    encodeObjectAsListItem(item as ObjectNode, writer, depth + 1, options)
                }
            }
        }
    }

    private fun encodeFirstArrayAsComplex(
        encodedKey: String,
        arrayValue: ArrayNode,
        writer: LineWriter,
        depth: Int,
        options: EncodeOptions
    ) {
        writer.push(
            depth,
            "$LIST_ITEM_PREFIX$encodedKey$OPEN_BRACKET${arrayValue.size()}$CLOSE_BRACKET$COLON"
        )

        for (item in arrayValue) {
            when {
                item.isValueNode -> {
                    writer.push(
                        depth + 1,
                        "$LIST_ITEM_PREFIX${PrimitiveEncoder.encodePrimitive(item, options.delimiter.value)}"
                    )
                }
                item.isArray && ArrayEncoder.isArrayOfPrimitives(item) -> {
                    val inline = ArrayEncoder.formatInlineArray(
                        item as ArrayNode,
                        options.delimiter.value,
                        null,
                        options.lengthMarker
                    )
                    writer.push(depth + 1, "$LIST_ITEM_PREFIX$inline")
                }
                item.isObject -> {
                    encodeObjectAsListItem(item as ObjectNode, writer, depth + 1, options)
                }
            }
        }
    }

    private fun encodeFirstValueAsObject(
        encodedKey: String,
        nestedObj: ObjectNode,
        writer: LineWriter,
        depth: Int,
        options: EncodeOptions
    ) {
        writer.push(depth, "$LIST_ITEM_PREFIX$encodedKey$COLON")
        if (!nestedObj.isEmpty(null)) {
            ObjectEncoder.encodeObject(nestedObj, writer, depth + 2, options)
        }
    }
}

