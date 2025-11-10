# Differences Between KToon and JToon

This document outlines the key differences between KToon (Kotlin implementation) and JToon (Java implementation).

## Language-Specific Features

### KToon (Kotlin)

#### 1. **Object Declarations**
```kotlin
// KToon uses object for singletons
object KToon {
    fun encode(input: Any?): String
}

// Usage
KToon.encode(myData)
```

#### 2. **Data Classes**
```kotlin
// Kotlin data classes work naturally
data class User(val id: Int, val name: String)
val user = User(123, "Ada")
KToon.encode(user)
```

#### 3. **Nullable Types**
```kotlin
// No Optional needed - native null safety
val name: String? = null
KToon.encode(name) // outputs: null
```

#### 4. **Extension Functions**
```kotlin
// Could add extension functions (future enhancement)
fun Any.toToon(): String = KToon.encode(this)
```

#### 5. **Named Parameters**
```kotlin
// More readable option configuration
val options = EncodeOptions(
    indent = 4,
    delimiter = Delimiter.PIPE,
    lengthMarker = true
)
```

#### 6. **When Expressions**
```kotlin
// More concise than Java switch
when {
    value.isValueNode -> encodePrimitive(value)
    value.isArray -> encodeArray(value)
    value.isObject -> encodeObject(value)
}
```

#### 7. **Sequences**
```kotlin
// Lazy sequences are materialized automatically
val seq = sequenceOf(1, 2, 3, 4, 5)
KToon.encode(seq) // [5]: 1,2,3,4,5
```

### JToon (Java)

#### 1. **Final Classes with Private Constructors**
```java
// JToon uses final utility classes
public final class JToon {
    private JToon() {
        throw new UnsupportedOperationException();
    }
    
    public static String encode(Object input) { }
}

// Usage
JToon.encode(myData);
```

#### 2. **Records**
```java
// Java 17+ records
record User(int id, String name) {}
User user = new User(123, "Ada");
JToon.encode(user);
```

#### 3. **Optional Type**
```java
// Java uses Optional for nullable values
Optional<String> name = Optional.empty();
JToon.encode(name); // outputs: null
```

#### 4. **No Extension Functions**
```java
// Standard static method calls only
String result = JToon.encode(myObject);
```

#### 5. **Constructor Overloading**
```java
// Options require explicit constructor
EncodeOptions options = new EncodeOptions(4, Delimiter.PIPE, true);
```

#### 6. **Switch Expressions**
```java
// Java switch (Java 17+)
return switch (value.getNodeType()) {
    case BOOLEAN -> String.valueOf(value.asBoolean());
    case NUMBER -> value.asText();
    case STRING -> encodeStringLiteral(value.asText());
    default -> NULL_LITERAL;
};
```

#### 7. **Streams**
```java
// Streams are materialized
Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5);
JToon.encode(stream); // [5]: 1,2,3,4,5
```

## Code Structure Comparison

### Package Organization
Both projects use the same package structure:
```
com.amanjeet.{j|k}toon/
├── encoder/          # Encoding logic
├── normalizer/       # Type normalization
└── util/            # Utilities
```

### API Similarity

**JToon (Java):**
```java
String result = JToon.encode(data);
String result = JToon.encode(data, options);
String result = JToon.encodeJson(jsonString);
```

**KToon (Kotlin):**
```kotlin
val result = KToon.encode(data)
val result = KToon.encode(data, options)
val result = KToon.encodeJson(jsonString)
```

## Implementation Details

### Type Normalization

#### KToon
- Handles nullable types natively without Optional
- Sequences are converted via `toList()`
- Extension property `.isEmpty` instead of `.isEmpty()`
- Uses Kotlin's `isNullOrBlank()` for string checks

#### JToon
- Handles Optional explicitly
- Streams are converted via `collect(Collectors.toList())`
- Method call `.isEmpty()` on collections
- Manual null/empty checks

### String Building

#### KToon
```kotlin
// Uses buildString and string templates
buildString {
    append(key)
    append(": ")
    append(value)
}
```

#### JToon
```java
// Uses StringBuilder
StringBuilder sb = new StringBuilder();
sb.append(key);
sb.append(": ");
sb.append(value);
return sb.toString();
```

### Collection Operations

#### KToon
```kotlin
// Functional style with Kotlin collections
values.joinToString(delimiter) { encode(it) }
array.all { it.isValueNode }
header.map { obj[it] }
```

#### JToon
```java
// Stream API
values.stream()
    .map(v -> encode(v))
    .reduce((a, b) -> a + delimiter + b)
    .orElse("");
```

## Testing

### KToon
- Uses Kotlin Test and JUnit 5
- `kotlin.test.assertEquals`
- Nested inner classes with `@Nested`
- Data classes for test fixtures

### JToon
- Uses JUnit 5
- `org.junit.jupiter.api.Assertions.assertEquals`
- Nested static classes with `@Nested`
- Records for test fixtures (Java 17+)

## Build System

### KToon
- Uses Gradle Kotlin DSL (`build.gradle.kts`)
- Kotlin Gradle Plugin
- Jackson Kotlin Module

### JToon
- Uses Gradle Groovy DSL (`build.gradle`)
- Standard Java build
- Jackson Databind only

## Performance

Both implementations have similar performance characteristics as they:
- Use the same Jackson library for JSON handling
- Follow the same encoding algorithms
- Generate identical TOON output

## When to Choose Which?

### Choose KToon if:
- You're working in a Kotlin project
- You want idiomatic Kotlin code
- You prefer null-safety built into the type system
- You want to use Kotlin-specific features (data classes, extension functions)
- You need seamless integration with Kotlin coroutines (future enhancement)

### Choose JToon if:
- You're working in a pure Java project
- You want maximum compatibility with older Java versions
- Your team is more familiar with Java
- You prefer explicit type handling with Optional

## Future Enhancements (Potential)

### KToon-specific
- Coroutine support for async encoding
- Extension functions for common types
- Kotlin multiplatform support (JS, Native)
- DSL for building TOON structures

### JToon-specific
- Virtual threads support (Java 21+)
- Enhanced pattern matching (Java 21+)
- Record patterns for destructuring

## Conclusion

Both implementations are functionally equivalent and produce identical TOON output. The choice between them depends primarily on your project's language ecosystem and team preferences. KToon provides a more idiomatic experience for Kotlin developers while maintaining full Java interoperability.

