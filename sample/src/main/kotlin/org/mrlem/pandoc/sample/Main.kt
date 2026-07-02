package org.mrlem.pandoc.sample

import org.mrlem.pandoc.Pandoc
import org.mrlem.pandoc.enums.InputFormat
import org.mrlem.pandoc.enums.OutputFormat

fun main() {
    val html = Pandoc.convert()
        .from(InputFormat.MARKDOWN)
        .to(OutputFormat.HTML)
        .fromStdin()
        .standalone()
        .toc(2)
        .execute(
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
    println("html: $html")
}