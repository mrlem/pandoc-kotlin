package org.mrlem.pandoc.extensions

import org.mrlem.pandoc.Pandoc
import org.mrlem.pandoc.enums.InputFormat
import org.mrlem.pandoc.enums.OutputFormat
import java.io.File

/**
 * Convert this File from Markdown to HTML.
 *
 * @return The HTML content as a string
 */
suspend fun File.toHtml(): String = Pandoc.convert()
    .from(InputFormat.MARKDOWN)
    .inputString(this.readText())
    .to(OutputFormat.HTML)
    .outputString()

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
    .outputString()

/**
 * Convert this File to the specified output format and write to another file.
 *
 * @param to The output format
 * @param outputFile The output file
 */
suspend fun File.convertTo(to: OutputFormat, outputFile: File) {
    Pandoc.convert()
        .from(InputFormat.MARKDOWN)
        .inputString(this.readText())
        .to(to)
        .outputFile(outputFile.absolutePath)
}