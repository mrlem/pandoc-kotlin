package org.mrlem.pandoc.enums

/**
 * Top-level division types for document structure.
 */
enum class TopLevelDivision(
    val value: String,
) {
    SECTION("section"),
    CHAPTER("chapter"),
    PART("part"),
}