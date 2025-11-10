# KToon - Token-Oriented Object Notation (Kotlin Implementation)

[![Kotlin](https://img.shields.io/badge/kotlin-1.9.21-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**Token-Oriented Object Notation (TOON)** is a compact, human-readable format designed for passing structured data to Large Language Models with significantly reduced token usage.

KToon is the **Kotlin implementation** of the TOON format, offering idiomatic Kotlin features and seamless Java interoperability.

## Why TOON?

AI is becoming cheaper and more accessible, but **LLM tokens still cost money**. Standard JSON is verbose and token-expensive:

```json
{
  "users": [
    { "id": 1, "name": "Alice", "role": "admin" },
    { "id": 2, "name": "Bob", "role": "user" }
  ]
}
```

TOON conveys the same information with **30-60% fewer tokens**:

```
users[2]{id,name,role}:
  1,Alice,admin
  2,Bob,user
```

## Features

- 💸 **Token-efficient:** typically 30–60% fewer tokens than JSON
- 🤿 **LLM-friendly guardrails:** explicit lengths and field lists help models validate output
- 🍱 **Minimal syntax:** removes redundant punctuation (braces, brackets, most quotes)
- 📐 **Indentation-based structure:** replaces braces with whitespace for better readability
- 🧺 **Tabular arrays:** declare keys once, then stream rows without repetition
- 🎯 **Kotlin-native:** idiomatic Kotlin with data classes, null safety, and extension functions
- ☕ **Java interop:** fully compatible with Java projects

## Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("com.amanjeet:ktoon:0.1.0")
}
```

### Gradle (Groovy DSL)

```gradle
dependencies {
    implementation 'com.amanjeet:ktoon:0.1.0'
}
```

### Maven

```xml
<dependency>
    <groupId>com.amanjeet</groupId>
    <artifactId>ktoon</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Quick Start

### Basic Usage

```kotlin
import com.amanjeet.ktoon.KToon

data class User(val id: Int, val name: String, val tags: List<String>, val active: Boolean)
data class Data(val user: User)

val user = User(123, "Ada", listOf("reading", "gaming"), true)
val data = Data(user)

println(KToon.encode(data))
```

**Output:**

```
user:
  id: 123
  name: Ada
  tags[2]: reading,gaming
  active: true
```

### Encoding Data Classes

```kotlin
data class Item(val sku: String, val qty: Int, val price: Double)
data class Order(val items: List<Item>)

val item1 = Item("A1", 2, 9.99)
val item2 = Item("B2", 1, 14.5)
val order = Order(listOf(item1, item2))

println(KToon.encode(order))
```

**Output:**

```
items[2]{sku,qty,price}:
  A1,2,9.99
  B2,1,14.5
```

### Encoding JSON Strings

```kotlin
val json = """
{
  "user": {
    "id": 123,
    "name": "Ada",
    "tags": ["reading", "gaming"]
  }
}
"""

println(KToon.encodeJson(json))
```

**Output:**

```
user:
  id: 123
  name: Ada
  tags[2]: reading,gaming
```

## Custom Options

### Delimiter Options

```kotlin
import com.amanjeet.ktoon.EncodeOptions
import com.amanjeet.ktoon.Delimiter

data class Item(val sku: String, val name: String, val qty: Int, val price: Double)
data class Data(val items: List<Item>)

val data = Data(
    items = listOf(
        Item("A1", "Widget", 2, 9.99),
        Item("B2", "Gadget", 1, 14.5)
    )
)

// Tab delimiter
val tabOptions = EncodeOptions(delimiter = Delimiter.TAB)
println(KToon.encode(data, tabOptions))
// Output:
// items[2	]{sku	name	qty	price}:
//   A1	Widget	2	9.99
//   B2	Gadget	1	14.5

// Pipe delimiter
val pipeOptions = EncodeOptions(delimiter = Delimiter.PIPE)
println(KToon.encode(data, pipeOptions))
// Output:
// items[2|]{sku|name|qty|price}:
//   A1|Widget|2|9.99
//   B2|Gadget|1|14.5
```

### Length Marker

```kotlin
val options = EncodeOptions(lengthMarker = true)
val data = mapOf("tags" to listOf("reading", "gaming", "coding"))

println(KToon.encode(data, options))
// Output: tags[#3]: reading,gaming,coding
```

### Custom Indentation

```kotlin
val options = EncodeOptions(indent = 4)
val data = mapOf("user" to mapOf("id" to 123, "name" to "Ada"))

println(KToon.encode(data, options))
// Output:
// user:
//     id: 123
//     name: Ada
```

## API Reference

### KToon Object

```kotlin
object KToon {
    // Encode with default options
    fun encode(input: Any?): String
    
    // Encode with custom options
    fun encode(input: Any?, options: EncodeOptions): String
    
    // Encode JSON string with default options
    fun encodeJson(json: String): String
    
    // Encode JSON string with custom options
    fun encodeJson(json: String, options: EncodeOptions): String
}
```

### EncodeOptions

```kotlin
data class EncodeOptions(
    val indent: Int = 2,
    val delimiter: Delimiter = Delimiter.COMMA,
    val lengthMarker: Boolean = false
)

// Factory methods
EncodeOptions.withIndent(4)
EncodeOptions.withDelimiter(Delimiter.PIPE)
EncodeOptions.withLengthMarker(true)
```

### Delimiter

```kotlin
enum class Delimiter {
    COMMA,  // , (default)
    TAB,    // \t
    PIPE    // |
}
```

## Type Conversions

KToon automatically handles Kotlin and Java types:

| Input Type | Output |
|---|---|
| Number (finite) | Decimal form; `-0` → `0`; whole numbers as integers |
| Number (`NaN`, `±Infinity`) | `null` |
| `BigInteger` | Integer if within Long range, otherwise string (no quotes) |
| `BigDecimal` | Decimal number |
| `LocalDateTime` | ISO date-time string in quotes |
| `LocalDate` | ISO date string in quotes |
| `LocalTime` | ISO time string in quotes |
| `ZonedDateTime` | ISO zoned date-time string in quotes |
| `OffsetDateTime` | ISO offset date-time string in quotes |
| `Instant` | ISO instant string in quotes |
| `java.util.Date` | ISO instant string in quotes |
| Nullable types (`T?`) | Unwrapped value or `null` if null |
| `Sequence<T>` | Materialized to array |
| `Map` | Object with string keys |
| `Collection`, arrays | Arrays |

## Kotlin Features

KToon leverages Kotlin's powerful features:

- **Data classes:** Natural serialization of Kotlin data classes
- **Null safety:** Native handling of nullable types (no `Optional` needed)
- **Extension functions:** Clean, expressive API
- **Sequences:** Efficient handling of lazy collections
- **Type inference:** Less boilerplate code
- **Named parameters:** Flexible options configuration

## Java Interoperability

KToon works seamlessly with Java:

```java
import com.amanjeet.ktoon.KToon;
import com.amanjeet.ktoon.EncodeOptions;
import com.amanjeet.ktoon.Delimiter;

// Simple encoding
String result = KToon.INSTANCE.encode(myObject);

// With options
EncodeOptions options = new EncodeOptions(2, Delimiter.COMMA, false);
String result = KToon.INSTANCE.encode(myObject, options);
```

## Building from Source

```bash
git clone https://github.com/TechnicalAmanjeet/KToon.git
cd KToon
./gradlew build
```

## Running Tests

```bash
./gradlew test
```

## TOON Format Specification
### Original Specification
- **TOON Format:** Created by [Johann Schopplich](https://github.com/johannschopplich)
- **TypeScript Implementation:** [@johannschopplich/toon](https://github.com/johannschopplich/toon)

## Implementations in Other Languages

- **Kotlin:** [KToon](https://github.com/TechnicalAmanjeet/KToon)
- **Java:** [JToon](https://github.com/felipestanzani/JToon)
- **TypeScript/JavaScript:** [@johannschopplich/toon](https://github.com/johannschopplich/toon) (original)
- **Elixir:** [toon_ex](https://github.com/kentaro/toon_ex)
- **PHP:** [toon-php](https://github.com/HelgeSverre/toon-php)
- **Python:** [python-toon](https://github.com/xaviviro/python-toon) or [pytoon](https://github.com/bpradana/pytoon)
- **Ruby:** [toon-ruby](https://github.com/andrepcg/toon-ruby)
- **.NET:** [toon.NET](https://github.com/ghost1face/toon.NET)
- **Swift:** [TOONEncoder](https://github.com/mattt/TOONEncoder)
- **Go:** [gotoon](https://github.com/alpkeskin/gotoon)
- **Rust:** [toon-rs](https://github.com/JadJabbour/toon-rs)

## License

[MIT](./LICENSE) License © 2025-PRESENT [Amanjeet](https://amanjeet.com)

## Credits

KToon is a Kotlin port of TOON specification originally created by [Johann Schopplich](https://github.com/johannschopplich).

