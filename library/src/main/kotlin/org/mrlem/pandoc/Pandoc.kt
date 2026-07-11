/*
 * Fluent API for building and executing Pandoc commands.
 *
 * This module provides a type-safe, compile-time validated API for using pandoc
 * from Kotlin applications. The API uses a state-encoded builder pattern to ensure
 * that required fields are set before execution.
 */
package org.mrlem.pandoc

import org.mrlem.pandoc.states.Incomplete

/**
 * Main entry point for the Pandoc fluent API.
 *
 * Example usage:
 * ```kotlin
 * // Simple conversion from file to String
 * suspend fun example() {
 *     val html: String = Pandoc.convert()
 *         .from(InputFormat.MARKDOWN)
 *         .inputFile("readme.md")
 *         .to(OutputFormat.HTML)
 *         .standalone()
 *         .outputString()
 *
 *     // Simple conversion from string to String
 *     val html2: String = Pandoc.convert()
 *         .from(InputFormat.MARKDOWN)
 *         .inputString("# Hello")
 *         .to(OutputFormat.HTML)
 *         .outputString()
 *
 *     // Conversion from Reader to String
 *     val html3: String = Pandoc.convert()
 *         .from(InputFormat.MARKDOWN)
 *         .inputReader(reader)
 *         .to(OutputFormat.HTML)
 *         .outputString()
 *
 *     // Conversion to file
 *     Pandoc.convert()
 *         .from(InputFormat.MARKDOWN)
 *         .inputFile("input.md")
 *         .to(OutputFormat.HTML)
 *         .outputFile("output.html")
 *
 *     // Conversion to Writer
 *     val writer = java.io.StringWriter()
 *     Pandoc.convert()
 *         .from(InputFormat.MARKDOWN)
 *         .inputReader(reader)
 *         .to(OutputFormat.HTML)
 *         .outputWriter(writer)
 * }
 * ```
 */
object Pandoc {

    /**
     * Start building a pandoc conversion command.
     *
     * @return An [Incomplete] state ready for configuration
     */
    fun convert(): Incomplete = Incomplete()
}
