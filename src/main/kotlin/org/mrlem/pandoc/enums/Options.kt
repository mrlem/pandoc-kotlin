/**
 * Enums for Pandoc command-line options.
 * 
 * These enums provide type-safe access to Pandoc's many configuration options.
 */
package org.mrlem.pandoc.enums

/**
 * Text wrapping options for Pandoc output.
 */
enum class WrapOption(val value: String) {
    AUTO("auto"),
    NONE("none"),
    PRESERVE("preserve")
}

/**
 * EOL (end-of-line) character options.
 */
enum class EOL(val value: String) {
    CRLF("crlf"),
    LF("lf"),
    NATIVE("native")
}

/**
 * Top-level division types for document structure.
 */
enum class TopLevelDivision(val value: String) {
    SECTION("section"),
    CHAPTER("chapter"),
    PART("part")
}

/**
 * Track changes mode for Word docx output.
 */
enum class TrackChanges(val value: String) {
    ACCEPT("accept"),
    REJECT("reject"),
    ALL("all")
}

/**
 * Reference location for footnotes.
 */
enum class ReferenceLocation(val value: String) {
    BLOCK("block"),
    SECTION("section"),
    DOCUMENT("document")
}

/**
 * Email obfuscation method.
 */
enum class EmailObfuscation(val value: String) {
    NONE("none"),
    JAVASCRIPT("javascript"),
    REFERENCES("references")
}

/**
 * Markdown heading style.
 */
enum class MarkdownHeadingStyle(val value: String) {
    SETEXT("setext"),
    ATX("atx")
}

/**
 * Figure caption position.
 */
enum class CaptionPosition(val value: String) {
    ABOVE("above"),
    BELOW("below")
}

/**
 * IPython notebook output mode.
 */
enum class IPynbOutput(val value: String) {
    ALL("all"),
    NONE("none"),
    BEST("best")
}

/**
 * HTML slide level for splitting.
 */
enum class SlideLevel(val value: Int) {
    LEVEL1(1),
    LEVEL2(2),
    LEVEL3(3),
    LEVEL4(4),
    LEVEL5(5),
    LEVEL6(6)
}

/**
 * EPUB chapter level for splitting.
 */
enum class EpubChapterLevel(val value: Int) {
    LEVEL1(1),
    LEVEL2(2),
    LEVEL3(3),
    LEVEL4(4),
    LEVEL5(5),
    LEVEL6(6)
}

/**
 * EPUB version.
 */
enum class EpubVersion(val value: String) {
    EPUB2("epub2"),
    EPUB3("epub3")
}
