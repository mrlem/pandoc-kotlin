# 📄 Pandoc Kotlin

[![Maven Central](https://img.shields.io/maven-central/v/org.mrlem.pandoc/pandoc-kotlin)](https://search.maven.org/artifact/org.mrlem.pandoc/pandoc-kotlin)

A Kotlin library to convert documents between a variety of file formats, using Pandoc.

## Features

- **Fluent API**: Intuitive, chainable method calls for building pandoc commands
- **Type-Safe**: Strongly typed enums for all pandoc formats and options
- **Compile-Time Safety**: State-encoded builder pattern ensures required fields are set before execution
- **Coroutine Support**: All terminal operations are suspend functions
- **Comprehensive**: Supports 100+ pandoc options

## Installation

Add to your `build.gradle.kts`:

```kotlin
implementation("org.mrlem.pandoc:pandoc-kotlin:0.1.0")
```

## Quick Start

### Simple conversion

Markdown string to HTML string:

```kotlin
import org.mrlem.pandoc.Pandoc
import org.mrlem.pandoc.enums.InputFormat
import org.mrlem.pandoc.enums.OutputFormat

suspend fun convertFileToHtml() {
    val html = Pandoc.convert()
        .from(InputFormat.MARKDOWN)
        .inputString("# Hello World")
        .to(OutputFormat.HTML)
        .standalone()
        .outputString()
}
```

### More complex conversion

Markdown file to PDF file with template, table of content, metadata:

```kotlin
suspend fun convertWithOptions() {
    val html = Pandoc.convert()
        .from(InputFormat.MARKDOWN)
        .inputFile("readme.md")
        .to(OutputFormat.PDF)
        .standalone()
        .template("custom.html")
        .toc(2)
        .metadata("title", "My Document")
        .metadata("author", "John Doe")
        .outputFile("readme.pdf")
}
```

### Error handling

```kotlin
import org.mrlem.pandoc.exceptions.*

suspend fun convertWithErrorHandling() {
    try {
        val html = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .inputFile("nonexistent.md")
            .to(OutputFormat.HTML)
            .outputString()
    } catch (e: PandocExecutionException) {
        println("Exit code: ${e.exitCode}")
        println("Command: ${e.command}")
        println("Stderr: ${e.stderr}")
    } catch (e: PandocNotFoundException) {
        println("Pandoc not installed: ${e.message}")
    }
}
```

## Requirements

- Kotlin 2.4.0+
- Java 21+
- Pandoc 3.7.x installed on system PATH (for execution)

*Note: This library has been validated with Pandoc 3.7.0.2*