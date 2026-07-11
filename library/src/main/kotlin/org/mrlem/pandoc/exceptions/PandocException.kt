package org.mrlem.pandoc.exceptions

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
abstract class PandocException(
    message: String,
    val exitCode: Int? = null,
    val command: List<String>? = null,
    val stdout: String? = null,
    val stderr: String? = null,
    cause: Throwable? = null,
) : Exception(message, cause) {
    override fun toString(): String = StringBuilder()
        .appendLine("PandocException: $message")
        .apply {
            exitCode
                ?.let { appendLine("Exit code: $it") }
            command
                ?.let { appendLine("Command: ${it.joinToString(" ")}") }
            stdout
                ?.takeIf { it.isNotBlank() }
                ?.let { appendLine("Stdout:\n$it") }
            stderr
                ?.takeIf { it.isNotBlank() }
                ?.let { appendLine("Stderr:\n$it") }
        }
        .toString()
}