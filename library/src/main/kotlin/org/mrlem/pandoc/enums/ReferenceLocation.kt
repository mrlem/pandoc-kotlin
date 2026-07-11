package org.mrlem.pandoc.enums

/**
 * Reference location for footnotes.
 */
enum class ReferenceLocation(
    val value: String,
) {
    BLOCK("block"),
    SECTION("section"),
    DOCUMENT("document"),
}