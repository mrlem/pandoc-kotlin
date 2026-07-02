/**
 * Extension functions for convenient pandoc conversions.
 */
package org.mrlem.pandoc.extensions

import org.mrlem.pandoc.Pandoc
import org.mrlem.pandoc.enums.InputFormat
import org.mrlem.pandoc.enums.OutputFormat
import java.io.File
import java.nio.file.Path

/**
 * Convert this File from Markdown to HTML.
 * 
 * @return The HTML content as a string
 */
suspend fun File.toHtml(): String = Pandoc.convert()
    .from(InputFormat.MARKDOWN)
    .inputString(this.readText())
    .to(OutputFormat.HTML)
    .executeAsync()

/**
 * Convert this File to the specified output format.
 * 
 * @param to The output format
 * @return The converted content as a string
 */
suspend fun File.convertTo(to: OutputFormat): String = Pandoc.convert()
    .from(InputFormat.MARKDOWN)
    .inputString(this.readText())
    .to(to)
    .executeAsync()

/**
 * Convert this File to the specified output format and write to another file.
 * 
 * @param to The output format
 * @param outputFile The output file
 */
suspend fun File.convertTo(to: OutputFormat, outputFile: File) {
    val content = Pandoc.convert()
        .from(InputFormat.MARKDOWN)
        .inputString(this.readText())
        .to(to)
        .executeAsync()
    outputFile.writeText(content)
}

/**
 * Convert this String from Markdown to HTML.
 * 
 * @return The HTML content as a string
 */
suspend fun String.markdownToHtml(): String = Pandoc.convert()
    .from(InputFormat.MARKDOWN)
    .inputString(this)
    .to(OutputFormat.HTML)
    .executeAsync()

/**
 * Convert this String from the specified input format to the specified output format.
 * 
 * @param from The input format
 * @param to The output format
 * @return The converted content as a string
 */
suspend fun String.convert(from: InputFormat, to: OutputFormat): String = Pandoc.convert()
    .from(from)
    .inputString(this)
    .to(to)
    .executeAsync()

/**
 * Convert this Path from Markdown to HTML.
 * 
 * @return The HTML content as a string
 */
suspend fun Path.toHtml(): String = this.toFile().toHtml()

/**
 * Convert this Path to the specified output format.
 * 
 * @param to The output format
 * @return The converted content as a string
 */
suspend fun Path.convertTo(to: OutputFormat): String = this.toFile().convertTo(to)
