package org.mrlem.pandoc.extensions

import org.mrlem.pandoc.enums.OutputFormat
import java.nio.file.Path

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