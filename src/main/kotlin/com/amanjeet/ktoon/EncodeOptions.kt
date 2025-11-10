package com.amanjeet.ktoon

/**
 * Configuration options for encoding data to TOON format.
 *
 * @param indent       Number of spaces per indentation level (default: 2)
 * @param delimiter    Delimiter to use for tabular array rows and inline
 *                     primitive arrays (default: COMMA)
 * @param lengthMarker Optional marker to prefix array lengths in headers. When
 *                     true, arrays render as [#N] instead of [N] (default: false)
 */
data class EncodeOptions(
    val indent: Int = 2,
    val delimiter: Delimiter = Delimiter.COMMA,
    val lengthMarker: Boolean = false
) {
    companion object {
        /**
         * Default encoding options: 2 spaces indent, comma delimiter, no length marker
         */
        val DEFAULT = EncodeOptions()

        /**
         * Creates EncodeOptions with custom indent, using default delimiter and length marker.
         */
        fun withIndent(indent: Int) = EncodeOptions(indent = indent)

        /**
         * Creates EncodeOptions with custom delimiter, using default indent and length marker.
         */
        fun withDelimiter(delimiter: Delimiter) = EncodeOptions(delimiter = delimiter)

        /**
         * Creates EncodeOptions with custom length marker, using default indent and delimiter.
         */
        fun withLengthMarker(lengthMarker: Boolean) = EncodeOptions(lengthMarker = lengthMarker)
    }
}
