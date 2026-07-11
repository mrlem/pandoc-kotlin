package org.mrlem.pandoc.exceptions

/**
 * Exception thrown when pandoc execution fails.
 */
class PandocExecutionException(
    message: String,
    exitCode: Int,
    command: List<String>,
    stdout: String? = null,
    stderr: String? = null,
    cause: Throwable? = null,
) : PandocException(message, exitCode, command, stdout, stderr, cause)