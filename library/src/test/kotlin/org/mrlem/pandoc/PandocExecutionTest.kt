/*
 * Integration tests for Pandoc execution.
 * These tests use resource files and verify conversions work correctly.
 */
package org.mrlem.pandoc

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mrlem.pandoc.enums.InputFormat
import org.mrlem.pandoc.enums.OutputFormat
import org.mrlem.pandoc.extensions.convertTo
import org.mrlem.pandoc.extensions.markdownToHtml
import org.mrlem.pandoc.extensions.toHtml
import java.io.File
import java.nio.file.Path

class PandocExecutionTest {
    
    @TempDir
    lateinit var tempDir: Path
    
    private fun getResource(path: String): String {
        return object {}.javaClass.getResourceAsStream("/$path")
            ?.bufferedReader()
            ?.readText()
            ?: error("Resource $path not found")
    }
    
    private fun copyResourceToTemp(path: String, destName: String = path): File {
        val resourceContent = getResource(path)
        val destFile = tempDir.resolve(destName).toFile()
        destFile.writeText(resourceContent)
        return destFile
    }
    
    @Test
    fun `test markdown to html conversion with file input`() {
        val mdFile = copyResourceToTemp("simple.md")
        
        val html = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .to(OutputFormat.HTML)
            .input(mdFile)
            .execute()
        
        assertTrue(html.contains("Hello World"))
        assertTrue(html.contains("simple markdown document"))
        assertTrue(html.contains("List item 1"))
        assertTrue(html.contains("List item 2"))
    }
    
    @Test
    fun `test markdown to html with standalone`() {
        val mdFile = copyResourceToTemp("standalone.md")
        
        val html = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .to(OutputFormat.HTML)
            .input(mdFile)
            .standalone()
            .execute()
        
        assertTrue(html.contains("<!DOCTYPE html>"))
        assertTrue(html.contains("<html"))
        assertTrue(html.contains("</html>"))
        assertTrue(html.contains("Test Document"))
        assertTrue(html.contains("Some content here"))
    }
    
    @Test
    fun `test string conversion via stdin`() {
        val markdown = "# Test Content\n\nParagraph text."
        
        val html = Pandoc.convert()
            .fromStdin()
            .to(OutputFormat.HTML)
            .execute(markdown)
        
        assertTrue(html.contains("Test Content"))
        assertTrue(html.contains("Paragraph text"))
    }
    
    @Test
    fun `test with metadata`() {
        val mdFile = copyResourceToTemp("standalone.md")
        
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
        val mdFile = copyResourceToTemp("with-toc.md")
        
        val html = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .to(OutputFormat.HTML)
            .input(mdFile)
            .toc(3)
            .standalone()
            .execute()
        
        assertTrue(html.contains("Heading 1"))
        assertTrue(html.contains("Heading 2"))
        assertTrue(html.contains("Heading 3"))
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
    }
    
    @Test
    fun `test output to file`() {
        val mdFile = copyResourceToTemp("simple.md", "input.md")
        val outputFile = tempDir.resolve("output.html").toFile()
        
        Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .to(OutputFormat.HTML)
            .input(mdFile)
            .standalone()
            .executeToFile(outputFile.absolutePath)
        
        assertTrue(outputFile.exists(), "Output file should be created")
        
        val content = outputFile.readText()
        assertTrue(content.contains("Hello World"), "Output should contain input content")
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
        
        assertEquals(1, results.size, "Flow should emit one result")
        assertTrue(results[0].contains("Flow Test"), "Result should contain input content")
    }
    
    @Test
    fun `test to string conversion`() = runTest {
        val html = "# HTML Test".markdownToHtml()
        assertTrue(html.contains("HTML Test"))
    }
    
    @Test
    fun `test input with multiple files`() {
        val file1 = copyResourceToTemp("multiple.md", "file1.md")
        val file2 = copyResourceToTemp("multiple2.md", "file2.md")
        
        val html = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .to(OutputFormat.HTML)
            .input(file1, file2)
            .execute()
        
        assertTrue(html.contains("File 1 Content"), "First file content should be present")
        assertTrue(html.contains("File 2 Content"), "Second file content should be present")
    }
    
    @Test
    fun `test file to file conversion`() = runTest {
        val mdFile = copyResourceToTemp("simple.md")
        val outputFile = tempDir.resolve("output.html").toFile()
        
        mdFile.convertTo(OutputFormat.HTML, outputFile)
        
        assertTrue(outputFile.exists())
        val content = outputFile.readText()
        assertTrue(content.contains("Hello World"))
    }
    
    @Test
    fun `test path to html conversion`() = runTest {
        val mdFile = copyResourceToTemp("simple.md")
        
        val html = mdFile.toPath().toHtml()
        assertTrue(html.contains("Hello World"))
    }
}
