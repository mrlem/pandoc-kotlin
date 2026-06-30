/**
 * Exception classes for Pandoc execution errors.
 */
package com.github.sebguillemin.pandoc.exceptions

/**
 * Base exception for all Pandoc-related errors.
 * 
 * @param message The error message
 * @param exitCode The exit code from the pandoc process (null if not executed)
 * @param command The command line that was executed
 * @param stdout The standard output from the process
 * @param stderr The standard error from the process
 * @param cause The underlying cause of the error
 */
open class PandocException(
    message: String,
    val exitCode: Int? = null,
    val command: List<String>? = null,
    val stdout: String? = null,
    val stderr: String? = null,
    cause: Throwable? = null
) : Exception(message, cause) {
    
    override fun toString(): String {
        val builder = StringBuilder()
        builder.appendLine("PandocException: $message")
        
        exitCode?.let { builder.appendLine("Exit code: $it") }
        command?.let { builder.appendLine("Command: ${it.joinToString(" ")}") }
        stdout?.takeIf { it.isNotBlank() }?.let { builder.appendLine("Stdout:\n$it") }
        stderr?.takeIf { it.isNotBlank() }?.let { builder.appendLine("Stderr:\n$it") }
        
        return builder.toString()
    }
}

/**
 * Exception thrown when pandoc binary is not found on the system PATH.
 */
class PandocNotFoundException(
    message: String = "pandoc executable not found on PATH. Please install pandoc or specify its location.",
    cause: Throwable? = null
) : PandocException(message, cause = cause)

/**
 * Exception thrown when required configuration is missing.
 */
class PandocConfigurationException(
    message: String,
    cause: Throwable? = null
) : PandocException(message, cause = cause)

/**
 * Exception thrown when pandoc execution fails.
 */
class PandocExecutionException(
    message: String,
    exitCode: Int,
    command: List<String>,
    stdout: String? = null,
    stderr: String? = null,
    cause: Throwable? = null
) : PandocException(message, exitCode, command, stdout, stderr, cause)

/**
 * Exception thrown when file operations fail.
 */
class PandocFileException(
    message: String,
    val filePath: String? = null,
    cause: Throwable? = null
) : PandocException(message, cause = cause)

/**
 * Exception thrown when format conversion is not supported.
 */
class PandocFormatException(
    message: String,
    val inputFormat: String? = null,
    val outputFormat: String? = null,
    cause: Throwable? = null
) : PandocException(message, cause = cause)
