package org.mrlem.pandoc.extensions

import org.mrlem.pandoc.enums.InputFormat

/**
 * Extension property to get the string value for use in command-line arguments.
 */
val InputFormat.cliValue: String
    get() = value