package org.mrlem.pandoc.enums

/**
 * EOL (end-of-line) character options.
 */
enum class EOL(val value: String) {
    CRLF("crlf"),
    LF("lf"),
    NATIVE("native")
}