package com.amanjeet.ktoon

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

// Test data classes (must be outside inner classes)
data class TestUser(val id: Int, val name: String, val active: Boolean)
data class TestItem(val sku: String, val qty: Int, val price: Double)
data class TestOrder(val items: List<TestItem>)

/**
 * Test class for KToon encoder, converted from Java JToon tests.
 */
@DisplayName("KToon Tests")
class KToonTest {

    @Nested
    @DisplayName("primitives")
    inner class Primitives {

        @Test
        @DisplayName("encodes safe strings without quotes")
        fun encodesSafeStrings() {
            assertEquals("hello", KToon.encode("hello"))
            assertEquals("Ada_99", KToon.encode("Ada_99"))
        }

        @Test
        @DisplayName("quotes empty string")
        fun quotesEmptyString() {
            assertEquals("\"\"", KToon.encode(""))
        }

        @Test
        @DisplayName("quotes strings that look like booleans or numbers")
        fun quotesAmbiguousStrings() {
            assertEquals("\"true\"", KToon.encode("true"))
            assertEquals("\"false\"", KToon.encode("false"))
            assertEquals("\"null\"", KToon.encode("null"))
            assertEquals("\"42\"", KToon.encode("42"))
            assertEquals("\"-3.14\"", KToon.encode("-3.14"))
            assertEquals("\"1e-6\"", KToon.encode("1e-6"))
            assertEquals("\"05\"", KToon.encode("05"))
        }

        @Test
        @DisplayName("escapes control characters in strings")
        fun escapesControlChars() {
            assertEquals("\"line1\\nline2\"", KToon.encode("line1\nline2"))
            assertEquals("\"tab\\there\"", KToon.encode("tab\there"))
            assertEquals("\"return\\rcarriage\"", KToon.encode("return\rcarriage"))
            assertEquals("\"C:\\\\Users\\\\path\"", KToon.encode("C:\\Users\\path"))
        }

        @Test
        @DisplayName("quotes strings with structural characters")
        fun quotesStructuralChars() {
            assertEquals("\"[3]: x,y\"", KToon.encode("[3]: x,y"))
            assertEquals("\"- item\"", KToon.encode("- item"))
            assertEquals("\"[test]\"", KToon.encode("[test]"))
            assertEquals("\"{key}\"", KToon.encode("{key}"))
        }

        @Test
        @DisplayName("handles Unicode and emoji")
        fun handlesUnicodeAndEmoji() {
            assertEquals("café", KToon.encode("café"))
            assertEquals("你好", KToon.encode("你好"))
            assertEquals("🚀", KToon.encode("🚀"))
        }

        @Test
        @DisplayName("encodes booleans")
        fun encodesBooleans() {
            assertEquals("true", KToon.encode(true))
            assertEquals("false", KToon.encode(false))
        }

        @Test
        @DisplayName("encodes null")
        fun encodesNull() {
            assertEquals("null", KToon.encode(null))
        }

        @Test
        @DisplayName("encodes numbers")
        fun encodesNumbers() {
            assertEquals("42", KToon.encode(42))
            assertEquals("-17", KToon.encode(-17))
            assertEquals("3.14", KToon.encode(3.14))
            assertEquals("-0.5", KToon.encode(-0.5))
        }
    }

    @Nested
    @DisplayName("objects")
    inner class Objects {

        @Test
        @DisplayName("encodes simple object")
        fun encodesSimpleObject() {
            val obj = mapOf("id" to 123, "name" to "Ada")
            val expected = "id: 123\nname: Ada"
            assertEquals(expected, KToon.encode(obj))
        }

        @Test
        @DisplayName("encodes nested object")
        fun encodesNestedObject() {
            val obj = mapOf(
                "user" to mapOf(
                    "id" to 123,
                    "name" to "Ada"
                )
            )
            val expected = "user:\n  id: 123\n  name: Ada"
            assertEquals(expected, KToon.encode(obj))
        }

        @Test
        @DisplayName("encodes empty object")
        fun encodesEmptyObject() {
            val obj = mapOf("config" to emptyMap<String, Any>())
            assertEquals("config:", KToon.encode(obj))
        }
    }

    @Nested
    @DisplayName("arrays")
    inner class Arrays {

        @Test
        @DisplayName("encodes primitive array inline")
        fun encodesPrimitiveArrayInline() {
            val obj = mapOf("tags" to listOf("admin", "ops", "dev"))
            assertEquals("tags[3]: admin,ops,dev", KToon.encode(obj))
        }

        @Test
        @DisplayName("encodes empty array")
        fun encodesEmptyArray() {
            val obj = mapOf("items" to emptyList<Any>())
            assertEquals("items[0]:", KToon.encode(obj))
        }

        @Test
        @DisplayName("encodes array of uniform objects as tabular")
        fun encodesTabularArray() {
            val items = listOf(
                mapOf("sku" to "A1", "qty" to 2, "price" to 9.99),
                mapOf("sku" to "B2", "qty" to 1, "price" to 14.5)
            )
            val obj = mapOf("items" to items)
            val expected = "items[2]{sku,qty,price}:\n  A1,2,9.99\n  B2,1,14.5"
            assertEquals(expected, KToon.encode(obj))
        }

        @Test
        @DisplayName("encodes root array")
        fun encodesRootArray() {
            val arr = listOf("x", "y", "z")
            assertEquals("[3]: x,y,z", KToon.encode(arr))
        }
    }

    @Nested
    @DisplayName("data classes")
    inner class DataClasses {

        @Test
        @DisplayName("encodes data class")
        fun encodesDataClass() {
            val user = TestUser(123, "Ada", true)
            val expected = "id: 123\nname: Ada\nactive: true"
            assertEquals(expected, KToon.encode(user))
        }

        @Test
        @DisplayName("encodes data class with tabular array")
        fun encodesDataClassWithTabularArray() {
            val order = TestOrder(
                items = listOf(
                    TestItem("A1", 2, 9.99),
                    TestItem("B2", 1, 14.5)
                )
            )
            val expected = "items[2]{sku,qty,price}:\n  A1,2,9.99\n  B2,1,14.5"
            assertEquals(expected, KToon.encode(order))
        }
    }

    @Nested
    @DisplayName("JSON strings")
    inner class JsonStrings {

        @Test
        @DisplayName("encodes JSON string")
        fun encodesJsonString() {
            val json = """{"id":123,"name":"Ada"}"""
            val expected = "id: 123\nname: Ada"
            assertEquals(expected, KToon.encodeJson(json))
        }

        @Test
        @DisplayName("encodes JSON string with nested object")
        fun encodesJsonStringWithNested() {
            val json = """{"user":{"id":123,"name":"Ada"}}"""
            val expected = "user:\n  id: 123\n  name: Ada"
            assertEquals(expected, KToon.encodeJson(json))
        }
    }

    @Nested
    @DisplayName("custom options")
    inner class CustomOptions {

        @Test
        @DisplayName("uses custom indent")
        fun usesCustomIndent() {
            val obj = mapOf("user" to mapOf("id" to 123))
            val options = EncodeOptions(indent = 4)
            val expected = "user:\n    id: 123"
            assertEquals(expected, KToon.encode(obj, options))
        }

        @Test
        @DisplayName("uses pipe delimiter")
        fun usesPipeDelimiter() {
            val obj = mapOf("tags" to listOf("a", "b", "c"))
            val options = EncodeOptions(delimiter = Delimiter.PIPE)
            val expected = "tags[3|]: a|b|c"
            assertEquals(expected, KToon.encode(obj, options))
        }

        @Test
        @DisplayName("uses tab delimiter")
        fun usesTabDelimiter() {
            val obj = mapOf("tags" to listOf("a", "b", "c"))
            val options = EncodeOptions(delimiter = Delimiter.TAB)
            val expected = "tags[3\t]: a\tb\tc"
            assertEquals(expected, KToon.encode(obj, options))
        }

        @Test
        @DisplayName("uses length marker")
        fun usesLengthMarker() {
            val obj = mapOf("tags" to listOf("a", "b", "c"))
            val options = EncodeOptions(lengthMarker = true)
            val expected = "tags[#3]: a,b,c"
            assertEquals(expected, KToon.encode(obj, options))
        }
    }
}

