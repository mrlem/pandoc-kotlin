package org.mrlem.pandoc

import java.io.Reader

/**
 * Represents the input source for pandoc conversion.
 */
sealed interface InputSource {
    /** Input from one or more files. */
    data class Files(val files: List<String>) : InputSource
    /** Input from a string (passed via stdin). */
    data class StringInput(val content: String) : InputSource
    /** Input from a Reader (passed via stdin). */
    data class ReaderInput(val reader: Reader) : InputSource
}