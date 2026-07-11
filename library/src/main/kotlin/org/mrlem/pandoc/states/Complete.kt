package org.mrlem.pandoc.states

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
import org.mrlem.pandoc.exceptions.PandocExecutionException
import org.mrlem.pandoc.exceptions.PandocNotFoundException
import java.io.Writer

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
    fun wrap(option: WrapMode): Complete = copy(wrapMode = option)

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
    fun requestHeader(name: String, value: String): Complete =
        copy(requestHeaders = (requestHeaders ?: emptyMap()) + (name to value))

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
        wrapMode?.let { args.addAll(listOf("--wrap", it.value)) }
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