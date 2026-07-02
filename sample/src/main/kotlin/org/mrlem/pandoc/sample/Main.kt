package org.mrlem.pandoc.sample

import org.mrlem.pandoc.Pandoc
import org.mrlem.pandoc.enums.InputFormat
import org.mrlem.pandoc.enums.OutputFormat
import kotlinx.coroutines.runBlocking

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
            """.trimIndent()
        )
        .to(OutputFormat.HTML)
        .standalone()
        .toc(2)
        .outputString()
    println("html: $html")
}
