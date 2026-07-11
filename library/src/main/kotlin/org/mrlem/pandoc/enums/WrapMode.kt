package org.mrlem.pandoc.enums

/**
 * Text wrapping options for Pandoc output.
 */
enum class WrapMode(val value: String) {
    AUTO("auto"),
    NONE("none"),
    PRESERVE("preserve")
}