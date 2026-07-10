/*
 * Fluent API for building and executing Pandoc commands.
 *
 * This module provides a type-safe, compile-time validated API for using pandoc
 * from Kotlin applications. The API uses a state-encoded builder pattern to ensure
 * that required fields are set before execution.
 */
package org.mrlem.pandoc

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mrlem.pandoc.enums.CaptionPosition
import org.mrlem.pandoc.enums.EOL
import org.mrlem.pandoc.enums.EmailObfuscation
import org.mrlem.pandoc.enums.InputFormat
import org.mrlem.pandoc.enums.MarkdownHeadingStyle
import org.mrlem.pandoc.enums.OutputFormat
import org.mrlem.pandoc.enums.ReferenceLocation
import org.mrlem.pandoc.enums.TopLevelDivision
import org.mrlem.pandoc.enums.TrackChanges
import org.mrlem.pandoc.enums.WrapOption
import org.mrlem.pandoc.exceptions.PandocExecutionException
import org.mrlem.pandoc.exceptions.PandocNotFoundException
import java.io.File
import java.io.Reader
import java.io.Writer
import java.nio.file.Path

/**
 * Represents the input source for pandoc conversion.
 */
sealed class InputSource {
    /** Input from one or more files. */
    data class Files(val files: List<String>) : InputSource()
    /** Input from a string (passed via stdin). */
    data class StringInput(val content: String) : InputSource()
    /** Input from a Reader (passed via stdin). */
    data class ReaderInput(val reader: Reader) : InputSource()
}

/**
 * Sealed hierarchy for Pandoc command building with compile-time safety.
 *
 * Simplified state machine with enforced order: from() -> input() or inputString() -> to()
 *
 * - [Incomplete]: No required fields set yet
 * - [HasFrom]: Input format set, needs input source
 * - [NeedsTo]: Input format and source set, needs output format
 * - [Complete]: All required fields set, ready for execution
 *
 * Only the [Complete] state has terminal operations like [outputString], [outputFile], and [outputWriter].
 */
sealed class PandocCommand {

    // ========================================================================
    // INCOMPLETE STATE
    // ========================================================================

    /**
     * Initial state with no required fields set.
     *
     * Use [from] to begin configuration.
     */
    class Incomplete internal constructor() : PandocCommand() {

        /**
         * Set the input format.
         *
         * @param format The input format (e.g., MARKDOWN, HTML, DOCX)
         * @return A [HasFrom] state ready to set input source
         */
        fun from(format: InputFormat): HasFrom = HasFrom(format)
    }

    // ========================================================================
    // HAS INPUT FORMAT
    // ========================================================================

    /**
     * State where input format is set but input source and output format are not yet set.
     */
    data class HasFrom internal constructor(
        val from: InputFormat,
        val standalone: Boolean? = null,
        val template: String? = null,
        val metadata: Map<String, String> = emptyMap(),
        val variables: Map<String, String> = emptyMap(),
        val toc: Boolean? = null,
        val tocDepth: Int? = null,
        val wrap: WrapOption? = null,
        val ascii: Boolean? = null,
        val numberSections: Boolean? = null,
        val numberOffset: List<Int>? = null,
        val topLevelDivision: TopLevelDivision? = null,
        val extractMedia: String? = null,
        val resourcePath: List<String>? = null,
        val includeInHeader: List<String>? = null,
        val includeBeforeBody: List<String>? = null,
        val includeAfterBody: List<String>? = null,
        val highlightStyle: String? = null,
        val syntaxDefinition: String? = null,
        val dpi: Int? = null,
        val eol: EOL? = null,
        val columns: Int? = null,
        val preserveTabs: Boolean? = null,
        val tabStop: Int? = null,
        val pdfEngine: String? = null,
        val pdfEngineOpt: List<String>? = null,
        val selfContained: Boolean? = null,
        val embedResources: Boolean? = null,
        val linkImages: Boolean? = null,
        val requestHeaders: Map<String, String>? = null,
        val noCheckCertificate: Boolean? = null,
        val abbreviations: String? = null,
        val indentedCodeClasses: String? = null,
        val defaultImageExtension: String? = null,
        val filters: List<String>? = null,
        val luaFilters: List<String>? = null,
        val shiftHeadingLevelBy: Int? = null,
        val baseHeaderLevel: Int? = null,
        val trackChanges: TrackChanges? = null,
        val stripComments: Boolean? = null,
        val referenceLinks: Boolean? = null,
        val referenceLocation: ReferenceLocation? = null,
        val figureCaptionPosition: CaptionPosition? = null,
        val tableCaptionPosition: CaptionPosition? = null,
        val markdownHeadings: MarkdownHeadingStyle? = null,
        val listTables: Boolean? = null,
        val listings: Boolean? = null,
        val incremental: Boolean? = null,
        val slideLevel: Int? = null,
        val sectionDivs: Boolean? = null,
        val htmlQTags: Boolean? = null,
        val emailObfuscation: EmailObfuscation? = null,
        val idPrefix: String? = null,
        val titlePrefix: String? = null,
        val css: String? = null,
        val citeproc: Boolean? = null,
        val bibliography: String? = null,
        val csl: String? = null,
        val citationAbbreviations: String? = null,
        val natbib: Boolean? = null,
        val biblatex: Boolean? = null,
        val mathml: Boolean? = null,
        val webtex: String? = null,
        val mathjax: String? = null,
        val katex: String? = null,
        val gladtex: Boolean? = null,
        val trace: Boolean? = null,
        val dumpArgs: Boolean? = null,
        val ignoreArgs: Boolean? = null,
        val verbose: Boolean? = null,
        val quiet: Boolean? = null,
        val failIfWarnings: Boolean? = null,
        val log: String? = null
    ) : PandocCommand() {

        // Input methods

        fun inputFiles(vararg files: String): NeedsTo = NeedsTo(
            from = from,
            inputSource = InputSource.Files(files.toList()),
            standalone = standalone,
            template = template,
            metadata = metadata,
            variables = variables,
            toc = toc,
            tocDepth = tocDepth,
            wrap = wrap,
            ascii = ascii,
            numberSections = numberSections,
            numberOffset = numberOffset,
            topLevelDivision = topLevelDivision,
            extractMedia = extractMedia,
            resourcePath = resourcePath,
            includeInHeader = includeInHeader,
            includeBeforeBody = includeBeforeBody,
            includeAfterBody = includeAfterBody,
            highlightStyle = highlightStyle,
            syntaxDefinition = syntaxDefinition,
            dpi = dpi,
            eol = eol,
            columns = columns,
            preserveTabs = preserveTabs,
            tabStop = tabStop,
            pdfEngine = pdfEngine,
            pdfEngineOpt = pdfEngineOpt,
            selfContained = selfContained,
            embedResources = embedResources,
            linkImages = linkImages,
            requestHeaders = requestHeaders,
            noCheckCertificate = noCheckCertificate,
            abbreviations = abbreviations,
            indentedCodeClasses = indentedCodeClasses,
            defaultImageExtension = defaultImageExtension,
            filters = filters,
            luaFilters = luaFilters,
            shiftHeadingLevelBy = shiftHeadingLevelBy,
            baseHeaderLevel = baseHeaderLevel,
            trackChanges = trackChanges,
            stripComments = stripComments,
            referenceLinks = referenceLinks,
            referenceLocation = referenceLocation,
            figureCaptionPosition = figureCaptionPosition,
            tableCaptionPosition = tableCaptionPosition,
            markdownHeadings = markdownHeadings,
            listTables = listTables,
            listings = listings,
            incremental = incremental,
            slideLevel = slideLevel,
            sectionDivs = sectionDivs,
            htmlQTags = htmlQTags,
            emailObfuscation = emailObfuscation,
            idPrefix = idPrefix,
            titlePrefix = titlePrefix,
            css = css,
            citeproc = citeproc,
            bibliography = bibliography,
            csl = csl,
            citationAbbreviations = citationAbbreviations,
            natbib = natbib,
            biblatex = biblatex,
            mathml = mathml,
            webtex = webtex,
            mathjax = mathjax,
            katex = katex,
            gladtex = gladtex,
            trace = trace,
            dumpArgs = dumpArgs,
            ignoreArgs = ignoreArgs,
            verbose = verbose,
            quiet = quiet,
            failIfWarnings = failIfWarnings,
            log = log
        )

        fun inputFile(file: String): NeedsTo = inputFiles(file)
        fun inputFile(file: Path): NeedsTo = inputFile(file.toString())
        fun inputFile(file: File): NeedsTo = inputFile(file.absolutePath)
        fun inputFiles(vararg files: File): NeedsTo = inputFiles(*files.map { it.absolutePath }.toTypedArray())
        fun inputFiles(vararg files: Path): NeedsTo = inputFiles(*files.map { it.toString() }.toTypedArray())

        fun inputReader(reader: Reader): NeedsTo = NeedsTo(
            from = from,
            inputSource = InputSource.ReaderInput(reader),
            standalone = standalone,
            template = template,
            metadata = metadata,
            variables = variables,
            toc = toc,
            tocDepth = tocDepth,
            wrap = wrap,
            ascii = ascii,
            numberSections = numberSections,
            numberOffset = numberOffset,
            topLevelDivision = topLevelDivision,
            extractMedia = extractMedia,
            resourcePath = resourcePath,
            includeInHeader = includeInHeader,
            includeBeforeBody = includeBeforeBody,
            includeAfterBody = includeAfterBody,
            highlightStyle = highlightStyle,
            syntaxDefinition = syntaxDefinition,
            dpi = dpi,
            eol = eol,
            columns = columns,
            preserveTabs = preserveTabs,
            tabStop = tabStop,
            pdfEngine = pdfEngine,
            pdfEngineOpt = pdfEngineOpt,
            selfContained = selfContained,
            embedResources = embedResources,
            linkImages = linkImages,
            requestHeaders = requestHeaders,
            noCheckCertificate = noCheckCertificate,
            abbreviations = abbreviations,
            indentedCodeClasses = indentedCodeClasses,
            defaultImageExtension = defaultImageExtension,
            filters = filters,
            luaFilters = luaFilters,
            shiftHeadingLevelBy = shiftHeadingLevelBy,
            baseHeaderLevel = baseHeaderLevel,
            trackChanges = trackChanges,
            stripComments = stripComments,
            referenceLinks = referenceLinks,
            referenceLocation = referenceLocation,
            figureCaptionPosition = figureCaptionPosition,
            tableCaptionPosition = tableCaptionPosition,
            markdownHeadings = markdownHeadings,
            listTables = listTables,
            listings = listings,
            incremental = incremental,
            slideLevel = slideLevel,
            sectionDivs = sectionDivs,
            htmlQTags = htmlQTags,
            emailObfuscation = emailObfuscation,
            idPrefix = idPrefix,
            titlePrefix = titlePrefix,
            css = css,
            citeproc = citeproc,
            bibliography = bibliography,
            csl = csl,
            citationAbbreviations = citationAbbreviations,
            natbib = natbib,
            biblatex = biblatex,
            mathml = mathml,
            webtex = webtex,
            mathjax = mathjax,
            katex = katex,
            gladtex = gladtex,
            trace = trace,
            dumpArgs = dumpArgs,
            ignoreArgs = ignoreArgs,
            verbose = verbose,
            quiet = quiet,
            failIfWarnings = failIfWarnings,
            log = log
        )

        fun inputString(content: String): NeedsTo = NeedsTo(
            from = from,
            inputSource = InputSource.StringInput(content),
            standalone = standalone,
            template = template,
            metadata = metadata,
            variables = variables,
            toc = toc,
            tocDepth = tocDepth,
            wrap = wrap,
            ascii = ascii,
            numberSections = numberSections,
            numberOffset = numberOffset,
            topLevelDivision = topLevelDivision,
            extractMedia = extractMedia,
            resourcePath = resourcePath,
            includeInHeader = includeInHeader,
            includeBeforeBody = includeBeforeBody,
            includeAfterBody = includeAfterBody,
            highlightStyle = highlightStyle,
            syntaxDefinition = syntaxDefinition,
            dpi = dpi,
            eol = eol,
            columns = columns,
            preserveTabs = preserveTabs,
            tabStop = tabStop,
            pdfEngine = pdfEngine,
            pdfEngineOpt = pdfEngineOpt,
            selfContained = selfContained,
            embedResources = embedResources,
            linkImages = linkImages,
            requestHeaders = requestHeaders,
            noCheckCertificate = noCheckCertificate,
            abbreviations = abbreviations,
            indentedCodeClasses = indentedCodeClasses,
            defaultImageExtension = defaultImageExtension,
            filters = filters,
            luaFilters = luaFilters,
            shiftHeadingLevelBy = shiftHeadingLevelBy,
            baseHeaderLevel = baseHeaderLevel,
            trackChanges = trackChanges,
            stripComments = stripComments,
            referenceLinks = referenceLinks,
            referenceLocation = referenceLocation,
            figureCaptionPosition = figureCaptionPosition,
            tableCaptionPosition = tableCaptionPosition,
            markdownHeadings = markdownHeadings,
            listTables = listTables,
            listings = listings,
            incremental = incremental,
            slideLevel = slideLevel,
            sectionDivs = sectionDivs,
            htmlQTags = htmlQTags,
            emailObfuscation = emailObfuscation,
            idPrefix = idPrefix,
            titlePrefix = titlePrefix,
            css = css,
            citeproc = citeproc,
            bibliography = bibliography,
            csl = csl,
            citationAbbreviations = citationAbbreviations,
            natbib = natbib,
            biblatex = biblatex,
            mathml = mathml,
            webtex = webtex,
            mathjax = mathjax,
            katex = katex,
            gladtex = gladtex,
            trace = trace,
            dumpArgs = dumpArgs,
            ignoreArgs = ignoreArgs,
            verbose = verbose,
            quiet = quiet,
            failIfWarnings = failIfWarnings,
            log = log
        )

        // Configuration options

        /**
         * Produce output with an appropriate header and footer (e.g. a standalone HTML,
         * LaTeX, TEI, or RTF file, not a fragment). This option is set automatically for
         * pdf, epub, epub3, fb2, docx, and odt output.
         *
         * @param enabled Whether to produce standalone output (default: true)
         */
        fun standalone(enabled: Boolean = true): HasFrom = copy(standalone = enabled)

        /**
         * Use the specified file as a custom template for the generated document.
         * Implies [standalone]. If the template is not found, pandoc will search for it
         * in the templates subdirectory of the user data directory.
         *
         * @param file Path to the template file
         */
        fun template(file: String): HasFrom = copy(template = file)

        /**
         * Set the metadata field KEY to the value VAL. A value specified on the command
         * line overrides a value specified in the document using YAML metadata blocks.
         * Values will be parsed as YAML boolean or string values.
         *
         * @param key The metadata key
         * @param value The metadata value
         */
        fun metadata(key: String, value: String): HasFrom = copy(metadata = metadata + (key to value))

        /**
         * Set the template variable KEY to the string value VAL when rendering the document
         * in standalone mode. If the variable already has a list value, the value will be
         * added to the list. If it already has another kind of value, it will be made into
         * a list containing the previous and the new value.
         *
         * @param key The variable key
         * @param value The variable value
         */
        fun variable(key: String, value: String): HasFrom = copy(variables = variables + (key to value))

        /**
         * Include an automatically generated table of contents in the output document.
         * This option has no effect unless [standalone] is used, and it has no effect
         * on man, docbook4, docbook5, or jats output.
         *
         * @param depth The number of section levels to include (default: 3)
         */
        fun toc(depth: Int? = null): HasFrom = copy(toc = true, tocDepth = depth)

        /**
         * Determine how text is wrapped in the output (the source code, not the rendered version).
         * - [WrapOption.AUTO] (default): pandoc will attempt to wrap lines to the column width
         *   specified by [columns] (default 72)
         * - [WrapOption.NONE]: pandoc will not wrap lines at all
         * - [WrapOption.PRESERVE]: pandoc will attempt to preserve the wrapping from the
         *   source document
         *
         * @param option The wrapping option
         */
        fun wrap(option: WrapOption): HasFrom = copy(wrap = option)

        /**
         * Use only ASCII characters in output. Currently supported for XML and HTML formats
         * (which use entities instead of UTF-8 when this option is selected), CommonMark, gfm,
         * and Markdown (which use entities), roff man and ms (which use hexadecimal escapes),
         * and to a limited degree LaTeX (which uses standard commands for accented characters when possible).
         *
         * @param enabled Whether to use only ASCII characters (default: true)
         */
        fun ascii(enabled: Boolean = true): HasFrom = copy(ascii = enabled)

        /**
         * Number section headings in LaTeX, ConTeXt, HTML, Docx, ms, or EPUB output.
         * By default, sections are not numbered. Sections with class "unnumbered" will
         * never be numbered, even if this option is specified.
         *
         * @param enabled Whether to number sections (default: true)
         */
        fun numberSections(enabled: Boolean = true): HasFrom = copy(numberSections = enabled)

        /**
         * Offsets for section heading numbers. The first number is added to the section
         * number for level-1 headings, the second for level-2 headings, and so on.
         * For example, --number-offset=5 makes the first level-1 heading "6".
         * Only affects HTML and Docx output.
         *
         * @param offsets Vararg list of offset values for each heading level
         */
        fun numberOffset(vararg offsets: Int): HasFrom = copy(numberOffset = offsets.toList())

        /**
         * Treat top-level headings as the given division type in LaTeX, ConTeXt, DocBook,
         * and TEI output. The hierarchy order is part, chapter, then section; all headings
         * are shifted such that the top-level heading becomes the specified type.
         *
         * @param division The top-level division type
         */
        fun topLevelDivision(division: TopLevelDivision): HasFrom = copy(topLevelDivision = division)
        /**
         * Extract images and other media contained in or linked from the source document
         * to the specified path, creating it if necessary, and adjust the image references
         * in the document so they point to the extracted files.
         *
         * @param path The directory path to extract media to
         */
        fun extractMedia(path: String): HasFrom = copy(extractMedia = path)

        /**
         * List of paths to search for images and other resources. The paths should be
         * separated by : on Linux, UNIX, and macOS systems, and by ; on Windows.
         * If not specified, the default resource path is the working directory.
         *
         * @param paths Vararg list of search paths
         */
        fun resourcePath(vararg paths: String): HasFrom = copy(resourcePath = paths.toList())

        /**
         * Include contents of FILE, verbatim, at the end of the header. This can be
         * used, for example, to include special CSS or JavaScript in HTML documents.
         * Implies [standalone]. This option can be used repeatedly to include multiple files.
         *
         * @param file Path to the file to include in the header
         */
        fun includeInHeader(file: String): HasFrom = copy(includeInHeader = (includeInHeader ?: emptyList()) + file)

        /**
         * Include contents of FILE, verbatim, at the beginning of the document body
         * (e.g. after the body tag in HTML, or the begin{document} command in LaTeX).
         * This can be used to include navigation bars or banners in HTML documents.
         * Implies [standalone].
         *
         * @param file Path to the file to include before the body
         */
        fun includeBeforeBody(file: String): HasFrom = copy(includeBeforeBody = (includeBeforeBody ?: emptyList()) + file)

        /**
         * Include contents of FILE, verbatim, at the end of the document body
         * (before the /body tag in HTML, or the end{document} command in LaTeX).
         * Implies [standalone].
         *
         * @param file Path to the file to include after the body
         */
        fun includeAfterBody(file: String): HasFrom = copy(includeAfterBody = (includeAfterBody ?: emptyList()) + file)

        /**
         * Specifies the styling to use for highlighted source code.
         *
         * @param style The highlight style name or path to a JSON theme file
         */
        fun highlightStyle(style: String): HasFrom = copy(highlightStyle = style)

        /**
         * Instructs pandoc to load a KDE XML syntax definition file, which will be
         * used for syntax highlighting of appropriately marked code blocks.
         *
         * @param file Path to the syntax definition XML file
         */
        fun syntaxDefinition(file: String): HasFrom = copy(syntaxDefinition = file)

        /**
         * Specify the default dpi (dots per inch) value for conversion from pixels
         * to inch/centimeters and vice versa. The default is 96dpi.
         *
         * @param value The DPI value
         */
        fun dpi(value: Int): HasFrom = copy(dpi = value)

        /**
         * Manually specify line endings: crlf (Windows), lf (macOS/Linux/UNIX), or native
         * (line endings appropriate to the OS on which pandoc is being run).
         *
         * @param option The end-of-line option
         */
        fun eol(option: EOL): HasFrom = copy(eol = option)

        /**
         * Specify length of lines in characters. This affects text wrapping in the
         * generated source code and calculation of column widths for plain text tables.
         *
         * @param value The number of columns
         */
        fun columns(value: Int): HasFrom = copy(columns = value)
        /**
         * Preserve tabs instead of converting them to spaces. By default, pandoc converts
         * tabs to spaces before parsing its input. Note that this will only affect tabs
         * in literal code spans and code blocks. Tabs in regular text are always treated
         * as spaces.
         *
         * @param enabled Whether to preserve tabs (default: true)
         */
        fun preserveTabs(enabled: Boolean = true): HasFrom = copy(preserveTabs = enabled)

        /**
         * Specify the number of spaces per tab (default is 4).
         *
         * @param value The tab stop value in spaces
         */
        fun tabStop(value: Int): HasFrom = copy(tabStop = value)

        /**
         * Use the specified engine when producing PDF output. Valid values include
         * pdflatex, lualatex, xelatex, latexmk, tectonic, wkhtmltopdf, weasyprint,
         * pagedjs-cli, prince, context, groff, pdfroff, and typst.
         *
         * @param engine The PDF engine to use
         */
        fun pdfEngine(engine: String): HasFrom = copy(pdfEngine = engine)

        /**
         * Use the given string as a command-line argument to the PDF engine.
         * This option can be used repeatedly to pass multiple arguments.
         *
         * @param option The PDF engine option
         */
        fun pdfEngineOpt(option: String): HasFrom = copy(pdfEngineOpt = (pdfEngineOpt ?: emptyList()) + option)

        /**
         * Produce a standalone HTML file with no external dependencies, using data:
         * URIs to incorporate the contents of linked scripts, stylesheets, images, and videos.
         * Works only with HTML output formats (html4, html5, html+lhs, html5+lhs, s5,
         * slidy, slideous, dzslides, and revealjs).
         *
         * @param enabled Whether to embed resources (default: true)
         */
        fun embedResources(enabled: Boolean = true): HasFrom = copy(embedResources = enabled)

        /**
         * Include links to images instead of embedding the images in ODT output.
         *
         * @param enabled Whether to link images instead of embedding (default: true)
         */
        fun linkImages(enabled: Boolean = true): HasFrom = copy(linkImages = enabled)

        /**
         * Include links to images instead of embedding the images in ODT.
         * This option is deprecated; use [embedResources] instead.
         *
         * @param enabled Whether to make output self-contained
         */
        fun selfContained(enabled: Boolean = true): HasFrom = copy(selfContained = enabled)

        /**
         * Set the request header NAME to the value VAL when making HTTP requests.
         *
         * @param name The header name
         * @param value The header value
         */
        fun requestHeader(name: String, value: String): HasFrom = copy(requestHeaders = (requestHeaders ?: emptyMap()) + (name to value))

        /**
         * Disable the certificate verification to allow access to unsecure HTTP
         * resources (for example when the certificate is no longer valid or self-signed).
         *
         * @param enabled Whether to skip certificate checks (default: true)
         */
        fun noCheckCertificate(enabled: Boolean = true): HasFrom = copy(noCheckCertificate = enabled)

        /**
         * Specifies a custom abbreviations file, with abbreviations one to a line.
         * If this option is not specified, pandoc will read the data file 'abbreviations'
         * from the user data directory or fall back on a system default.
         *
         * @param file Path to the abbreviations file
         */
        fun abbreviations(file: String): HasFrom = copy(abbreviations = file)

        /**
         * Specify classes to use for indented code blocks—for example, perl,numberLines
         * or haskell. Multiple classes may be separated by spaces or commas.
         *
         * @param classes The CSS classes to apply
         */
        fun indentedCodeClasses(classes: String): HasFrom = copy(indentedCodeClasses = classes)

        /**
         * Specify a default extension to use when image paths/URLs have no extension.
         * This allows you to use the same source for formats that require different
         * kinds of images.
         *
         * @param extension The default image extension (e.g., ".jpg", ".png")
         */
        fun defaultImageExtension(extension: String): HasFrom = copy(defaultImageExtension = extension)
        /**
         * Specify an executable to be used as a filter transforming the pandoc AST
         * after the input is parsed and before the output is written. The executable
         * should read JSON from stdin and write JSON to stdout.
         *
         * @param program The filter program path or command
         */
        fun filter(program: String): HasFrom = copy(filters = (filters ?: emptyList()) + program)

        /**
         * Transform the document using pandoc's built-in Lua filtering system.
         * The given Lua script is expected to return a list of Lua filters which will
         * be applied in order.
         *
         * @param script Path to the Lua filter script
         */
        fun luaFilter(script: String): HasFrom = copy(luaFilters = (luaFilters ?: emptyList()) + script)

        /**
         * Shift heading levels by a positive or negative integer. For example,
         * with shiftHeadingLevelBy=-1, level 2 headings become level 1 headings.
         * Headings cannot have a level less than 1.
         *
         * @param value The number of levels to shift (can be negative)
         */
        fun shiftHeadingLevelBy(value: Int): HasFrom = copy(shiftHeadingLevelBy = value)

        /**
         * Deprecated. Use [shiftHeadingLevelBy] instead, where X = NUMBER - 1.
         * Specify the base level for headings (defaults to 1).
         *
         * @param value The base header level
         */
        fun baseHeaderLevel(value: Int): HasFrom = copy(baseHeaderLevel = value)

        /**
         * Specifies what to do with insertions, deletions, and comments produced by
         * the MS Word "Track Changes" feature.
         * - [TrackChanges.ACCEPT] (default): processes all insertions and deletions
         * - [TrackChanges.REJECT]: ignores them
         * - [TrackChanges.ALL]: includes all insertions, deletions, and comments
         *
         * @param mode The track changes mode
         */
        fun trackChanges(mode: TrackChanges): HasFrom = copy(trackChanges = mode)

        /**
         * Strip out HTML comments in the Markdown or Textile source, rather than
         * passing them on to Markdown, Textile or HTML output as raw HTML.
         *
         * @param enabled Whether to strip comments (default: true)
         */
        fun stripComments(enabled: Boolean = true): HasFrom = copy(stripComments = enabled)

        /**
         * Use reference-style links, rather than inline links, in writing Markdown
         * or reStructuredText. By default inline links are used.
         *
         * @param enabled Whether to use reference-style links (default: true)
         */
        fun referenceLinks(enabled: Boolean = true): HasFrom = copy(referenceLinks = enabled)

        /**
         * Specify whether footnotes (and references, if referenceLinks is set)
         * are placed at the end of the current block, section, or document.
         * - [ReferenceLocation.BLOCK]: end of current block
         * - [ReferenceLocation.SECTION]: end of current section
         * - [ReferenceLocation.DOCUMENT] (default): end of document
         *
         * @param location The reference location
         */
        fun referenceLocation(location: ReferenceLocation): HasFrom = copy(referenceLocation = location)

        /**
         * Specify whether figure captions go above or below figures (default is below).
         * Only affects HTML, LaTeX, Docx, ODT, and Typst output.
         *
         * @param position The caption position
         */
        fun figureCaptionPosition(position: CaptionPosition): HasFrom = copy(figureCaptionPosition = position)

        /**
         * Specify whether table captions go above or below tables (default is above).
         * Only affects HTML, LaTeX, Docx, ODT, and Typst output.
         *
         * @param position The caption position
         */
        fun tableCaptionPosition(position: CaptionPosition): HasFrom = copy(tableCaptionPosition = position)

        /**
         * Specify whether to use ATX-style (#-prefixed) or Setext-style (underlined)
         * headings for level 1 and 2 headings in Markdown output. Default is ATX.
         *
         * @param style The markdown heading style
         */
        fun markdownHeadings(style: MarkdownHeadingStyle): HasFrom = copy(markdownHeadings = style)

        /**
         * Render tables as list tables in RST output.
         *
         * @param enabled Whether to use list tables (default: true)
         */
        fun listTables(enabled: Boolean = true): HasFrom = copy(listTables = enabled)

        /**
         * Use the listings package for LaTeX code blocks. The package does not support
         * multi-byte encoding for source code. This option is deprecated; use syntax-highlighting
         * instead.
         *
         * @param enabled Whether to use listings (default: true)
         */
        fun listings(enabled: Boolean = true): HasFrom = copy(listings = enabled)
        /**
         * Make list items in slide shows display incrementally (one by one).
         * The default is for lists to be displayed all at once.
         *
         * @param enabled Whether to show list items incrementally (default: true)
         */
        fun incremental(enabled: Boolean = true): HasFrom = copy(incremental = enabled)

        /**
         * Specifies that headings with the specified level create slides.
         * Headings above this level in the hierarchy are used to divide the slide
         * show into sections; headings below this level create subheads within a slide.
         * Valid values are 0-6.
         *
         * @param value The heading level that creates slides (1-6, or 0 for manual)
         */
        fun slideLevel(value: Int): HasFrom = copy(slideLevel = value)

        /**
         * Wrap sections in section tags (or div tags for html4), and attach
         * identifiers to the enclosing section (or div) rather than the heading itself.
         * Only affects HTML output (not HTML slide formats).
         *
         * @param enabled Whether to wrap sections in divs (default: true)
         */
        fun sectionDivs(enabled: Boolean = true): HasFrom = copy(sectionDivs = enabled)

        /**
         * Use q tags for quotes in HTML. This option only has an effect if the
         * smart extension is enabled for the input format used.
         *
         * @param enabled Whether to use q tags (default: true)
         */
        fun htmlQTags(enabled: Boolean = true): HasFrom = copy(htmlQTags = enabled)

        /**
         * Specify a method for obfuscating mailto: links in HTML documents.
         * - [EmailObfuscation.NONE]: leaves mailto: links as they are
         * - [EmailObfuscation.JAVASCRIPT]: obfuscates them using JavaScript
         * - [EmailObfuscation.REFERENCES]: obfuscates them by printing their letters as
         *   decimal or hexadecimal character references
         *
         * @param mode The email obfuscation method
         */
        fun emailObfuscation(mode: EmailObfuscation): HasFrom = copy(emailObfuscation = mode)

        /**
         * Specify a prefix to be added to all identifiers and internal links in
         * HTML and DocBook output, and to footnote numbers in Markdown and Haddock output.
         * This is useful for preventing duplicate identifiers when generating fragments
         * to be included in other pages.
         *
         * @param prefix The ID prefix
         */
        fun idPrefix(prefix: String): HasFrom = copy(idPrefix = prefix)

        /**
         * Specify a prefix at the beginning of the title that appears in the HTML
         * header (but not in the title as it appears at the beginning of the HTML body).
         * Implies [standalone].
         *
         * @param prefix The title prefix
         */
        fun titlePrefix(prefix: String): HasFrom = copy(titlePrefix = prefix)

        /**
         * Link to a CSS style sheet. This option can be used repeatedly to include
         * multiple files. It should be used together with [standalone], because the
         * link to the stylesheet goes in the document header.
         *
         * @param url The URL or path to the CSS file
         */
        fun css(url: String): HasFrom = copy(css = url)
        /**
         * Process the citations in the file, replacing them with rendered citations
         * and adding a bibliography. Citation processing will not take place unless
         * bibliographic data is supplied, either through an external file specified
         * using [bibliography] or the bibliography field in metadata, or via a
         * references section in metadata containing a list of citations in CSL YAML
         * format with Markdown formatting.
         *
         * @param enabled Whether to process citations (default: true)
         */
        fun citeproc(enabled: Boolean = true): HasFrom = copy(citeproc = enabled)

        /**
         * Set the bibliography field in the document's metadata to FILE, overriding
         * any value set in the metadata. If you supply this argument multiple times,
         * each FILE will be added to bibliography.
         *
         * @param file Path to the bibliography file
         */
        fun bibliography(file: String): HasFrom = copy(bibliography = file)

        /**
         * Set the csl field in the document's metadata to FILE, overriding any value
         * set in the metadata. This specifies the citation style.
         *
         * @param file Path to the CSL file
         */
        fun csl(file: String): HasFrom = copy(csl = file)

        /**
         * Set the citation-abbreviations field in the document's metadata to FILE,
         * overriding any value set in the metadata.
         *
         * @param file Path to the citation abbreviations file
         */
        fun citationAbbreviations(file: String): HasFrom = copy(citationAbbreviations = file)

        /**
         * Use natbib for citations in LaTeX output. This option is not for use with
         * [citeproc] or with PDF output. It is intended for use in producing a LaTeX
         * file that can be processed with bibtex.
         *
         * @param enabled Whether to use natbib (default: true)
         */
        fun natbib(enabled: Boolean = true): HasFrom = copy(natbib = enabled)

        /**
         * Use biblatex for citations in LaTeX output. This option is not for use with
         * [citeproc] or with PDF output. It is intended for use in producing a LaTeX
         * file that can be processed with bibtex or biber.
         *
         * @param enabled Whether to use biblatex (default: true)
         */
        fun biblatex(enabled: Boolean = true): HasFrom = copy(biblatex = enabled)

        /**
         * Convert TeX math to MathML. This is the default in odt output.
         * MathML is supported natively by the main web browsers and select e-book readers.
         *
         * @param enabled Whether to use MathML (default: true)
         */
        fun mathml(enabled: Boolean = true): HasFrom = copy(mathml = enabled)

        /**
         * Convert TeX formulas to img tags that link to an external script that
         * converts formulas to images. If no URL is specified, the CodeCogs URL
         * generating PNGs will be used.
         *
         * @param url The webtex URL (optional, default uses CodeCogs)
         */
        fun webtex(url: String? = null): HasFrom = copy(webtex = url)

        /**
         * Use MathJax to display embedded TeX math in HTML output. TeX math will be
         * put between \(...\) (for inline math) or \[...\] (for display math) and
         * wrapped in span tags with class "math". If a URL is not provided, a link
         * to the Cloudflare CDN will be inserted.
         *
         * @param url The MathJax URL (optional, default uses Cloudflare CDN)
         */
        fun mathjax(url: String? = null): HasFrom = copy(mathjax = url)

        /**
         * Use KaTeX to display embedded TeX math in HTML output. The URL is the base
         * URL for the KaTeX library. If a URL is not provided, a link to the KaTeX
         * CDN will be inserted.
         *
         * @param url The KaTeX base URL (optional, default uses KaTeX CDN)
         */
        fun katex(url: String? = null): HasFrom = copy(katex = url)

        /**
         * Enclose TeX math in eq tags in HTML output. The resulting HTML can then
         * be processed by GladTeX to produce SVG images of the typeset formulas.
         *
         * @param enabled Whether to use GladTeX (default: true)
         */
        fun gladtex(enabled: Boolean = true): HasFrom = copy(gladtex = enabled)
        /**
         * Print diagnostic output tracing parser progress to stderr. This option is
         * intended for use by developers in diagnosing performance issues.
         *
         * @param enabled Whether to enable trace output (default: true)
         */
        fun trace(enabled: Boolean = true): HasFrom = copy(trace = enabled)

        /**
         * Print information about command-line arguments to stdout, then exit.
         * This option is intended primarily for use in wrapper scripts.
         *
         * @param enabled Whether to dump arguments (default: true)
         */
        fun dumpArgs(enabled: Boolean = true): HasFrom = copy(dumpArgs = enabled)

        /**
         * Ignore command-line arguments (for use in wrapper scripts).
         * Regular pandoc options are not ignored.
         *
         * @param enabled Whether to ignore arguments (default: true)
         */
        fun ignoreArgs(enabled: Boolean = true): HasFrom = copy(ignoreArgs = enabled)

        /**
         * Give verbose debugging output.
         *
         * @param enabled Whether to enable verbose output (default: true)
         */
        fun verbose(enabled: Boolean = true): HasFrom = copy(verbose = enabled)

        /**
         * Suppress warning messages.
         *
         * @param enabled Whether to suppress warnings (default: true)
         */
        fun quiet(enabled: Boolean = true): HasFrom = copy(quiet = enabled)

        /**
         * Exit with error status if there are any warnings.
         *
         * @param enabled Whether to fail on warnings (default: true)
         */
        fun failIfWarnings(enabled: Boolean = true): HasFrom = copy(failIfWarnings = enabled)

        /**
         * Write log messages in machine-readable JSON format to FILE.
         * All messages above DEBUG level will be written, regardless of verbosity settings.
         *
         * @param file Path to the log file
         */
        fun log(file: String): HasFrom = copy(log = file)
    }

    // ========================================================================
    // NEEDS TO
    // ========================================================================

    /**
     * State where input format and input source are set but output format is not yet set.
     */
    data class NeedsTo internal constructor(
        val from: InputFormat,
        val inputSource: InputSource,
        val standalone: Boolean? = null,
        val template: String? = null,
        val metadata: Map<String, String> = emptyMap(),
        val variables: Map<String, String> = emptyMap(),
        val toc: Boolean? = null,
        val tocDepth: Int? = null,
        val wrap: WrapOption? = null,
        val ascii: Boolean? = null,
        val numberSections: Boolean? = null,
        val numberOffset: List<Int>? = null,
        val topLevelDivision: TopLevelDivision? = null,
        val extractMedia: String? = null,
        val resourcePath: List<String>? = null,
        val includeInHeader: List<String>? = null,
        val includeBeforeBody: List<String>? = null,
        val includeAfterBody: List<String>? = null,
        val highlightStyle: String? = null,
        val syntaxDefinition: String? = null,
        val dpi: Int? = null,
        val eol: EOL? = null,
        val columns: Int? = null,
        val preserveTabs: Boolean? = null,
        val tabStop: Int? = null,
        val pdfEngine: String? = null,
        val pdfEngineOpt: List<String>? = null,
        val selfContained: Boolean? = null,
        val embedResources: Boolean? = null,
        val linkImages: Boolean? = null,
        val requestHeaders: Map<String, String>? = null,
        val noCheckCertificate: Boolean? = null,
        val abbreviations: String? = null,
        val indentedCodeClasses: String? = null,
        val defaultImageExtension: String? = null,
        val filters: List<String>? = null,
        val luaFilters: List<String>? = null,
        val shiftHeadingLevelBy: Int? = null,
        val baseHeaderLevel: Int? = null,
        val trackChanges: TrackChanges? = null,
        val stripComments: Boolean? = null,
        val referenceLinks: Boolean? = null,
        val referenceLocation: ReferenceLocation? = null,
        val figureCaptionPosition: CaptionPosition? = null,
        val tableCaptionPosition: CaptionPosition? = null,
        val markdownHeadings: MarkdownHeadingStyle? = null,
        val listTables: Boolean? = null,
        val listings: Boolean? = null,
        val incremental: Boolean? = null,
        val slideLevel: Int? = null,
        val sectionDivs: Boolean? = null,
        val htmlQTags: Boolean? = null,
        val emailObfuscation: EmailObfuscation? = null,
        val idPrefix: String? = null,
        val titlePrefix: String? = null,
        val css: String? = null,
        val citeproc: Boolean? = null,
        val bibliography: String? = null,
        val csl: String? = null,
        val citationAbbreviations: String? = null,
        val natbib: Boolean? = null,
        val biblatex: Boolean? = null,
        val mathml: Boolean? = null,
        val webtex: String? = null,
        val mathjax: String? = null,
        val katex: String? = null,
        val gladtex: Boolean? = null,
        val trace: Boolean? = null,
        val dumpArgs: Boolean? = null,
        val ignoreArgs: Boolean? = null,
        val verbose: Boolean? = null,
        val quiet: Boolean? = null,
        val failIfWarnings: Boolean? = null,
        val log: String? = null
    ) : PandocCommand() {

        fun to(format: OutputFormat): Complete = Complete(
            from = from,
            inputSource = inputSource,
            to = format,
            standalone = standalone,
            template = template,
            metadata = metadata,
            variables = variables,
            toc = toc,
            tocDepth = tocDepth,
            wrap = wrap,
            ascii = ascii,
            numberSections = numberSections,
            numberOffset = numberOffset,
            topLevelDivision = topLevelDivision,
            extractMedia = extractMedia,
            resourcePath = resourcePath,
            includeInHeader = includeInHeader,
            includeBeforeBody = includeBeforeBody,
            includeAfterBody = includeAfterBody,
            highlightStyle = highlightStyle,
            syntaxDefinition = syntaxDefinition,
            dpi = dpi,
            eol = eol,
            columns = columns,
            preserveTabs = preserveTabs,
            tabStop = tabStop,
            pdfEngine = pdfEngine,
            pdfEngineOpt = pdfEngineOpt,
            selfContained = selfContained,
            embedResources = embedResources,
            linkImages = linkImages,
            requestHeaders = requestHeaders,
            noCheckCertificate = noCheckCertificate,
            abbreviations = abbreviations,
            indentedCodeClasses = indentedCodeClasses,
            defaultImageExtension = defaultImageExtension,
            filters = filters,
            luaFilters = luaFilters,
            shiftHeadingLevelBy = shiftHeadingLevelBy,
            baseHeaderLevel = baseHeaderLevel,
            trackChanges = trackChanges,
            stripComments = stripComments,
            referenceLinks = referenceLinks,
            referenceLocation = referenceLocation,
            figureCaptionPosition = figureCaptionPosition,
            tableCaptionPosition = tableCaptionPosition,
            markdownHeadings = markdownHeadings,
            listTables = listTables,
            listings = listings,
            incremental = incremental,
            slideLevel = slideLevel,
            sectionDivs = sectionDivs,
            htmlQTags = htmlQTags,
            emailObfuscation = emailObfuscation,
            idPrefix = idPrefix,
            titlePrefix = titlePrefix,
            css = css,
            citeproc = citeproc,
            bibliography = bibliography,
            csl = csl,
            citationAbbreviations = citationAbbreviations,
            natbib = natbib,
            biblatex = biblatex,
            mathml = mathml,
            webtex = webtex,
            mathjax = mathjax,
            katex = katex,
            gladtex = gladtex,
            trace = trace,
            dumpArgs = dumpArgs,
            ignoreArgs = ignoreArgs,
            verbose = verbose,
            quiet = quiet,
            failIfWarnings = failIfWarnings,
            log = log
        )

        // Configuration setters
        // See HasFrom class for detailed documentation of each method

        /**
         * Produce output with an appropriate header and footer.
         * See [HasFrom.standalone] for details.
         */
        fun standalone(enabled: Boolean = true): NeedsTo = copy(standalone = enabled)

        /**
         * Use the specified file as a custom template.
         * See [HasFrom.template] for details.
         */
        fun template(file: String): NeedsTo = copy(template = file)

        /**
         * Set the metadata field KEY to the value VAL.
         * See [HasFrom.metadata] for details.
         */
        fun metadata(key: String, value: String): NeedsTo = copy(metadata = metadata + (key to value))

        /**
         * Set the template variable KEY to the string value VAL.
         * See [HasFrom.variable] for details.
         */
        fun variable(key: String, value: String): NeedsTo = copy(variables = variables + (key to value))

        /**
         * Include an automatically generated table of contents.
         * See [HasFrom.toc] for details.
         */
        fun toc(depth: Int? = null): NeedsTo = copy(toc = true, tocDepth = depth)

        /**
         * Determine how text is wrapped in the output.
         * See [HasFrom.wrap] for details.
         */
        fun wrap(option: WrapOption): NeedsTo = copy(wrap = option)

        /**
         * Use only ASCII characters in output.
         * See [HasFrom.ascii] for details.
         */
        fun ascii(enabled: Boolean = true): NeedsTo = copy(ascii = enabled)

        /**
         * Number section headings in output.
         * See [HasFrom.numberSections] for details.
         */
        fun numberSections(enabled: Boolean = true): NeedsTo = copy(numberSections = enabled)

        /**
         * Offsets for section heading numbers.
         * See [HasFrom.numberOffset] for details.
         */
        fun numberOffset(vararg offsets: Int): NeedsTo = copy(numberOffset = offsets.toList())

        /**
         * Treat top-level headings as the given division type.
         * See [HasFrom.topLevelDivision] for details.
         */
        fun topLevelDivision(division: TopLevelDivision): NeedsTo = copy(topLevelDivision = division)
        /**
         * Extract images and other media contained in or linked from the source document.
         * See [HasFrom.extractMedia] for details.
         */
        fun extractMedia(path: String): NeedsTo = copy(extractMedia = path)

        /**
         * List of paths to search for images and other resources.
         * See [HasFrom.resourcePath] for details.
         */
        fun resourcePath(vararg paths: String): NeedsTo = copy(resourcePath = paths.toList())

        /**
         * Include contents of FILE at the end of the header.
         * See [HasFrom.includeInHeader] for details.
         */
        fun includeInHeader(file: String): NeedsTo = copy(includeInHeader = (includeInHeader ?: emptyList()) + file)

        /**
         * Include contents of FILE at the beginning of the document body.
         * See [HasFrom.includeBeforeBody] for details.
         */
        fun includeBeforeBody(file: String): NeedsTo = copy(includeBeforeBody = (includeBeforeBody ?: emptyList()) + file)

        /**
         * Include contents of FILE at the end of the document body.
         * See [HasFrom.includeAfterBody] for details.
         */
        fun includeAfterBody(file: String): NeedsTo = copy(includeAfterBody = (includeAfterBody ?: emptyList()) + file)

        /**
         * Specifies the styling to use for highlighted source code.
         * See [HasFrom.highlightStyle] for details.
         */
        fun highlightStyle(style: String): NeedsTo = copy(highlightStyle = style)

        /**
         * Instructs pandoc to load a KDE XML syntax definition file.
         * See [HasFrom.syntaxDefinition] for details.
         */
        fun syntaxDefinition(file: String): NeedsTo = copy(syntaxDefinition = file)

        /**
         * Specify the default dpi value for conversion from pixels to inches.
         * See [HasFrom.dpi] for details.
         */
        fun dpi(value: Int): NeedsTo = copy(dpi = value)

        /**
         * Manually specify line endings.
         * See [HasFrom.eol] for details.
         */
        fun eol(option: EOL): NeedsTo = copy(eol = option)

        /**
         * Specify length of lines in characters.
         * See [HasFrom.columns] for details.
         */
        fun columns(value: Int): NeedsTo = copy(columns = value)
        /**
         * Preserve tabs instead of converting them to spaces.
         * See [HasFrom.preserveTabs] for details.
         */
        fun preserveTabs(enabled: Boolean = true): NeedsTo = copy(preserveTabs = enabled)

        /**
         * Specify the number of spaces per tab.
         * See [HasFrom.tabStop] for details.
         */
        fun tabStop(value: Int): NeedsTo = copy(tabStop = value)

        /**
         * Use the specified engine when producing PDF output.
         * See [HasFrom.pdfEngine] for details.
         */
        fun pdfEngine(engine: String): NeedsTo = copy(pdfEngine = engine)

        /**
         * Use the given string as a command-line argument to the PDF engine.
         * See [HasFrom.pdfEngineOpt] for details.
         */
        fun pdfEngineOpt(option: String): NeedsTo = copy(pdfEngineOpt = (pdfEngineOpt ?: emptyList()) + option)

        /**
         * Produce a standalone HTML file with no external dependencies.
         * See [HasFrom.embedResources] for details.
         */
        fun embedResources(enabled: Boolean = true): NeedsTo = copy(embedResources = enabled)

        /**
         * Include links to images instead of embedding the images in ODT output.
         * See [HasFrom.linkImages] for details.
         */
        fun linkImages(enabled: Boolean = true): NeedsTo = copy(linkImages = enabled)

        /**
         * This option is deprecated; use [embedResources] instead.
         * See [HasFrom.selfContained] for details.
         */
        fun selfContained(enabled: Boolean = true): NeedsTo = copy(selfContained = enabled)

        /**
         * Set the request header NAME to the value VAL when making HTTP requests.
         * See [HasFrom.requestHeader] for details.
         */
        fun requestHeader(name: String, value: String): NeedsTo = copy(requestHeaders = (requestHeaders ?: emptyMap()) + (name to value))

        /**
         * Disable the certificate verification to allow access to unsecure HTTP resources.
         * See [HasFrom.noCheckCertificate] for details.
         */
        fun noCheckCertificate(enabled: Boolean = true): NeedsTo = copy(noCheckCertificate = enabled)

        /**
         * Specifies a custom abbreviations file.
         * See [HasFrom.abbreviations] for details.
         */
        fun abbreviations(file: String): NeedsTo = copy(abbreviations = file)

        /**
         * Specify classes to use for indented code blocks.
         * See [HasFrom.indentedCodeClasses] for details.
         */
        fun indentedCodeClasses(classes: String): NeedsTo = copy(indentedCodeClasses = classes)

        /**
         * Specify a default extension to use when image paths/URLs have no extension.
         * See [HasFrom.defaultImageExtension] for details.
         */
        fun defaultImageExtension(extension: String): NeedsTo = copy(defaultImageExtension = extension)
        /**
         * Specify an executable to be used as a filter transforming the pandoc AST.
         * See [HasFrom.filter] for details.
         */
        fun filter(program: String): NeedsTo = copy(filters = (filters ?: emptyList()) + program)

        /**
         * Transform the document using pandoc's built-in Lua filtering system.
         * See [HasFrom.luaFilter] for details.
         */
        fun luaFilter(script: String): NeedsTo = copy(luaFilters = (luaFilters ?: emptyList()) + script)

        /**
         * Shift heading levels by a positive or negative integer.
         * See [HasFrom.shiftHeadingLevelBy] for details.
         */
        fun shiftHeadingLevelBy(value: Int): NeedsTo = copy(shiftHeadingLevelBy = value)

        /**
         * Deprecated. Use [shiftHeadingLevelBy] instead.
         * See [HasFrom.baseHeaderLevel] for details.
         */
        fun baseHeaderLevel(value: Int): NeedsTo = copy(baseHeaderLevel = value)

        /**
         * Specifies what to do with insertions, deletions, and comments produced by
         * the MS Word "Track Changes" feature.
         * See [HasFrom.trackChanges] for details.
         */
        fun trackChanges(mode: TrackChanges): NeedsTo = copy(trackChanges = mode)

        /**
         * Strip out HTML comments in the Markdown or Textile source.
         * See [HasFrom.stripComments] for details.
         */
        fun stripComments(enabled: Boolean = true): NeedsTo = copy(stripComments = enabled)

        /**
         * Use reference-style links, rather than inline links, in writing Markdown or reStructuredText.
         * See [HasFrom.referenceLinks] for details.
         */
        fun referenceLinks(enabled: Boolean = true): NeedsTo = copy(referenceLinks = enabled)

        /**
         * Specify whether footnotes and references are placed at the end of the
         * current block, section, or document.
         * See [HasFrom.referenceLocation] for details.
         */
        fun referenceLocation(location: ReferenceLocation): NeedsTo = copy(referenceLocation = location)

        /**
         * Specify whether figure captions go above or below figures.
         * See [HasFrom.figureCaptionPosition] for details.
         */
        fun figureCaptionPosition(position: CaptionPosition): NeedsTo = copy(figureCaptionPosition = position)

        /**
         * Specify whether table captions go above or below tables.
         * See [HasFrom.tableCaptionPosition] for details.
         */
        fun tableCaptionPosition(position: CaptionPosition): NeedsTo = copy(tableCaptionPosition = position)

        /**
         * Specify whether to use ATX-style or Setext-style headings for Markdown output.
         * See [HasFrom.markdownHeadings] for details.
         */
        fun markdownHeadings(style: MarkdownHeadingStyle): NeedsTo = copy(markdownHeadings = style)

        /**
         * Render tables as list tables in RST output.
         * See [HasFrom.listTables] for details.
         */
        fun listTables(enabled: Boolean = true): NeedsTo = copy(listTables = enabled)

        /**
         * Use the listings package for LaTeX code blocks.
         * See [HasFrom.listings] for details.
         */
        fun listings(enabled: Boolean = true): NeedsTo = copy(listings = enabled)
        /**
         * Make list items in slide shows display incrementally (one by one).
         * See [HasFrom.incremental] for details.
         */
        fun incremental(enabled: Boolean = true): NeedsTo = copy(incremental = enabled)

        /**
         * Specifies that headings with the specified level create slides.
         * See [HasFrom.slideLevel] for details.
         */
        fun slideLevel(value: Int): NeedsTo = copy(slideLevel = value)

        /**
         * Wrap sections in section tags (or div tags for html4).
         * See [HasFrom.sectionDivs] for details.
         */
        fun sectionDivs(enabled: Boolean = true): NeedsTo = copy(sectionDivs = enabled)

        /**
         * Use q tags for quotes in HTML.
         * See [HasFrom.htmlQTags] for details.
         */
        fun htmlQTags(enabled: Boolean = true): NeedsTo = copy(htmlQTags = enabled)

        /**
         * Specify a method for obfuscating mailto: links in HTML documents.
         * See [HasFrom.emailObfuscation] for details.
         */
        fun emailObfuscation(mode: EmailObfuscation): NeedsTo = copy(emailObfuscation = mode)

        /**
         * Specify a prefix to be added to all identifiers and internal links.
         * See [HasFrom.idPrefix] for details.
         */
        fun idPrefix(prefix: String): NeedsTo = copy(idPrefix = prefix)

        /**
         * Specify a prefix at the beginning of the title that appears in the HTML header.
         * See [HasFrom.titlePrefix] for details.
         */
        fun titlePrefix(prefix: String): NeedsTo = copy(titlePrefix = prefix)

        /**
         * Link to a CSS style sheet.
         * See [HasFrom.css] for details.
         */
        fun css(url: String): NeedsTo = copy(css = url)
        /**
         * Process the citations in the file, replacing them with rendered citations.
         * See [HasFrom.citeproc] for details.
         */
        fun citeproc(enabled: Boolean = true): NeedsTo = copy(citeproc = enabled)

        /**
         * Set the bibliography field in the document's metadata to FILE.
         * See [HasFrom.bibliography] for details.
         */
        fun bibliography(file: String): NeedsTo = copy(bibliography = file)

        /**
         * Set the csl field in the document's metadata to FILE.
         * See [HasFrom.csl] for details.
         */
        fun csl(file: String): NeedsTo = copy(csl = file)

        /**
         * Set the citation-abbreviations field in the document's metadata to FILE.
         * See [HasFrom.citationAbbreviations] for details.
         */
        fun citationAbbreviations(file: String): NeedsTo = copy(citationAbbreviations = file)

        /**
         * Use natbib for citations in LaTeX output.
         * See [HasFrom.natbib] for details.
         */
        fun natbib(enabled: Boolean = true): NeedsTo = copy(natbib = enabled)

        /**
         * Use biblatex for citations in LaTeX output.
         * See [HasFrom.biblatex] for details.
         */
        fun biblatex(enabled: Boolean = true): NeedsTo = copy(biblatex = enabled)

        /**
         * Convert TeX math to MathML.
         * See [HasFrom.mathml] for details.
         */
        fun mathml(enabled: Boolean = true): NeedsTo = copy(mathml = enabled)

        /**
         * Convert TeX formulas to img tags that link to an external script.
         * See [HasFrom.webtex] for details.
         */
        fun webtex(url: String? = null): NeedsTo = copy(webtex = url)

        /**
         * Use MathJax to display embedded TeX math in HTML output.
         * See [HasFrom.mathjax] for details.
         */
        fun mathjax(url: String? = null): NeedsTo = copy(mathjax = url)

        /**
         * Use KaTeX to display embedded TeX math in HTML output.
         * See [HasFrom.katex] for details.
         */
        fun katex(url: String? = null): NeedsTo = copy(katex = url)

        /**
         * Enclose TeX math in eq tags in HTML output.
         * See [HasFrom.gladtex] for details.
         */
        fun gladtex(enabled: Boolean = true): NeedsTo = copy(gladtex = enabled)
        /**
         * Print diagnostic output tracing parser progress to stderr.
         * See [HasFrom.trace] for details.
         */
        fun trace(enabled: Boolean = true): NeedsTo = copy(trace = enabled)

        /**
         * Print information about command-line arguments to stdout, then exit.
         * See [HasFrom.dumpArgs] for details.
         */
        fun dumpArgs(enabled: Boolean = true): NeedsTo = copy(dumpArgs = enabled)

        /**
         * Ignore command-line arguments (for use in wrapper scripts).
         * See [HasFrom.ignoreArgs] for details.
         */
        fun ignoreArgs(enabled: Boolean = true): NeedsTo = copy(ignoreArgs = enabled)

        /**
         * Give verbose debugging output.
         * See [HasFrom.verbose] for details.
         */
        fun verbose(enabled: Boolean = true): NeedsTo = copy(verbose = enabled)

        /**
         * Suppress warning messages.
         * See [HasFrom.quiet] for details.
         */
        fun quiet(enabled: Boolean = true): NeedsTo = copy(quiet = enabled)

        /**
         * Exit with error status if there are any warnings.
         * See [HasFrom.failIfWarnings] for details.
         */
        fun failIfWarnings(enabled: Boolean = true): NeedsTo = copy(failIfWarnings = enabled)

        /**
         * Write log messages in machine-readable JSON format to FILE.
         * See [HasFrom.log] for details.
         */
        fun log(file: String): NeedsTo = copy(log = file)
    }

    // ========================================================================
    // COMPLETE STATE
    // ========================================================================

    /**
     * Complete state with all required fields set (input format, input source, output format).
     *
     * This state has all terminal operations ([outputString], [outputFile], [outputWriter]).
     */
    data class Complete internal constructor(
        val from: InputFormat,
        val inputSource: InputSource,
        val to: OutputFormat,
        val standalone: Boolean? = null,
        val template: String? = null,
        val metadata: Map<String, String> = emptyMap(),
        val variables: Map<String, String> = emptyMap(),
        val toc: Boolean? = null,
        val tocDepth: Int? = null,
        val wrap: WrapOption? = null,
        val ascii: Boolean? = null,
        val numberSections: Boolean? = null,
        val numberOffset: List<Int>? = null,
        val topLevelDivision: TopLevelDivision? = null,
        val extractMedia: String? = null,
        val resourcePath: List<String>? = null,
        val includeInHeader: List<String>? = null,
        val includeBeforeBody: List<String>? = null,
        val includeAfterBody: List<String>? = null,
        val highlightStyle: String? = null,
        val syntaxDefinition: String? = null,
        val dpi: Int? = null,
        val eol: EOL? = null,
        val columns: Int? = null,
        val preserveTabs: Boolean? = null,
        val tabStop: Int? = null,
        val pdfEngine: String? = null,
        val pdfEngineOpt: List<String>? = null,
        val selfContained: Boolean? = null,
        val embedResources: Boolean? = null,
        val linkImages: Boolean? = null,
        val requestHeaders: Map<String, String>? = null,
        val noCheckCertificate: Boolean? = null,
        val abbreviations: String? = null,
        val indentedCodeClasses: String? = null,
        val defaultImageExtension: String? = null,
        val filters: List<String>? = null,
        val luaFilters: List<String>? = null,
        val shiftHeadingLevelBy: Int? = null,
        val baseHeaderLevel: Int? = null,
        val trackChanges: TrackChanges? = null,
        val stripComments: Boolean? = null,
        val referenceLinks: Boolean? = null,
        val referenceLocation: ReferenceLocation? = null,
        val figureCaptionPosition: CaptionPosition? = null,
        val tableCaptionPosition: CaptionPosition? = null,
        val markdownHeadings: MarkdownHeadingStyle? = null,
        val listTables: Boolean? = null,
        val listings: Boolean? = null,
        val incremental: Boolean? = null,
        val slideLevel: Int? = null,
        val sectionDivs: Boolean? = null,
        val htmlQTags: Boolean? = null,
        val emailObfuscation: EmailObfuscation? = null,
        val idPrefix: String? = null,
        val titlePrefix: String? = null,
        val css: String? = null,
        val citeproc: Boolean? = null,
        val bibliography: String? = null,
        val csl: String? = null,
        val citationAbbreviations: String? = null,
        val natbib: Boolean? = null,
        val biblatex: Boolean? = null,
        val mathml: Boolean? = null,
        val webtex: String? = null,
        val mathjax: String? = null,
        val katex: String? = null,
        val gladtex: Boolean? = null,
        val trace: Boolean? = null,
        val dumpArgs: Boolean? = null,
        val ignoreArgs: Boolean? = null,
        val verbose: Boolean? = null,
        val quiet: Boolean? = null,
        val failIfWarnings: Boolean? = null,
        val log: String? = null
    ) : PandocCommand() {

        // Configuration setters
        // See HasFrom class for detailed documentation of each method

        /** Produce output with an appropriate header and footer. See [HasFrom.standalone] */
        fun standalone(enabled: Boolean = true): Complete = copy(standalone = enabled)

        /** Use the specified file as a custom template. See [HasFrom.template] */
        fun template(file: String): Complete = copy(template = file)

        /** Set the metadata field KEY to the value VAL. See [HasFrom.metadata] */
        fun metadata(key: String, value: String): Complete = copy(metadata = metadata + (key to value))

        /** Set the template variable KEY to the string value VAL. See [HasFrom.variable] */
        fun variable(key: String, value: String): Complete = copy(variables = variables + (key to value))

        /** Include an automatically generated table of contents. See [HasFrom.toc] */
        fun toc(depth: Int? = null): Complete = copy(toc = true, tocDepth = depth)

        /** Determine how text is wrapped in the output. See [HasFrom.wrap] */
        fun wrap(option: WrapOption): Complete = copy(wrap = option)

        /** Use only ASCII characters in output. See [HasFrom.ascii] */
        fun ascii(enabled: Boolean = true): Complete = copy(ascii = enabled)

        /** Number section headings in output. See [HasFrom.numberSections] */
        fun numberSections(enabled: Boolean = true): Complete = copy(numberSections = enabled)

        /** Offsets for section heading numbers. See [HasFrom.numberOffset] */
        fun numberOffset(vararg offsets: Int): Complete = copy(numberOffset = offsets.toList())

        /** Treat top-level headings as the given division type. See [HasFrom.topLevelDivision] */
        fun topLevelDivision(division: TopLevelDivision): Complete = copy(topLevelDivision = division)
        /** Extract images and other media. See [HasFrom.extractMedia] */
        fun extractMedia(path: String): Complete = copy(extractMedia = path)

        /** List of paths to search for images and other resources. See [HasFrom.resourcePath] */
        fun resourcePath(vararg paths: String): Complete = copy(resourcePath = paths.toList())

        /** Include contents at the end of the header. See [HasFrom.includeInHeader] */
        fun includeInHeader(file: String): Complete = copy(includeInHeader = (includeInHeader ?: emptyList()) + file)

        /** Include contents at the beginning of the document body. See [HasFrom.includeBeforeBody] */
        fun includeBeforeBody(file: String): Complete = copy(includeBeforeBody = (includeBeforeBody ?: emptyList()) + file)

        /** Include contents at the end of the document body. See [HasFrom.includeAfterBody] */
        fun includeAfterBody(file: String): Complete = copy(includeAfterBody = (includeAfterBody ?: emptyList()) + file)

        /** Specifies the styling for highlighted source code. See [HasFrom.highlightStyle] */
        fun highlightStyle(style: String): Complete = copy(highlightStyle = style)

        /** Instructs pandoc to load a KDE XML syntax definition file. See [HasFrom.syntaxDefinition] */
        fun syntaxDefinition(file: String): Complete = copy(syntaxDefinition = file)

        /** Specify the default dpi value. See [HasFrom.dpi] */
        fun dpi(value: Int): Complete = copy(dpi = value)

        /** Manually specify line endings. See [HasFrom.eol] */
        fun eol(option: EOL): Complete = copy(eol = option)

        /** Specify length of lines in characters. See [HasFrom.columns] */
        fun columns(value: Int): Complete = copy(columns = value)
        /** Preserve tabs instead of converting to spaces. See [HasFrom.preserveTabs] */
        fun preserveTabs(enabled: Boolean = true): Complete = copy(preserveTabs = enabled)

        /** Specify the number of spaces per tab. See [HasFrom.tabStop] */
        fun tabStop(value: Int): Complete = copy(tabStop = value)

        /** Use the specified engine when producing PDF output. See [HasFrom.pdfEngine] */
        fun pdfEngine(engine: String): Complete = copy(pdfEngine = engine)

        /** Use as command-line argument to the PDF engine. See [HasFrom.pdfEngineOpt] */
        fun pdfEngineOpt(option: String): Complete = copy(pdfEngineOpt = (pdfEngineOpt ?: emptyList()) + option)

        /** Deprecated; use [embedResources] instead. See [HasFrom.selfContained] */
        fun selfContained(enabled: Boolean = true): Complete = copy(selfContained = enabled)

        /** Produce standalone HTML with no external dependencies. See [HasFrom.embedResources] */
        fun embedResources(enabled: Boolean = true): Complete = copy(embedResources = enabled)

        /** Include links to images instead of embedding. See [HasFrom.linkImages] */
        fun linkImages(enabled: Boolean = true): Complete = copy(linkImages = enabled)

        /** Set the request header NAME to VALUE. See [HasFrom.requestHeader] */
        fun requestHeader(name: String, value: String): Complete = copy(requestHeaders = (requestHeaders ?: emptyMap()) + (name to value))

        /** Disable certificate verification. See [HasFrom.noCheckCertificate] */
        fun noCheckCertificate(enabled: Boolean = true): Complete = copy(noCheckCertificate = enabled)

        /** Specifies a custom abbreviations file. See [HasFrom.abbreviations] */
        fun abbreviations(file: String): Complete = copy(abbreviations = file)

        /** Specify classes for indented code blocks. See [HasFrom.indentedCodeClasses] */
        fun indentedCodeClasses(classes: String): Complete = copy(indentedCodeClasses = classes)

        /** Specify default extension for images. See [HasFrom.defaultImageExtension] */
        fun defaultImageExtension(extension: String): Complete = copy(defaultImageExtension = extension)

        /** Specify executable as a filter. See [HasFrom.filter] */
        fun filter(program: String): Complete = copy(filters = (filters ?: emptyList()) + program)

        /** Transform document using Lua filtering. See [HasFrom.luaFilter] */
        fun luaFilter(script: String): Complete = copy(luaFilters = (luaFilters ?: emptyList()) + script)

        /** Shift heading levels. See [HasFrom.shiftHeadingLevelBy] */
        fun shiftHeadingLevelBy(value: Int): Complete = copy(shiftHeadingLevelBy = value)

        /** Deprecated. Use [shiftHeadingLevelBy] instead. See [HasFrom.baseHeaderLevel] */
        fun baseHeaderLevel(value: Int): Complete = copy(baseHeaderLevel = value)

        /** Track Changes mode for MS Word. See [HasFrom.trackChanges] */
        fun trackChanges(mode: TrackChanges): Complete = copy(trackChanges = mode)
        /** Strip HTML comments. See [HasFrom.stripComments] */
        fun stripComments(enabled: Boolean = true): Complete = copy(stripComments = enabled)

        /** Use reference-style links. See [HasFrom.referenceLinks] */
        fun referenceLinks(enabled: Boolean = true): Complete = copy(referenceLinks = enabled)

        /** Reference location for footnotes. See [HasFrom.referenceLocation] */
        fun referenceLocation(location: ReferenceLocation): Complete = copy(referenceLocation = location)

        /** Figure caption position. See [HasFrom.figureCaptionPosition] */
        fun figureCaptionPosition(position: CaptionPosition): Complete = copy(figureCaptionPosition = position)

        /** Table caption position. See [HasFrom.tableCaptionPosition] */
        fun tableCaptionPosition(position: CaptionPosition): Complete = copy(tableCaptionPosition = position)

        /** Markdown heading style. See [HasFrom.markdownHeadings] */
        fun markdownHeadings(style: MarkdownHeadingStyle): Complete = copy(markdownHeadings = style)

        /** Render tables as list tables in RST. See [HasFrom.listTables] */
        fun listTables(enabled: Boolean = true): Complete = copy(listTables = enabled)

        /** Use listings package for LaTeX. See [HasFrom.listings] */
        fun listings(enabled: Boolean = true): Complete = copy(listings = enabled)

        /** Make list items display incrementally. See [HasFrom.incremental] */
        fun incremental(enabled: Boolean = true): Complete = copy(incremental = enabled)

        /** Heading level that creates slides. See [HasFrom.slideLevel] */
        fun slideLevel(value: Int): Complete = copy(slideLevel = value)

        /** Wrap sections in section/div tags. See [HasFrom.sectionDivs] */
        fun sectionDivs(enabled: Boolean = true): Complete = copy(sectionDivs = enabled)

        /** Use q tags for quotes in HTML. See [HasFrom.htmlQTags] */
        fun htmlQTags(enabled: Boolean = true): Complete = copy(htmlQTags = enabled)

        /** Email obfuscation method. See [HasFrom.emailObfuscation] */
        fun emailObfuscation(mode: EmailObfuscation): Complete = copy(emailObfuscation = mode)
        /** Prefix for identifiers and internal links. See [HasFrom.idPrefix] */
        fun idPrefix(prefix: String): Complete = copy(idPrefix = prefix)

        /** Prefix for the HTML title. See [HasFrom.titlePrefix] */
        fun titlePrefix(prefix: String): Complete = copy(titlePrefix = prefix)

        /** Link to a CSS style sheet. See [HasFrom.css] */
        fun css(url: String): Complete = copy(css = url)

        /** Process citations. See [HasFrom.citeproc] */
        fun citeproc(enabled: Boolean = true): Complete = copy(citeproc = enabled)

        /** Set bibliography file. See [HasFrom.bibliography] */
        fun bibliography(file: String): Complete = copy(bibliography = file)

        /** Set CSL file. See [HasFrom.csl] */
        fun csl(file: String): Complete = copy(csl = file)

        /** Set citation abbreviations file. See [HasFrom.citationAbbreviations] */
        fun citationAbbreviations(file: String): Complete = copy(citationAbbreviations = file)

        /** Use natbib for LaTeX citations. See [HasFrom.natbib] */
        fun natbib(enabled: Boolean = true): Complete = copy(natbib = enabled)

        /** Use biblatex for LaTeX citations. See [HasFrom.biblatex] */
        fun biblatex(enabled: Boolean = true): Complete = copy(biblatex = enabled)

        /** Convert TeX math to MathML. See [HasFrom.mathml] */
        fun mathml(enabled: Boolean = true): Complete = copy(mathml = enabled)

        /** Convert TeX formulas to img tags. See [HasFrom.webtex] */
        fun webtex(url: String? = null): Complete = copy(webtex = url)

        /** Use MathJax for TeX math. See [HasFrom.mathjax] */
        fun mathjax(url: String? = null): Complete = copy(mathjax = url)

        /** Use KaTeX for TeX math. See [HasFrom.katex] */
        fun katex(url: String? = null): Complete = copy(katex = url)

        /** Enclose TeX math in eq tags. See [HasFrom.gladtex] */
        fun gladtex(enabled: Boolean = true): Complete = copy(gladtex = enabled)
        /** Print diagnostic output tracing parser progress. See [HasFrom.trace] */
        fun trace(enabled: Boolean = true): Complete = copy(trace = enabled)

        /** Print command-line arguments info. See [HasFrom.dumpArgs] */
        fun dumpArgs(enabled: Boolean = true): Complete = copy(dumpArgs = enabled)

        /** Ignore command-line arguments. See [HasFrom.ignoreArgs] */
        fun ignoreArgs(enabled: Boolean = true): Complete = copy(ignoreArgs = enabled)

        /** Give verbose debugging output. See [HasFrom.verbose] */
        fun verbose(enabled: Boolean = true): Complete = copy(verbose = enabled)

        /** Suppress warning messages. See [HasFrom.quiet] */
        fun quiet(enabled: Boolean = true): Complete = copy(quiet = enabled)

        /** Exit with error status on warnings. See [HasFrom.failIfWarnings] */
        fun failIfWarnings(enabled: Boolean = true): Complete = copy(failIfWarnings = enabled)

        /** Write log messages in JSON format to FILE. See [HasFrom.log] */
        fun log(file: String): Complete = copy(log = file)

        // Terminal operations

        suspend fun outputString(): String = withContext(Dispatchers.IO) {
            val command = buildCommandLine()
            runPandoc(command, inputSource)
        }

        suspend fun outputFile(file: String) = withContext(Dispatchers.IO) {
            val command = buildCommandLine(file)
            runPandoc(command, inputSource)
        }

        suspend fun outputWriter(writer: Writer) = withContext(Dispatchers.IO) {
            val command = buildCommandLine()
            runPandoc(command, inputSource, writer)
        }

        private fun buildCommandLine(outputFile: String? = null): List<String> {
            val args = mutableListOf<String>("pandoc")

            args.addAll(listOf("-f", from.value))
            args.addAll(listOf("-t", to.value))
            outputFile?.let { args.addAll(listOf("-o", it)) }

            standalone?.let { if (it) args.add("--standalone") }
            template?.let { args.addAll(listOf("--template", it)) }
            wrap?.let { args.addAll(listOf("--wrap", it.value)) }
            ascii?.let { if (it) args.add("--ascii") }

            metadata.forEach { (key, value) ->
                args.addAll(listOf("-M", "$key=$value"))
            }
            variables.forEach { (key, value) ->
                args.addAll(listOf("-V", "$key=$value"))
            }

            toc?.let { if (it) args.add("--toc") }
            tocDepth?.let { args.addAll(listOf("--toc-depth", it.toString())) }
            numberSections?.let { if (it) args.add("--number-sections") }
            numberOffset?.let { args.addAll(listOf("--number-offset", it.joinToString(","))) }
            topLevelDivision?.let { args.addAll(listOf("--top-level-division", it.value)) }
            extractMedia?.let { args.addAll(listOf("--extract-media", it)) }
            resourcePath?.let { args.addAll(it.flatMap { listOf("--resource-path", it) }) }
            includeInHeader?.let { it.forEach { h -> args.addAll(listOf("--include-in-header", h)) } }
            includeBeforeBody?.let { it.forEach { h -> args.addAll(listOf("--include-before-body", h)) } }
            includeAfterBody?.let { it.forEach { h -> args.addAll(listOf("--include-after-body", h)) } }
            highlightStyle?.let { args.addAll(listOf("--highlight-style", it)) }
            syntaxDefinition?.let { args.addAll(listOf("--syntax-definition", it)) }
            dpi?.let { args.addAll(listOf("--dpi", it.toString())) }
            eol?.let { args.addAll(listOf("--eol", it.value)) }
            columns?.let { args.addAll(listOf("--columns", it.toString())) }
            preserveTabs?.let { if (it) args.add("--preserve-tabs") }
            tabStop?.let { args.addAll(listOf("--tab-stop", it.toString())) }
            pdfEngine?.let { args.addAll(listOf("--pdf-engine", it)) }
            pdfEngineOpt?.let { it.forEach { opt -> args.addAll(listOf("--pdf-engine-opt", opt)) } }
            selfContained?.let { if (it) args.add("--self-contained") }
            embedResources?.let { if (it) args.add("--embed-resources") }
            linkImages?.let { if (it) args.add("--link-images") }
            requestHeaders?.let { it.forEach { (k, v) -> args.addAll(listOf("--request-header", "$k:$v")) } }
            noCheckCertificate?.let { if (it) args.add("--no-check-certificate") }
            abbreviations?.let { args.addAll(listOf("--abbreviations", it)) }
            indentedCodeClasses?.let { args.addAll(listOf("--indented-code-classes", it)) }
            defaultImageExtension?.let { args.addAll(listOf("--default-image-extension", it)) }
            filters?.let { it.forEach { f -> args.addAll(listOf("--filter", f)) } }
            luaFilters?.let { it.forEach { f -> args.addAll(listOf("--lua-filter", f)) } }
            shiftHeadingLevelBy?.let { args.addAll(listOf("--shift-heading-level-by", it.toString())) }
            baseHeaderLevel?.let { args.addAll(listOf("--base-header-level", it.toString())) }
            trackChanges?.let { args.addAll(listOf("--track-changes", it.value)) }
            stripComments?.let { if (it) args.add("--strip-comments") }
            referenceLinks?.let { if (it) args.add("--reference-links") }
            referenceLocation?.let { args.addAll(listOf("--reference-location", it.value)) }
            figureCaptionPosition?.let { args.addAll(listOf("--figure-caption-position", it.value)) }
            tableCaptionPosition?.let { args.addAll(listOf("--table-caption-position", it.value)) }
            markdownHeadings?.let { args.addAll(listOf("--markdown-headings", it.value)) }
            listTables?.let { if (it) args.add("--list-tables") }
            listings?.let { if (it) args.add("--listings") }
            incremental?.let { if (it) args.add("--incremental") }
            slideLevel?.let { args.addAll(listOf("--slide-level", it.toString())) }
            sectionDivs?.let { if (it) args.add("--section-divs") }
            htmlQTags?.let { if (it) args.add("--html-q-tags") }
            emailObfuscation?.let { args.addAll(listOf("--email-obfuscation", it.value)) }
            idPrefix?.let { args.addAll(listOf("--id-prefix", it)) }
            titlePrefix?.let { args.addAll(listOf("--title-prefix", it)) }
            css?.let { args.addAll(listOf("--css", it)) }
            citeproc?.let { if (it) args.add("--citeproc") }
            bibliography?.let { args.addAll(listOf("--bibliography", it)) }
            csl?.let { args.addAll(listOf("--csl", it)) }
            citationAbbreviations?.let { args.addAll(listOf("--citation-abbreviations", it)) }
            natbib?.let { if (it) args.add("--natbib") }
            biblatex?.let { if (it) args.add("--biblatex") }
            mathml?.let { if (it) args.add("--mathml") }
            webtex?.let { args.addAll(listOf("--webtex", it)) }
            mathjax?.let { args.addAll(listOf("--mathjax", it)) }
            katex?.let { args.addAll(listOf("--katex", it)) }
            gladtex?.let { if (it) args.add("--gladtex") }
            trace?.let { if (it) args.add("--trace") }
            dumpArgs?.let { if (it) args.add("--dump-args") }
            ignoreArgs?.let { if (it) args.add("--ignore-args") }
            verbose?.let { if (it) args.add("--verbose") }
            quiet?.let { if (it) args.add("--quiet") }
            failIfWarnings?.let { if (it) args.add("--fail-if-warnings") }
            log?.let { args.addAll(listOf("--log", it)) }

            when (inputSource) {
                is InputSource.Files -> args.addAll(inputSource.files)
                is InputSource.StringInput -> args.add("-")
                is InputSource.ReaderInput -> args.add("-")
            }

            return args
        }

        private fun runPandoc(command: List<String>, inputSource: InputSource): String {
            try {
                val process = ProcessBuilder(command)
                    .redirectErrorStream(false)
                    .start()

                when (inputSource) {
                    is InputSource.StringInput -> {
                        process.outputStream.bufferedWriter().use { writer ->
                            writer.write(inputSource.content)
                            writer.flush()
                        }
                    }
                    is InputSource.ReaderInput -> {
                        process.outputStream.bufferedWriter().use { writer ->
                            inputSource.reader.use { reader ->
                                reader.copyTo(writer)
                            }
                        }
                    }
                    is InputSource.Files -> {
                        // Files are passed as arguments, no stdin needed
                    }
                }

                val stdout = process.inputStream.bufferedReader().readText()
                val stderr = process.errorStream.bufferedReader().readText()
                val exitCode = process.waitFor()

                if (exitCode != 0) {
                    throw PandocExecutionException(
                        message = "Pandoc execution failed with exit code $exitCode",
                        exitCode = exitCode,
                        command = command,
                        stdout = stdout,
                        stderr = stderr
                    )
                }

                return stdout
            } catch (e: Exception) {
                if (e is PandocExecutionException) throw e
                throw PandocNotFoundException("Failed to execute pandoc", e)
            }
        }

        private fun runPandoc(command: List<String>, inputSource: InputSource, writer: Writer) {
            try {
                val process = ProcessBuilder(command)
                    .redirectErrorStream(false)
                    .start()

                // Write input to stdin
                when (inputSource) {
                    is InputSource.StringInput -> {
                        process.outputStream.bufferedWriter().use { w ->
                            w.write(inputSource.content)
                            w.flush()
                        }
                    }
                    is InputSource.ReaderInput -> {
                        process.outputStream.bufferedWriter().use { w ->
                            inputSource.reader.use { reader ->
                                reader.copyTo(w)
                            }
                        }
                    }
                    is InputSource.Files -> {
                        // Files are passed as arguments, no stdin needed
                    }
                }

                // Read stdout and write to the provided Writer
                process.inputStream.bufferedReader().use { reader ->
                    reader.copyTo(writer)
                }

                val stderr = process.errorStream.bufferedReader().readText()
                val exitCode = process.waitFor()

                if (exitCode != 0) {
                    throw PandocExecutionException(
                        message = "Pandoc execution failed with exit code $exitCode",
                        exitCode = exitCode,
                        command = command,
                        stdout = "",
                        stderr = stderr
                    )
                }
            } catch (e: Exception) {
                if (e is PandocExecutionException) throw e
                throw PandocNotFoundException("Failed to execute pandoc", e)
            }
        }
    }
}

/**
 * Main entry point for the Pandoc fluent API.
 *
 * Example usage:
 * ```kotlin
 * // Simple conversion from file to String
 * suspend fun example() {
 *     val html: String = Pandoc.convert()
 *         .from(InputFormat.MARKDOWN)
 *         .inputFile("readme.md")
 *         .to(OutputFormat.HTML)
 *         .standalone()
 *         .outputString()
 *
 *     // Simple conversion from string to String
 *     val html2: String = Pandoc.convert()
 *         .from(InputFormat.MARKDOWN)
 *         .inputString("# Hello")
 *         .to(OutputFormat.HTML)
 *         .outputString()
 *
 *     // Conversion from Reader to String
 *     val html3: String = Pandoc.convert()
 *         .from(InputFormat.MARKDOWN)
 *         .inputReader(reader)
 *         .to(OutputFormat.HTML)
 *         .outputString()
 *
 *     // Conversion to file
 *     Pandoc.convert()
 *         .from(InputFormat.MARKDOWN)
 *         .inputFile("input.md")
 *         .to(OutputFormat.HTML)
 *         .outputFile("output.html")
 *
 *     // Conversion to Writer
 *     val writer = java.io.StringWriter()
 *     Pandoc.convert()
 *         .from(InputFormat.MARKDOWN)
 *         .inputReader(reader)
 *         .to(OutputFormat.HTML)
 *         .outputWriter(writer)
 * }
 * ```
 */
object Pandoc {

    /**
     * Start building a pandoc conversion command.
     *
     * @return An [Incomplete] state ready for configuration
     */
    fun convert(): PandocCommand.Incomplete = PandocCommand.Incomplete()
}
