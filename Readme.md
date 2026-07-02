# 📄 pandoc-kt

A Kotlin library to convert documents between a variety of file formats, using Pandoc.

## Features

- **Fluent API**: Intuitive, chainable method calls for building pandoc commands
- **Type-Safe**: Strongly typed enums for all pandoc formats and options
- **Compile-Time Safety**: State-encoded builder pattern ensures required fields are set before execution
- **Coroutine Support**: Async operations with `suspend` functions and Kotlin Flow
- **Comprehensive**: Supports 100+ pandoc options

## Installation

Add to your `build.gradle.kts`:

```kotlin
implementation("org.mrlem.pandoc:pandoc-kt:0.1.0")
```

## Quick Start

### Simple Conversion

```kotlin
import org.mrlem.pandoc.Pandoc
import org.mrlem.pandoc.enums.InputFormat
import org.mrlem.pandoc.enums.OutputFormat

val html = Pandoc.convert()
    .from(InputFormat.MARKDOWN)
    .to(OutputFormat.HTML)
    .input("readme.md")
    .standalone()
    .execute()
```

### Async Conversion

```kotlin
suspend fun convertAsync() {
    val result = Pandoc.convert()
        .from(InputFormat.MARKDOWN)
        .to(OutputFormat.PDF)
        .input("input.md")
        .output("output.pdf")
        .executeAsync()
}
```

### String Conversion

```kotlin
suspend fun convertString() {
    val html = Pandoc.convertString(
        from = InputFormat.MARKDOWN,
        to = OutputFormat.HTML,
        content = "# Hello World"
    )
}
```

### Using Flow

```kotlin
import kotlinx.coroutines.flow.collect

Pandoc.convert()
    .from(InputFormat.MARKDOWN)
    .to(OutputFormat.HTML)
    .input("input.md")
    .flow()
    .collect { html ->
        println(html)
    }
```

## Fluent API

### Starting Points

```kotlin
// Start with no configuration
Pandoc.convert()

// Start with input format
Pandoc.convert().from(InputFormat.MARKDOWN)

// Start with input files
Pandoc.convert().input("file1.md", "file2.md")

// Start with stdin
Pandoc.convert().fromStdin()

// Start with output format
Pandoc.convert().to(OutputFormat.HTML)
```

### Common Options

```kotlin
Pandoc.convert()
    .from(InputFormat.MARKDOWN)
    .to(OutputFormat.HTML)
    .input("readme.md")
    .standalone()
    .template("custom.html")
    .toc(2)
    .metadata("title", "My Document")
    .metadata("author", "John Doe")
    .execute()
```

## Compile-Time Safety

The library uses Kotlin's sealed class hierarchy to provide compile-time validation.

## Error Handling

```kotlin
import org.mrlem.pandoc.exceptions.*

try {
    val html = Pandoc.convert()
        .from(InputFormat.MARKDOWN)
        .to(OutputFormat.HTML)
        .input("nonexistent.md")
        .execute()
} catch (e: PandocExecutionException) {
    println("Exit code: ${e.exitCode}")
    println("Command: ${e.command}")
    println("Stderr: ${e.stderr}")
} catch (e: PandocNotFoundException) {
    println("Pandoc not installed: ${e.message}")
}
```

## Requirements

- Kotlin 2.4.0+
- Java 21+
- Pandoc 3.7.x installed on system PATH (for execution)

*Note: This library has been validated with Pandoc 3.7.0.2*

## Contributing

This library follows semantic versioning and uses conventional commits.

## License

MIT License - see LICENSE file for details.
