package org.mrlem.pandoc.extensions

import org.mrlem.pandoc.Pandoc
import org.mrlem.pandoc.enums.InputFormat
import org.mrlem.pandoc.enums.OutputFormat

/**
 * Convert this String from Markdown to HTML.
 *
 * @return The HTML content as a string
 */
suspend fun String.markdownToHtml(): String = Pandoc.convert()
    .from(InputFormat.MARKDOWN)
    .inputString(this)
    .to(OutputFormat.HTML)
    .outputString()

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
    .outputString()
