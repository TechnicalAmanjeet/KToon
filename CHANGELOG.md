# Changelog

All notable changes to KToon will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.0] - 2025-11-07

### Added
- Initial release of KToon (Kotlin implementation of TOON format)
- Core encoding functionality for converting Kotlin/Java objects to TOON format
- Support for data classes, maps, lists, and primitive types
- JsonNormalizer for handling Java/Kotlin type conversions:
  - Temporal types (LocalDateTime, LocalDate, Instant, etc.) to ISO strings
  - Nullable types without Optional wrapper
  - Sequences materialized to arrays
  - Collections and Maps
  - BigInteger and BigDecimal
  - Primitive arrays
- Encoder package with specialized encoders:
  - ValueEncoder - orchestrates encoding
  - ObjectEncoder - handles JSON objects
  - ArrayEncoder - detects and routes array types
  - TabularArrayEncoder - efficient tabular format for uniform arrays
  - ListItemEncoder - handles non-uniform arrays
  - PrimitiveEncoder - encodes primitive values
  - HeaderFormatter - formats array/table headers
  - LineWriter - manages indented output
- Utility classes:
  - StringValidator - determines quoting needs
  - StringEscaper - escapes special characters
  - Constants - shared constants
- Configuration options:
  - Custom indentation (default: 2 spaces)
  - Delimiter selection (COMMA, TAB, PIPE)
  - Length marker option
- Comprehensive test suite
- Full documentation and README
- Demo examples showing various use cases
- Gradle build configuration
- MIT License

### Features
- 🎯 Idiomatic Kotlin implementation
- ☕ Full Java interoperability
- 💸 30-60% token reduction vs JSON
- 🧺 Tabular format for uniform object arrays
- 📐 YAML-style indentation
- 🍱 Minimal syntax with smart quoting
- 🔧 Configurable encoding options

[0.1.0]: https://github.com/TechnicalAmanjeet/KToon/releases/tag/v0.1.0

