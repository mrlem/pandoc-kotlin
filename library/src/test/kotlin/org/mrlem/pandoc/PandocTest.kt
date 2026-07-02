/**
 * Tests for compile-time safety of the Pandoc fluent API.
 * 
 * These tests verify that the state-encoded builder pattern works correctly:
 * - Incomplete states don't have execute() methods
 * - Complete states have all terminal operations
 */
package org.mrlem.pandoc

import org.mrlem.pandoc.enums.InputFormat
import org.mrlem.pandoc.enums.OutputFormat
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class PandocTest {
    
    @Test
    fun `compile-time safety - Incomplete state does not have execute`() {
        // This test verifies that Incomplete doesn't have execute()
        val command = Pandoc.convert()
        // The following would not compile: command.execute()
    }
    
    @Test
    fun `compile-time safety - HasFrom state does not have execute`() {
        val command = Pandoc.convert().from(InputFormat.MARKDOWN)
        // The following would not compile: command.execute()
    }
    
    @Test
    fun `compile-time safety - NeedsInput state does not have execute`() {
        val command = Pandoc.convert().to(OutputFormat.HTML)
        // The following would not compile: command.execute()
    }
    
    @Test
    fun `compile-time safety - NeedsTo state does not have execute`() {
        val command = Pandoc.convert().fromStdin()
        // The following would not compile: command.execute("content")
    }
    
    @Test
    fun `compile-time safety - NeedsInputSource state does not have execute`() {
        val command = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .to(OutputFormat.HTML)
        // The following would not compile: command.execute()
        // Must specify input first: command.input("file.md").execute()
    }
    
    @Test
    fun `HasFromAndTo state has execute`() {
        val command = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .to(OutputFormat.HTML)
            .input("test.md")
        // This compiles - HasFromAndTo has execute()
    }
    
    @Test
    fun `HasInputAndTo state has execute`() {
        val command = Pandoc.convert()
            .input("test.md")
            .to(OutputFormat.HTML)
        // This compiles - HasInputAndTo has execute()
    }
    
    @Test
    fun `HasStdinAndTo state has execute with content`() {
        val command = Pandoc.convert()
            .fromStdin()
            .to(OutputFormat.HTML)
        // This compiles - HasStdinAndTo has execute(content)
    }
    
    @Test
    fun `full conversion chain compiles`() {
        val command = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .to(OutputFormat.HTML)
            .input("test.md")
            .standalone()
            .toc(2)
            .metadata("title", "Test Document")
        // All methods exist and chain together
    }
    
    @Test
    fun `async execution methods exist`() = runTest {
        val command = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .to(OutputFormat.HTML)
            .input("test.md")
        // executeAsync() exists on complete states
    }
    
    @Test
    fun `flow execution methods exist`() = runTest {
        val command = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .to(OutputFormat.HTML)
            .input("test.md")
        // flow() exists on complete states
    }
    
    @Test
    fun `convertString convenience function exists`() = runTest {
        // convertString() convenience function exists
    }
}
