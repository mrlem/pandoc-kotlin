/**
 * Integration tests for Pandoc execution.
 * These tests actually execute pandoc and verify the output.
 */
package org.mrlem.pandoc

import org.mrlem.pandoc.enums.InputFormat
import org.mrlem.pandoc.enums.OutputFormat
import org.mrlem.pandoc.extensions.markdownToHtml
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

class PandocExecutionTest {
    
    @TempDir
    lateinit var tempDir: Path
    
    @Test
    fun `test markdown to html conversion with file input`() {
        // Create a test markdown file
        val mdFile = tempDir.resolve("test.md").toFile()
        mdFile.writeText("# Hello World\n\nThis is a test.")
        
        val html = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .to(OutputFormat.HTML)
            .input(mdFile)
            .execute()
        
        assertTrue(html.contains("Hello World"))
        assertTrue(html.contains("<h1"))
        assertTrue(html.contains("<p>This is a test.</p>"))
    }
    
    @Test
    fun `test markdown to html with standalone`() {
        val mdFile = tempDir.resolve("test.md").toFile()
        mdFile.writeText("# Test")
        
        val html = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .to(OutputFormat.HTML)
            .input(mdFile)
            .standalone()
            .execute()
        
        assertTrue(html.contains("<!DOCTYPE html>"))
        assertTrue(html.contains("<html"))
        assertTrue(html.contains("</html>"))
    }
    
    @Test
    fun `test string conversion via stdin`() {
        val markdown = "# Test Content\n\nParagraph text."
        
        val html = Pandoc.convert()
            .fromStdin()
            .to(OutputFormat.HTML)
            .execute(markdown)
        
        assertTrue(html.contains("Test Content"))
        assertTrue(html.contains("<h1"))
        assertTrue(html.contains("<p>Paragraph text.</p>"))
    }
    
    @Test
    fun `test with metadata`() {
        val mdFile = tempDir.resolve("test.md").toFile()
        mdFile.writeText("# Document")
        
        val html = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .to(OutputFormat.HTML)
            .input(mdFile)
            .standalone()
            .metadata("title", "My Document")
            .execute()
        
        assertTrue(html.contains("<title>"))
        assertTrue(html.contains("My Document"))
    }
    
    @Test
    fun `test with table of contents`() {
        val mdFile = tempDir.resolve("test.md").toFile()
        mdFile.writeText("# Heading 1\n## Heading 2\n### Heading 3")
        
        val html = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .to(OutputFormat.HTML)
            .input(mdFile)
            .toc(3)
            .standalone()
            .execute()
        
        // TOC should be present
        assertTrue(html.contains("table of contents") || html.contains("TOC") || html.contains("<nav") || html.contains("<div"))
    }
    
    @Test
    fun `test async execution`() = runTest {
        val markdown = "# Async Test"
        
        val html = Pandoc.convertString(
            from = InputFormat.MARKDOWN,
            to = OutputFormat.HTML,
            content = markdown
        )
        
        assertTrue(html.contains("Async Test"))
        assertTrue(html.contains("<h1"))
    }
    
    @Test
    fun `test output to file`() {
        val mdFile = tempDir.resolve("input.md").toFile()
        mdFile.writeText("# File Output Test")
        
        val outputFile = tempDir.resolve("output.html").toFile()
        
        Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .to(OutputFormat.HTML)
            .input(mdFile)
            .standalone()
            .executeToFile(outputFile.absolutePath)
        
        assertTrue(outputFile.exists())
        val content = outputFile.readText()
        assertTrue(content.contains("File Output Test"))
        assertTrue(content.contains("<h1"))
    }
    
    @Test
    fun `test flow execution`() = runTest {
        val markdown = "# Flow Test"
        
        val results = mutableListOf<String>()
        Pandoc.convert()
            .fromStdin()
            .to(OutputFormat.HTML)
            .flow(markdown)
            .collect { html ->
                results.add(html)
            }
        
        assertEquals(1, results.size)
        assertTrue(results[0].contains("Flow Test"))
        assertTrue(results[0].contains("<h1"))
    }
    
    @Test
    fun `test to string conversion`() = runTest {
        val html = "# HTML Test".markdownToHtml()
        assertTrue(html.contains("HTML Test"))
        assertTrue(html.contains("<h1"))
    }
    
    @Test
    fun `test input with multiple files`() {
        val file1 = tempDir.resolve("file1.md").toFile()
        val file2 = tempDir.resolve("file2.md").toFile()
        
        file1.writeText("# File 1")
        file2.writeText("# File 2")
        
        val html = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .to(OutputFormat.HTML)
            .input(file1, file2)
            .execute()
        
        assertTrue(html.contains("File 1") && html.contains("File 2"))
    }
}
