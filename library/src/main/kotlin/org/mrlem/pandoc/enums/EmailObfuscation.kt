package org.mrlem.pandoc.enums

/**
 * Email obfuscation method.
 */
enum class EmailObfuscation(val value: String) {
    NONE("none"),
    JAVASCRIPT("javascript"),
    REFERENCES("references")
}