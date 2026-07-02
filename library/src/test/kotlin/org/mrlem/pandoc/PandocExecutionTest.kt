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
import java.io.StringReader
import java.io.StringWriter
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
    fun `test markdown to html conversion with file input`() = runTest {
        val mdFile = copyResourceToTemp("simple.md")
        
        val html = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .inputFile(mdFile)
            .to(OutputFormat.HTML)
            .outputString()
        
        assertTrue(html.contains("Hello World"))
        assertTrue(html.contains("simple markdown document"))
        assertTrue(html.contains("List item 1"))
        assertTrue(html.contains("List item 2"))
    }
    
    @Test
    fun `test markdown to html with standalone`() = runTest {
        val mdFile = copyResourceToTemp("standalone.md")
        
        val html = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .inputFile(mdFile)
            .to(OutputFormat.HTML)
            .standalone()
            .outputString()
        
        assertTrue(html.contains("<!DOCTYPE html>"))
        assertTrue(html.contains("<html"))
        assertTrue(html.contains("</html>"))
        assertTrue(html.contains("Test Document"))
        assertTrue(html.contains("Some content here"))
    }
    
    @Test
    fun `test string conversion via inputString`() = runTest {
        val markdown = "# Test Content\n\nParagraph text."
        
        val html = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .inputString(markdown)
            .to(OutputFormat.HTML)
            .outputString()
        
        assertTrue(html.contains("Test Content"))
        assertTrue(html.contains("Paragraph text"))
    }
    
    @Test
    fun `test with metadata`() = runTest {
        val mdFile = copyResourceToTemp("standalone.md")
        
        val html = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .inputFile(mdFile)
            .to(OutputFormat.HTML)
            .standalone()
            .metadata("title", "My Document")
            .outputString()
        
        assertTrue(html.contains("<title>"))
        assertTrue(html.contains("My Document"))
    }
    
    @Test
    fun `test with table of contents`() = runTest {
        val mdFile = copyResourceToTemp("with-toc.md")
        
        val html = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .inputFile(mdFile)
            .to(OutputFormat.HTML)
            .toc(3)
            .standalone()
            .outputString()
        
        assertTrue(html.contains("Heading 1"))
        assertTrue(html.contains("Heading 2"))
        assertTrue(html.contains("Heading 3"))
    }
    
    @Test
    fun `test async execution`() = runTest {
        val markdown = "# Async Test"
        
        val html = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .inputString(markdown)
            .to(OutputFormat.HTML)
            .outputString()
        
        assertTrue(html.contains("Async Test"))
    }
    
    @Test
    fun `test output to file`() = runTest {
        val mdFile = copyResourceToTemp("simple.md", "input.md")
        val outputFile = tempDir.resolve("output.html").toFile()
        
        Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .inputFile(mdFile)
            .to(OutputFormat.HTML)
            .standalone()
            .outputFile(outputFile.absolutePath)
        
        assertTrue(outputFile.exists(), "Output file should be created")
        
        val content = outputFile.readText()
        assertTrue(content.contains("Hello World"), "Output should contain input content")
    }
    
    @Test
    fun `test inputReader conversion`() = runTest {
        val markdown = "# Reader Test"
        val reader = StringReader(markdown)
        
        val html = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .inputReader(reader)
            .to(OutputFormat.HTML)
            .outputString()
        
        assertTrue(html.contains("Reader Test"))
    }
    
    @Test
    fun `test to string conversion`() = runTest {
        val html = "# HTML Test".markdownToHtml()
        assertTrue(html.contains("HTML Test"))
    }
    
    @Test
    fun `test input with multiple files`() = runTest {
        val file1 = copyResourceToTemp("multiple.md", "file1.md")
        val file2 = copyResourceToTemp("multiple2.md", "file2.md")
        
        val html = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .inputFiles(file1, file2)
            .to(OutputFormat.HTML)
            .outputString()
        
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

    @Test
    fun `test outputWriter conversion`() = runTest {
        val markdown = "# Writer Test\n\nContent via writer"
        val writer = StringWriter()
        
        Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .inputString(markdown)
            .to(OutputFormat.HTML)
            .outputWriter(writer)
        
        val result = writer.toString()
        assertTrue(result.contains("Writer Test"), "Output should contain title")
        assertTrue(result.contains("Content via writer"), "Output should contain content")
    }

    @Test
    fun `test outputWriter with inputReader`() = runTest {
        val markdown = "# Reader to Writer Test"
        val reader = StringReader(markdown)
        val writer = StringWriter()
        
        Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .inputReader(reader)
            .to(OutputFormat.HTML)
            .outputWriter(writer)
        
        val result = writer.toString()
        assertTrue(result.contains("Reader to Writer Test"))
    }
}
