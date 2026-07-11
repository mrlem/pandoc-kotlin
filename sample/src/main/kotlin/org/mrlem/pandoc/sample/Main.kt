package org.mrlem.pandoc.sample

import kotlinx.coroutines.runBlocking
import org.mrlem.pandoc.Pandoc
import org.mrlem.pandoc.enums.InputFormat
import org.mrlem.pandoc.enums.OutputFormat

fun main() = runBlocking {
    val html = Pandoc.convert()
        .from(InputFormat.MARKDOWN)
        .inputString(
            """
            # Sample file

            Some sample text.

            ## Chapter 1: overview

            * item 1
            * item 2
            * item 3

            ## Chapter 2: detail

            Lorem ipsum dolor sit amet.
            """
                .trimIndent(),
        )
        .standalone()
        .toc(2)
        .to(OutputFormat.HTML)
        .outputString()
    println("html: $html")
}
