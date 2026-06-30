/**
 * Extension functions for convenient pandoc conversions.
 */
package com.github.sebguillemin.pandoc.extensions

import com.github.sebguillemin.pandoc.Pandoc
import com.github.sebguillemin.pandoc.enums.InputFormat
import com.github.sebguillemin.pandoc.enums.OutputFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.nio.file.Path

/**
 * Convert this File from Markdown to HTML.
 * 
 * @return The HTML content as a string
 */
suspend fun File.toHtml(): String = Pandoc.convertString(
    from = InputFormat.MARKDOWN,
    to = OutputFormat.HTML,
    content = this.readText()
)

/**
 * Convert this File to the specified output format.
 * 
 * @param to The output format
 * @return The converted content as a string
 */
suspend fun File.convertTo(to: OutputFormat): String = Pandoc.convertString(
    from = InputFormat.MARKDOWN,
    to = to,
    content = this.readText()
)

/**
 * Convert this File to the specified output format and write to another file.
 * 
 * @param to The output format
 * @param outputFile The output file
 */
suspend fun File.convertTo(to: OutputFormat, outputFile: File) {
    val content = Pandoc.convertString(
        from = InputFormat.MARKDOWN,
        to = to,
        content = this.readText()
    )
    outputFile.writeText(content)
}

/**
 * Convert this String from Markdown to HTML.
 * 
 * @return The HTML content as a string
 */
suspend fun String.markdownToHtml(): String = Pandoc.convertString(
    from = InputFormat.MARKDOWN,
    to = OutputFormat.HTML,
    content = this
)

/**
 * Convert this String from the specified input format to the specified output format.
 * 
 * @param from The input format
 * @param to The output format
 * @return The converted content as a string
 */
suspend fun String.convert(from: InputFormat, to: OutputFormat): String = Pandoc.convertString(
    from = from,
    to = to,
    content = this
)

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

/**
 * Convert a Flow of strings (Markdown) to a Flow of HTML strings.
 * 
 * Note: Each element is converted on Dispatchers.IO.
 * 
 * @return A Flow of HTML strings
 */
fun Flow<String>.markdownToHtml(): Flow<String> = flow {
    collect { markdown ->
        emit(markdown.markdownToHtml())
    }
}.flowOn(Dispatchers.IO)

/**
 * Convert a Flow of strings from one format to another.
 * 
 * Note: Each element is converted on Dispatchers.IO.
 * 
 * @param from The input format
 * @param to The output format
 * @return A Flow of converted strings
 */
fun Flow<String>.convert(from: InputFormat, to: OutputFormat): Flow<String> = flow {
    collect { content ->
        emit(content.convert(from, to))
    }
}.flowOn(Dispatchers.IO)
