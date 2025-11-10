package examples

import com.amanjeet.ktoon.KToon
import com.amanjeet.ktoon.EncodeOptions
import com.amanjeet.ktoon.Delimiter

// Data classes representing a simple e-commerce domain
data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val inStock: Boolean,
    val tags: List<String>
)

data class Customer(
    val id: Int,
    val name: String,
    val email: String
)

data class OrderItem(
    val sku: String,
    val quantity: Int,
    val price: Double
)

data class Order(
    val orderId: String,
    val customer: Customer,
    val items: List<OrderItem>,
    val total: Double
)

fun main() {
    println("=".repeat(70))
    println("KToon Demo - Kotlin Implementation of TOON Format")
    println("=".repeat(70))
    println()

    // Example 1: Simple data class
    println("1. Simple Data Class:")
    println("-".repeat(70))
    val product = Product(
        id = "PROD-001",
        name = "Wireless Mouse",
        price = 29.99,
        inStock = true,
        tags = listOf("electronics", "accessories", "wireless")
    )
    println(KToon.encode(product))
    println()

    // Example 2: Nested objects
    println("2. Nested Objects:")
    println("-".repeat(70))
    val customer = Customer(id = 123, name = "Ada Lovelace", email = "ada@example.com")
    val order = Order(
        orderId = "ORD-2025-001",
        customer = customer,
        items = listOf(
            OrderItem("PROD-001", 2, 29.99),
            OrderItem("PROD-002", 1, 49.99)
        ),
        total = 109.97
    )
    println(KToon.encode(order))
    println()

    // Example 3: Using Pipe Delimiter
    println("3. Using Pipe Delimiter:")
    println("-".repeat(70))
    val pipeOptions = EncodeOptions(delimiter = Delimiter.PIPE)
    val items = mapOf(
        "products" to listOf(
            mapOf("id" to "A1", "name" to "Widget", "qty" to 10),
            mapOf("id" to "B2", "name" to "Gadget", "qty" to 5)
        )
    )
    println(KToon.encode(items, pipeOptions))
    println()

    // Example 4: Using Tab Delimiter
    println("4. Using Tab Delimiter:")
    println("-".repeat(70))
    val tabOptions = EncodeOptions(delimiter = Delimiter.TAB)
    println(KToon.encode(items, tabOptions))
    println()

    // Example 5: Using Length Marker
    println("5. Using Length Marker:")
    println("-".repeat(70))
    val lengthMarkerOptions = EncodeOptions(lengthMarker = true)
    val data = mapOf(
        "categories" to listOf("Electronics", "Accessories", "Computers"),
        "tags" to listOf("new", "sale", "featured")
    )
    println(KToon.encode(data, lengthMarkerOptions))
    println()

    // Example 6: Custom Indentation
    println("6. Custom Indentation (4 spaces):")
    println("-".repeat(70))
    val indentOptions = EncodeOptions(indent = 4)
    val nestedData = mapOf(
        "company" to mapOf(
            "name" to "Tech Corp",
            "location" to mapOf(
                "city" to "San Francisco",
                "country" to "USA"
            )
        )
    )
    println(KToon.encode(nestedData, indentOptions))
    println()

    // Example 7: Encoding JSON String
    println("7. Encoding JSON String:")
    println("-".repeat(70))
    val jsonString = """
    {
        "users": [
            {"id": 1, "name": "Alice", "role": "admin"},
            {"id": 2, "name": "Bob", "role": "user"}
        ]
    }
    """.trimIndent()
    println(KToon.encodeJson(jsonString))
    println()

    // Example 8: Complex Nested Structure
    println("8. Complex Nested Structure:")
    println("-".repeat(70))
    val complexData = mapOf(
        "dashboard" to mapOf(
            "user" to mapOf("name" to "Admin", "role" to "superuser"),
            "metrics" to listOf(
                mapOf("name" to "CPU", "value" to 45.2, "unit" to "%"),
                mapOf("name" to "Memory", "value" to 78.5, "unit" to "%"),
                mapOf("name" to "Disk", "value" to 62.0, "unit" to "%")
            ),
            "alerts" to listOf("High CPU usage", "Low disk space")
        )
    )
    println(KToon.encode(complexData))
    println()

    // Example 9: Token Comparison
    println("9. Token Efficiency Comparison:")
    println("-".repeat(70))
    val comparisonData = mapOf(
        "repositories" to listOf(
            mapOf("id" to 1, "name" to "kotlin", "stars" to 45000, "language" to "Kotlin"),
            mapOf("id" to 2, "name" to "spring", "stars" to 52000, "language" to "Java"),
            mapOf("id" to 3, "name" to "react", "stars" to 200000, "language" to "JavaScript")
        )
    )
    
    val toonOutput = KToon.encode(comparisonData)
    val jsonOutput = """{"repositories":[{"id":1,"name":"kotlin","stars":45000,"language":"Kotlin"},{"id":2,"name":"spring","stars":52000,"language":"Java"},{"id":3,"name":"react","stars":200000,"language":"JavaScript"}]}"""
    
    println("TOON format:")
    println(toonOutput)
    println()
    println("TOON length: ${toonOutput.length} characters")
    println("JSON length: ${jsonOutput.length} characters")
    println("Savings: ${((jsonOutput.length - toonOutput.length) * 100.0 / jsonOutput.length).toInt()}%")
    println()

    // Example 10: Kotlin-specific features
    println("10. Kotlin-specific Features:")
    println("-".repeat(70))
    
    // Nullable types
    data class UserProfile(val name: String, val bio: String?, val age: Int?)
    val profile = UserProfile(name = "John Doe", bio = null, age = 30)
    println("Nullable types:")
    println(KToon.encode(profile))
    println()
    
    // Sequences (lazy collections)
    val sequence = sequenceOf(1, 2, 3, 4, 5)
    println("Sequence:")
    println(KToon.encode(mapOf("numbers" to sequence)))
    println()

    println("=".repeat(70))
    println("KToon Demo Complete!")
    println("=".repeat(70))
}
