package org.mrlem.pandoc.exceptions

/**
 * Exception thrown when pandoc binary is not found on the system PATH.
 */
class PandocNotFoundException(
    message: String = "pandoc executable not found on PATH. Please install pandoc or specify its location.",
    cause: Throwable? = null,
) : PandocException(message, cause = cause)