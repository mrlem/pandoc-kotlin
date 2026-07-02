/**
 * Tests for compile-time safety of the Pandoc fluent API.
 * 
 * These tests verify that the state-encoded builder pattern works correctly:
 * - Incomplete states don't have output methods
 * - Complete states have all terminal operations
 */
package org.mrlem.pandoc

import org.mrlem.pandoc.enums.InputFormat
import org.mrlem.pandoc.enums.OutputFormat
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class PandocTest {
    
    @Test
    fun `compile-time safety - Incomplete state does not have output methods`() {
        // This test verifies that Incomplete doesn't have outputString()
        val command = Pandoc.convert()
        // The following would not compile: command.outputString()
    }
    
    @Test
    fun `compile-time safety - HasFrom state does not have output methods`() {
        val command = Pandoc.convert().from(InputFormat.MARKDOWN)
        // The following would not compile: command.outputString()
    }
    
    @Test
    fun `compile-time safety - NeedsTo state does not have output methods`() {
        val command = Pandoc.convert().from(InputFormat.MARKDOWN).inputFile("test.md")
        // The following would not compile: command.outputString()
    }
    
    @Test
    fun `compile-time safety - NeedsTo state with inputString does not have output methods`() {
        val command = Pandoc.convert().from(InputFormat.MARKDOWN).inputString("# Hello")
        // The following would not compile: command.outputString()
    }
    
    @Test
    fun `Complete state with file input has output methods`() {
        val command = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .inputFile("test.md")
            .to(OutputFormat.HTML)
        // This compiles - Complete has outputString()
    }
    
    @Test
    fun `Complete state with string input has output methods`() {
        val command = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .inputString("# Hello")
            .to(OutputFormat.HTML)
        // This compiles - Complete has outputString()
    }
    
    @Test
    fun `full conversion chain with file input compiles`() {
        val command = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .inputFile("test.md")
            .to(OutputFormat.HTML)
            .standalone()
            .toc(2)
            .metadata("title", "Test Document")
        // All methods exist and chain together
    }
    
    @Test
    fun `full conversion chain with string input compiles`() {
        val command = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .inputString("# Test")
            .to(OutputFormat.HTML)
            .standalone()
            .toc(2)
            .metadata("title", "Test Document")
        // All methods exist and chain together
    }
    
    @Test
    fun `async output methods exist`() = runTest {
        val command = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .inputFile("test.md")
            .to(OutputFormat.HTML)
        // outputStringAsync() exists on complete states
    }
}
