package org.mrlem.pandoc.states

import org.mrlem.pandoc.enums.InputFormat

/**
 * Initial state with no required fields set.
 *
 * Use [from] to begin configuration.
 */
class NeedsFrom internal constructor() : CommandState {
    /**
     * Set the input format.
     *
     * @param format The input format (e.g., MARKDOWN, HTML, DOCX)
     * @return A [NeedsInput] state ready to set input source
     */
    fun from(format: InputFormat): NeedsInput = NeedsInput(format)
}