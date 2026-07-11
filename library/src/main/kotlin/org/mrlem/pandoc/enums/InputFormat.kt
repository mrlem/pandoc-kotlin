package org.mrlem.pandoc.enums

/**
 * Input formats supported by Pandoc.
 *
 * Use these values with [org.mrlem.pandoc.PandocCommand.from] to specify
 * the format of your input document.
 */
enum class InputFormat(
    val value: String,
) {
    // Markup formats
    BIBLATEX("biblatex"),
    BIBTEX("bibtex"),
    COMMONMARK("commonmark"),
    COMMONMARK_X("commonmark_x"),
    CREOLE("creole"),
    CSLJSON("csljson"),
    CSV("csv"),
    DJOT("djot"),
    DOCBOOK("docbook"),
    DOCX("docx"),
    DOKUWIKI("dokuwiki"),
    ENDNOTEXML("endnotexml"),
    EPUB("epub"),
    FB2("fb2"),
    GFM("gfm"),
    HADDOCK("haddock"),
    HTML("html"),
    IPYNB("ipynb"),
    JATS("jats"),
    JIRA("jira"),
    JSON("json"),
    LATEX("latex"),
    MAN("man"),
    MARKDOWN("markdown"),
    MARKDOWN_GITHUB("markdown_github"),
    MARKDOWN_MMD("markdown_mmd"),
    MARKDOWN_PHPEXTRA("markdown_phpextra"),
    MARKDOWN_STRICT("markdown_strict"),
    MDOC("mdoc"),
    MEDIAWIKI("mediawiki"),
    MUSE("muse"),
    NATIVE("native"),
    ODT("odt"),
    OPML("opml"),
    ORG("org"),
    POD("pod"),
    RIS("ris"),
    RST("rst"),
    RTF("rtf"),
    T2T("t2t"),
    TEXTILE("textile"),
    TIKIWIKI("tikiwiki"),
    TSV("tsv"),
    TWIKI("twiki"),
    TYPST("typst"),
    VIMWIKI("vimwiki"),

    // Aliases for common formats
    MD("markdown"),
    MD_GITHUB("markdown_github"),
    ADOC("commonmark"), // CommonMark is closest to AsciiDoc
}