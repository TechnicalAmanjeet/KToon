package com.amanjeet.ktoon.normalizer

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.*
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.math.BigDecimal
import java.math.BigInteger
import java.time.*
import java.time.format.DateTimeFormatter

/**
 * Normalizes Kotlin/Java objects to Jackson JsonNode representation.
 * Handles Java-specific types like LocalDateTime, Stream, etc.
 * Kotlin-specific: handles nullable types naturally without Optional wrapper.
 */
object JsonNormalizer {
    private val MAPPER = ObjectMapper().registerKotlinModule()

    private val normalizers: List<(Any) -> JsonNode?> = listOf(
        ::tryNormalizePrimitive,
        ::tryNormalizeBigNumber,
        ::tryNormalizeTemporal,
        ::tryNormalizeCollection,
        ::tryNormalizePojo
    )

    /**
     * Parses a JSON string into a JsonNode using the shared ObjectMapper.
     * This centralizes JSON parsing concerns to keep the public API thin and
     * maintain separation of responsibilities between parsing, normalization,
     * and encoding.
     *
     * @param json The JSON string to parse (must be non-blank)
     * @return Parsed JsonNode
     * @throws IllegalArgumentException if the input is blank or not valid JSON
     */
    fun parse(json: String): JsonNode {
        require(json.isNotBlank()) { "Invalid JSON" }
        return try {
            MAPPER.readTree(json)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid JSON", e)
        }
    }

    /**
     * Normalizes any Kotlin/Java object to a JsonNode.
     *
     * @param value The value to normalize
     * @return The normalized JsonNode
     */
    fun normalize(value: Any?): JsonNode {
        return when {
            value == null -> NullNode.getInstance()
            value is JsonNode -> value
            value is Sequence<*> -> normalize(value.toList())
            value.javaClass.isArray -> normalizeArray(value)
            else -> normalizeWithStrategy(value)
        }
    }

    /**
     * Attempts normalization using chain of responsibility pattern.
     */
    private fun normalizeWithStrategy(value: Any): JsonNode {
        return normalizers.firstNotNullOfOrNull { it(value) }
            ?: NullNode.getInstance()
    }

    /**
     * Attempts to normalize primitive types and their wrappers.
     * Returns null if the value is not a primitive type.
     */
    private fun tryNormalizePrimitive(value: Any): JsonNode? = when (value) {
        is String -> TextNode.valueOf(value)
        is Boolean -> BooleanNode.valueOf(value)
        is Int -> IntNode.valueOf(value)
        is Long -> LongNode.valueOf(value)
        is Double -> normalizeDouble(value)
        is Float -> normalizeFloat(value)
        is Short -> ShortNode.valueOf(value)
        is Byte -> IntNode.valueOf(value.toInt())
        else -> null
    }

    /**
     * Normalizes Double values handling special cases.
     */
    private fun normalizeDouble(value: Double): JsonNode {
        if (!value.isFinite()) {
            return NullNode.getInstance()
        }
        if (value == 0.0 || value == -0.0) {
            return IntNode.valueOf(0)
        }
        return tryConvertToLong(value) ?: DoubleNode.valueOf(value)
    }

    /**
     * Normalizes Float values handling special cases.
     */
    private fun normalizeFloat(value: Float): JsonNode =
        if (value.isFinite()) FloatNode.valueOf(value) else NullNode.getInstance()

    /**
     * Attempts to convert a double to a long if it's a whole number.
     */
    private fun tryConvertToLong(value: Double): JsonNode? {
        if (value != kotlin.math.floor(value)) {
            return null
        }
        if (value > Long.MAX_VALUE || value < Long.MIN_VALUE) {
            return null
        }
        val longVal = value.toLong()
        return if (longVal.toDouble() == value) {
            LongNode.valueOf(longVal)
        } else {
            null
        }
    }

    /**
     * Attempts to normalize BigInteger and BigDecimal.
     * Returns null if the value is not a big number type.
     */
    private fun tryNormalizeBigNumber(value: Any): JsonNode? = when (value) {
        is BigInteger -> normalizeBigInteger(value)
        is BigDecimal -> DecimalNode.valueOf(value)
        else -> null
    }

    /**
     * Normalizes BigInteger, converting to long if within range.
     */
    private fun normalizeBigInteger(value: BigInteger): JsonNode {
        val fitsInLong = value.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) <= 0 &&
                value.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) >= 0
        return if (fitsInLong) {
            LongNode.valueOf(value.toLong())
        } else {
            TextNode.valueOf(value.toString())
        }
    }

    /**
     * Attempts to normalize temporal types (date/time) to ISO strings.
     * Returns null if the value is not a temporal type.
     */
    private fun tryNormalizeTemporal(value: Any): JsonNode? = when (value) {
        is LocalDateTime -> formatTemporal(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        is LocalDate -> formatTemporal(value, DateTimeFormatter.ISO_LOCAL_DATE)
        is LocalTime -> formatTemporal(value, DateTimeFormatter.ISO_LOCAL_TIME)
        is ZonedDateTime -> formatTemporal(value, DateTimeFormatter.ISO_ZONED_DATE_TIME)
        is OffsetDateTime -> formatTemporal(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        is Instant -> TextNode.valueOf(value.toString())
        is java.util.Date -> TextNode.valueOf(value.toInstant().toString())
        else -> null
    }

    /**
     * Helper method to format temporal values consistently.
     */
    private fun <T : java.time.temporal.TemporalAccessor> formatTemporal(
        temporal: T,
        formatter: DateTimeFormatter
    ): JsonNode = TextNode.valueOf(formatter.format(temporal))

    /**
     * Attempts to normalize collections (Collection and Map).
     * Returns null if the value is not a collection type.
     */
    private fun tryNormalizeCollection(value: Any): JsonNode? = when (value) {
        is Collection<*> -> normalizeCollection(value)
        is Map<*, *> -> normalizeMap(value)
        else -> null
    }

    /**
     * Normalizes a Collection to an ArrayNode.
     */
    private fun normalizeCollection(collection: Collection<*>): ArrayNode {
        val arrayNode = MAPPER.createArrayNode()
        collection.forEach { item -> arrayNode.add(normalize(item)) }
        return arrayNode
    }

    /**
     * Normalizes a Map to an ObjectNode.
     */
    private fun normalizeMap(map: Map<*, *>): ObjectNode {
        val objectNode = MAPPER.createObjectNode()
        map.forEach { (key, value) ->
            objectNode.set<JsonNode>(key.toString(), normalize(value))
        }
        return objectNode
    }

    /**
     * Attempts to normalize POJOs using Jackson's default conversion.
     * Returns null for non-serializable objects.
     */
    private fun tryNormalizePojo(value: Any): JsonNode? = try {
        MAPPER.valueToTree(value)
    } catch (e: IllegalArgumentException) {
        NullNode.getInstance()
    }

    /**
     * Normalizes arrays to ArrayNode.
     */
    private fun normalizeArray(array: Any): JsonNode = when (array) {
        is IntArray -> buildArrayNode(array.size) { i -> IntNode.valueOf(array[i]) }
        is LongArray -> buildArrayNode(array.size) { i -> LongNode.valueOf(array[i]) }
        is DoubleArray -> buildArrayNode(array.size) { i -> normalizeDoubleElement(array[i]) }
        is FloatArray -> buildArrayNode(array.size) { i -> normalizeFloatElement(array[i]) }
        is BooleanArray -> buildArrayNode(array.size) { i -> BooleanNode.valueOf(array[i]) }
        is ByteArray -> buildArrayNode(array.size) { i -> IntNode.valueOf(array[i].toInt()) }
        is ShortArray -> buildArrayNode(array.size) { i -> ShortNode.valueOf(array[i]) }
        is CharArray -> buildArrayNode(array.size) { i -> TextNode.valueOf(array[i].toString()) }
        is Array<*> -> buildArrayNode(array.size) { i -> normalize(array[i]) }
        else -> MAPPER.createArrayNode()
    }

    /**
     * Builds an ArrayNode using a functional approach.
     */
    private fun buildArrayNode(length: Int, mapper: (Int) -> JsonNode): ArrayNode {
        val arrayNode = MAPPER.createArrayNode()
        for (i in 0 until length) {
            arrayNode.add(mapper(i))
        }
        return arrayNode
    }

    /**
     * Normalizes a single double element from an array.
     */
    private fun normalizeDoubleElement(value: Double): JsonNode =
        if (value.isFinite()) DoubleNode.valueOf(value) else NullNode.getInstance()

    /**
     * Normalizes a single float element from an array.
     */
    private fun normalizeFloatElement(value: Float): JsonNode =
        if (value.isFinite()) FloatNode.valueOf(value) else NullNode.getInstance()
}

