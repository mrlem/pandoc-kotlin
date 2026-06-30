/**
 * Enums for Pandoc input and output formats.
 * 
 * These enums provide type-safe access to all formats supported by Pandoc.
 * See [Pandoc manual](https://pandoc.org/MANUAL.html) for format details.
 */
package com.github.sebguillemin.pandoc.enums

/**
 * Input formats supported by Pandoc.
 * 
 * Use these values with [com.github.sebguillemin.pandoc.PandocCommand.from] to specify
 * the format of your input document.
 */
enum class InputFormat(val value: String) {
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

/**
 * Output formats supported by Pandoc.
 * 
 * Use these values with [com.github.sebguillemin.pandoc.PandocCommand.to] to specify
 * the desired output format for your document.
 */
enum class OutputFormat(val value: String) {
    // Markup formats
    ANSII("ansi"),
    ASCIIDOC("asciidoc"),
    ASCIIDOC_LEGACY("asciidoc_legacy"),
    ASCIIDOCTOR("asciidoctor"),
    BEAMER("beamer"),
    BIBLATEX("biblatex"),
    BIBTEX("bibtex"),
    CHUNKEDHTML("chunkedhtml"),
    COMMONMARK("commonmark"),
    COMMONMARK_X("commonmark_x"),
    CONTEXT("context"),
    CSLJSON("csljson"),
    DJOT("djot"),
    DOCBOOK("docbook"),
    DOCBOOK4("docbook4"),
    DOCBOOK5("docbook5"),
    DOCX("docx"),
    DOKUWIKI("dokuwiki"),
    DZSLIDES("dzslides"),
    EPUB("epub"),
    EPUB2("epub2"),
    EPUB3("epub3"),
    FB2("fb2"),
    GFM("gfm"),
    HADDOCK("haddock"),
    HTML("html"),
    HTML4("html4"),
    HTML5("html5"),
    ICML("icml"),
    IPYNB("ipynb"),
    JATS("jats"),
    JATS_ARCHIVING("jats_archiving"),
    JATS_ARTICLEAUTHORING("jats_articleauthoring"),
    JATS_PUBLISHING("jats_publishing"),
    JIRA("jira"),
    JSON("json"),
    LATEX("latex"),
    MAN("man"),
    MARKDOWN("markdown"),
    MARKDOWN_GITHUB("markdown_github"),
    MARKDOWN_MMD("markdown_mmd"),
    MARKDOWN_PHPEXTRA("markdown_phpextra"),
    MARKDOWN_STRICT("markdown_strict"),
    MARKUA("markua"),
    MEDIAWIKI("mediawiki"),
    MS("ms"),
    MUSE("muse"),
    NATIVE("native"),
    ODT("odt"),
    OPENDOCUMENT("opendocument"),
    OPML("opml"),
    ORG("org"),
    PDF("pdf"),
    PLAIN("plain"),
    PPTX("pptx"),
    REVEALJS("revealjs"),
    RST("rst"),
    RTF("rtf"),
    S5("s5"),
    SLIDEOUS("slideous"),
    SLIDY("slidy"),
    TEI("tei"),
    TEXINFO("texinfo"),
    TEXTILE("textile"),
    TYPST("typst"),
    XWIKI("xwiki"),
    ZIMWIKI("zimwiki"),
    
    // Aliases for common formats
    MD("markdown"),
    HTM("html"),
    ADOC("asciidoc"),
}

/**
 * Extension property to get the string value for use in command-line arguments.
 */
val InputFormat.cliValue: String
    get() = value

/**
 * Extension property to get the string value for use in command-line arguments.
 */
val OutputFormat.cliValue: String
    get() = value
