package examples

import com.amanjeet.ktoon.KToon
import com.amanjeet.ktoon.EncodeOptions
import com.amanjeet.ktoon.Delimiter

/**
 * Demonstration showing KToon output for various data structures.
 * Compare these outputs with JToon to verify they produce identical results.
 */
fun main() {
    println("=".repeat(80))
    println("KToon Output Samples - Compare with JToon")
    println("=".repeat(80))
    println()

    // Test 1: Simple Object
    println("1. Simple Object:")
    println("-".repeat(80))
    val simpleObj = mapOf("id" to 123, "name" to "Ada", "active" to true)
    val output1 = KToon.encode(simpleObj)
    println(output1)
    println()

    // Test 2: Nested Object
    println("2. Nested Object:")
    println("-".repeat(80))
    val nestedObj = mapOf(
        "user" to mapOf(
            "id" to 123,
            "name" to "Ada",
            "email" to "ada@example.com"
        )
    )
    val output2 = KToon.encode(nestedObj)
    println(output2)
    println()

    // Test 3: Primitive Array
    println("3. Primitive Array:")
    println("-".repeat(80))
    val primitiveArray = mapOf("tags" to listOf("admin", "ops", "dev"))
    val output3 = KToon.encode(primitiveArray)
    println(output3)
    println()

    // Test 4: Tabular Array
    println("4. Tabular Array (Uniform Objects):")
    println("-".repeat(80))
    val tabularArray = mapOf(
        "items" to listOf(
            mapOf("sku" to "A1", "qty" to 2, "price" to 9.99),
            mapOf("sku" to "B2", "qty" to 1, "price" to 14.5),
            mapOf("sku" to "C3", "qty" to 5, "price" to 3.25)
        )
    )
    val output4 = KToon.encode(tabularArray)
    println(output4)
    println()

    // Test 5: Complex Nested Structure
    println("5. Complex Nested Structure:")
    println("-".repeat(80))
    val complexNested = mapOf(
        "order" to mapOf(
            "id" to "ORD-123",
            "customer" to mapOf(
                "name" to "John Doe",
                "email" to "john@example.com"
            ),
            "items" to listOf(
                mapOf("sku" to "A1", "qty" to 2, "price" to 29.99),
                mapOf("sku" to "B2", "qty" to 1, "price" to 49.99)
            ),
            "total" to 109.97
        )
    )
    val output5 = KToon.encode(complexNested)
    println(output5)
    println()

    // Test 6: JSON String
    println("6. JSON String Encoding:")
    println("-".repeat(80))
    val json = """{"id":123,"name":"Ada","tags":["reading","gaming"]}"""
    val output6 = KToon.encodeJson(json)
    println(output6)
    println()

    // Test 7: Pipe Delimiter
    println("7. Pipe Delimiter:")
    println("-".repeat(80))
    val data7 = mapOf("tags" to listOf("a", "b", "c"))
    val output7 = KToon.encode(data7, EncodeOptions(delimiter = Delimiter.PIPE))
    println(output7)
    println()

    // Test 8: Tab Delimiter
    println("8. Tab Delimiter:")
    println("-".repeat(80))
    val data8 = mapOf("tags" to listOf("a", "b", "c"))
    val output8 = KToon.encode(data8, EncodeOptions(delimiter = Delimiter.TAB))
    println(output8)
    println()

    // Test 9: Length Marker
    println("9. Length Marker:")
    println("-".repeat(80))
    val data9 = mapOf("tags" to listOf("reading", "gaming", "coding"))
    val output9 = KToon.encode(data9, EncodeOptions(lengthMarker = true))
    println(output9)
    println()

    // Test 10: Empty Collections
    println("10. Empty Collections:")
    println("-".repeat(80))
    val emptyData = mapOf(
        "items" to emptyList<Any>(),
        "config" to emptyMap<String, Any>()
    )
    val output10 = KToon.encode(emptyData)
    println(output10)
    println()

    // Test 11: Special Characters
    println("11. Special Characters:")
    println("-".repeat(80))
    val specialChars = mapOf(
        "text" to "line1\nline2",
        "path" to "C:\\Users\\path",
        "quote" to "He said \"hello\"",
        "emoji" to "🚀",
        "unicode" to "café"
    )
    val output11 = KToon.encode(specialChars)
    println(output11)
    println()

    // Test 12: Numbers
    println("12. Numbers:")
    println("-".repeat(80))
    val numbers = mapOf(
        "int" to 42,
        "long" to 1234567890L,
        "double" to 3.14159,
        "negative" to -17,
        "zero" to 0,
        "float" to 2.5f
    )
    val output12 = KToon.encode(numbers)
    println(output12)
    println()

    // Test 13: Null Values
    println("13. Null Values:")
    println("-".repeat(80))
    val nullData = mapOf(
        "name" to "John",
        "middleName" to null,
        "age" to 30
    )
    val output13 = KToon.encode(nullData)
    println(output13)
    println()

    println("=".repeat(80))
    println("COMPARISON INSTRUCTIONS:")
    println("=".repeat(80))
    println("""
        To verify KToon produces identical output to JToon:
        
        1. Run JToon with the same test data
        2. Compare the outputs line by line
        3. They should be character-for-character identical
        
        Example JToon test:
        
        import com.amanjeet.jtoon.JToon;
        import java.util.Map;
        
        Map<String, Object> data = Map.of("id", 123, "name", "Ada", "active", true);
        System.out.println(JToon.encode(data));
        
        Expected output:
        id: 123
        name: Ada
        active: true
    """.trimIndent())
    println("=".repeat(80))
}

