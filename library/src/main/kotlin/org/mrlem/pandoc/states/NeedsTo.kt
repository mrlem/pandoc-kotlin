package org.mrlem.pandoc.states

import org.mrlem.pandoc.InputSource
import org.mrlem.pandoc.enums.CaptionPosition
import org.mrlem.pandoc.enums.EOL
import org.mrlem.pandoc.enums.EmailObfuscation
import org.mrlem.pandoc.enums.InputFormat
import org.mrlem.pandoc.enums.MarkdownHeadingStyle
import org.mrlem.pandoc.enums.OutputFormat
import org.mrlem.pandoc.enums.ReferenceLocation
import org.mrlem.pandoc.enums.TopLevelDivision
import org.mrlem.pandoc.enums.TrackChanges
import org.mrlem.pandoc.enums.WrapMode

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
    val wrapMode: WrapMode? = null,
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
) : CommandState {

    /**
     * Specify the output format.
     * Format can be any of the supported output formats including:
     * html, pdf, docx, markdown, latex, epub, pptx, odt, rtf, plain, and many others.
     * For the full list, see [OutputFormat] enum.
     *
     * @param format The output format
     * @return A [Complete] state ready for execution with terminal operations
     */
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
        wrapMode = wrapMode,
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
    fun wrap(option: WrapMode): NeedsTo = copy(wrapMode = option)

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
    fun requestHeader(name: String, value: String): NeedsTo =
        copy(requestHeaders = (requestHeaders ?: emptyMap()) + (name to value))

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