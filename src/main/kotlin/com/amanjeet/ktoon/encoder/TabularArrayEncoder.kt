package com.amanjeet.ktoon.encoder

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.amanjeet.ktoon.EncodeOptions

/**
 * Detects and encodes uniform arrays of objects in efficient tabular format.
 * Tabular format declares field names once in a header and streams rows as CSV-like data.
 */
object TabularArrayEncoder {
    /**
     * Detects if an array can be encoded in tabular format.
     * Returns the header fields if tabular encoding is possible, empty list otherwise.
     *
     * @param rows The array to analyze
     * @return List of field names for tabular header, or empty list if not tabular
     */
    fun detectTabularHeader(rows: ArrayNode): List<String> {
        if (rows.isEmpty) {
            return emptyList()
        }

        val firstRow = rows[0]
        if (!firstRow.isObject) {
            return emptyList()
        }

        val firstObj = firstRow as ObjectNode
        val firstKeys = mutableListOf<String>()
        firstObj.fieldNames().forEach { firstKeys.add(it) }

        if (firstKeys.isEmpty()) {
            return emptyList()
        }

        return if (isTabularArray(rows, firstKeys)) {
            firstKeys
        } else {
            emptyList()
        }
    }

    /**
     * Checks if all rows in the array have the same keys with primitive values.
     */
    private fun isTabularArray(rows: ArrayNode, header: List<String>): Boolean {
        for (row in rows) {
            if (!row.isObject) {
                return false
            }

            val obj = row as ObjectNode
            val keys = mutableListOf<String>()
            obj.fieldNames().forEach { keys.add(it) }

            // All objects must have the same keys (but order can differ)
            if (keys.size != header.size) {
                return false
            }

            // Check that all header keys exist in the row and all values are primitives
            for (key in header) {
                if (!obj.has(key)) {
                    return false
                }
                if (!obj[key].isValueNode) {
                    return false
                }
            }
        }

        return true
    }

    /**
     * Encodes an array of objects as a tabular structure.
     *
     * @param prefix  Optional key prefix
     * @param rows    Array of uniform objects
     * @param header  List of field names
     * @param writer  LineWriter for output
     * @param depth   Indentation depth
     * @param options Encoding options
     */
    fun encodeArrayOfObjectsAsTabular(
        prefix: String?,
        rows: ArrayNode,
        header: List<String>,
        writer: LineWriter,
        depth: Int,
        options: EncodeOptions
    ) {
        val headerStr = PrimitiveEncoder.formatHeader(
            rows.size(),
            prefix,
            header,
            options.delimiter.value,
            options.lengthMarker
        )
        writer.push(depth, headerStr)

        writeTabularRows(rows, header, writer, depth + 1, options)
    }

    /**
     * Writes rows of tabular data by extracting values in header order.
     * Public to allow ListItemEncoder to write rows after placing header on "- " line.
     *
     * @param rows    Array of objects
     * @param header  List of field names
     * @param writer  LineWriter for output
     * @param depth   Indentation depth
     * @param options Encoding options
     */
    fun writeTabularRows(
        rows: ArrayNode,
        header: List<String>,
        writer: LineWriter,
        depth: Int,
        options: EncodeOptions
    ) {
        for (row in rows) {
            val obj = row as ObjectNode
            val values = header.map { obj[it] }
            val joinedValue = PrimitiveEncoder.joinEncodedValues(values, options.delimiter.value)
            writer.push(depth, joinedValue)
        }
    }
}

