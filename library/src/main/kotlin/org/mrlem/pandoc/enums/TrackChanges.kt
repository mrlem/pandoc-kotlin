package org.mrlem.pandoc.enums

/**
 * Track changes mode for Word docx output.
 */
enum class TrackChanges(val value: String) {
    ACCEPT("accept"),
    REJECT("reject"),
    ALL("all")
}