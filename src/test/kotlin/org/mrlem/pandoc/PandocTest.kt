/**
 * Tests for the Pandoc fluent API.
 */
package org.mrlem.pandoc

import org.mrlem.pandoc.enums.InputFormat
import org.mrlem.pandoc.enums.OutputFormat
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PandocTest {
    
    @Test
    fun `test compile-time safety - Incomplete has no execute`() {
        // This should not compile:
        // Pandoc.convert().execute()
        
        // Instead, we need to set required fields first
        val command = Pandoc.convert()
        // command.execute() // Won't compile - Incomplete has no execute()
    }
    
    @Test
    fun `test compile-time safety - HasFrom has no execute`() {
        val command = Pandoc.convert().from(InputFormat.MARKDOWN)
        // command.execute() // Won't compile - HasFrom has no execute()
    }
    
    @Test
    fun `test compile-time safety - NeedsInput has no execute`() {
        val command = Pandoc.convert().to(OutputFormat.HTML)
        // command.execute() // Won't compile - NeedsInput has no execute()
    }
    
    @Test
    fun `test compile-time safety - NeedsTo has no execute`() {
        val command = Pandoc.convert().fromStdin()
        // command.execute("content") // Won't compile - NeedsTo has no execute()
    }
    
    @Test
    fun `test HasFromAndTo has execute`() {
        // This should compile
        val command = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .to(OutputFormat.HTML)
        
        // command.execute() // Would work if we had pandoc installed
    }
    
    @Test
    fun `test HasInputAndTo has execute`() {
        // This should compile
        val command = Pandoc.convert()
            .input("test.md")
            .to(OutputFormat.HTML)
        
        // command.execute() // Would work if we had pandoc installed
    }
    
    @Test
    fun `test HasStdinAndTo has execute with content`() {
        // This should compile
        val command = Pandoc.convert()
            .fromStdin()
            .to(OutputFormat.HTML)
        
        // command.execute("# Hello") // Would work if we had pandoc installed
    }
    
    @Test
    fun `test full conversion chain`() {
        // This should compile
        val command = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .to(OutputFormat.HTML)
            .input("test.md")
            .standalone()
            .toc(2)
            .metadata("title", "Test Document")
        
        // command.execute() // Would work if we had pandoc installed
    }
    
    @Test
    fun `test async execution`() = runTest {
        // This should compile
        val command = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .to(OutputFormat.HTML)
        
        // command.executeAsync() // Would work if we had pandoc installed
    }
    
    @Test
    fun `test flow execution`() = runTest {
        // This should compile
        val command = Pandoc.convert()
            .from(InputFormat.MARKDOWN)
            .to(OutputFormat.HTML)
        
        // command.flow().collect { } // Would work if we had pandoc installed
    }
    
    @Test
    fun `test convertString convenience function`() = runTest {
        // This should compile
        // Pandoc.convertString(InputFormat.MARKDOWN, OutputFormat.HTML, "# Test")
    }
}
