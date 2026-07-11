package org.mrlem.pandoc.extensions

import org.mrlem.pandoc.enums.OutputFormat

/**
 * Extension property to get the string value for use in command-line arguments.
 */
val OutputFormat.cliValue: String
    get() = value