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
 * DSL marker annotation to prevent accidental mixing of different DSL contexts.
 */
@DslMarker
annotation class PandocDsl

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
    @PandocDsl
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
    @PandocDsl
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
        fun standalone(enabled: Boolean = true): HasFrom = copy(standalone = enabled)
        fun template(file: String): HasFrom = copy(template = file)
        fun metadata(key: String, value: String): HasFrom = copy(metadata = metadata + (key to value))
        fun variable(key: String, value: String): HasFrom = copy(variables = variables + (key to value))
        fun toc(depth: Int? = null): HasFrom = copy(toc = true, tocDepth = depth)
        fun wrap(option: WrapOption): HasFrom = copy(wrap = option)
        fun ascii(enabled: Boolean = true): HasFrom = copy(ascii = enabled)
        fun numberSections(enabled: Boolean = true): HasFrom = copy(numberSections = enabled)
        fun numberOffset(vararg offsets: Int): HasFrom = copy(numberOffset = offsets.toList())
        fun topLevelDivision(division: TopLevelDivision): HasFrom = copy(topLevelDivision = division)
        fun extractMedia(path: String): HasFrom = copy(extractMedia = path)
        fun resourcePath(vararg paths: String): HasFrom = copy(resourcePath = paths.toList())
        fun includeInHeader(file: String): HasFrom = copy(includeInHeader = (includeInHeader ?: emptyList()) + file)
        fun includeBeforeBody(file: String): HasFrom = copy(includeBeforeBody = (includeBeforeBody ?: emptyList()) + file)
        fun includeAfterBody(file: String): HasFrom = copy(includeAfterBody = (includeAfterBody ?: emptyList()) + file)
        fun highlightStyle(style: String): HasFrom = copy(highlightStyle = style)
        fun syntaxDefinition(file: String): HasFrom = copy(syntaxDefinition = file)
        fun dpi(value: Int): HasFrom = copy(dpi = value)
        fun eol(option: EOL): HasFrom = copy(eol = option)
        fun columns(value: Int): HasFrom = copy(columns = value)
        fun preserveTabs(enabled: Boolean = true): HasFrom = copy(preserveTabs = enabled)
        fun tabStop(value: Int): HasFrom = copy(tabStop = value)
        fun pdfEngine(engine: String): HasFrom = copy(pdfEngine = engine)
        fun pdfEngineOpt(option: String): HasFrom = copy(pdfEngineOpt = (pdfEngineOpt ?: emptyList()) + option)
        fun selfContained(enabled: Boolean = true): HasFrom = copy(selfContained = enabled)
        fun embedResources(enabled: Boolean = true): HasFrom = copy(embedResources = enabled)
        fun linkImages(enabled: Boolean = true): HasFrom = copy(linkImages = enabled)
        fun requestHeader(name: String, value: String): HasFrom = copy(requestHeaders = (requestHeaders ?: emptyMap()) + (name to value))
        fun noCheckCertificate(enabled: Boolean = true): HasFrom = copy(noCheckCertificate = enabled)
        fun abbreviations(file: String): HasFrom = copy(abbreviations = file)
        fun indentedCodeClasses(classes: String): HasFrom = copy(indentedCodeClasses = classes)
        fun defaultImageExtension(extension: String): HasFrom = copy(defaultImageExtension = extension)
        fun filter(program: String): HasFrom = copy(filters = (filters ?: emptyList()) + program)
        fun luaFilter(script: String): HasFrom = copy(luaFilters = (luaFilters ?: emptyList()) + script)
        fun shiftHeadingLevelBy(value: Int): HasFrom = copy(shiftHeadingLevelBy = value)
        fun baseHeaderLevel(value: Int): HasFrom = copy(baseHeaderLevel = value)
        fun trackChanges(mode: TrackChanges): HasFrom = copy(trackChanges = mode)
        fun stripComments(enabled: Boolean = true): HasFrom = copy(stripComments = enabled)
        fun referenceLinks(enabled: Boolean = true): HasFrom = copy(referenceLinks = enabled)
        fun referenceLocation(location: ReferenceLocation): HasFrom = copy(referenceLocation = location)
        fun figureCaptionPosition(position: CaptionPosition): HasFrom = copy(figureCaptionPosition = position)
        fun tableCaptionPosition(position: CaptionPosition): HasFrom = copy(tableCaptionPosition = position)
        fun markdownHeadings(style: MarkdownHeadingStyle): HasFrom = copy(markdownHeadings = style)
        fun listTables(enabled: Boolean = true): HasFrom = copy(listTables = enabled)
        fun listings(enabled: Boolean = true): HasFrom = copy(listings = enabled)
        fun incremental(enabled: Boolean = true): HasFrom = copy(incremental = enabled)
        fun slideLevel(value: Int): HasFrom = copy(slideLevel = value)
        fun sectionDivs(enabled: Boolean = true): HasFrom = copy(sectionDivs = enabled)
        fun htmlQTags(enabled: Boolean = true): HasFrom = copy(htmlQTags = enabled)
        fun emailObfuscation(mode: EmailObfuscation): HasFrom = copy(emailObfuscation = mode)
        fun idPrefix(prefix: String): HasFrom = copy(idPrefix = prefix)
        fun titlePrefix(prefix: String): HasFrom = copy(titlePrefix = prefix)
        fun css(url: String): HasFrom = copy(css = url)
        fun citeproc(enabled: Boolean = true): HasFrom = copy(citeproc = enabled)
        fun bibliography(file: String): HasFrom = copy(bibliography = file)
        fun csl(file: String): HasFrom = copy(csl = file)
        fun citationAbbreviations(file: String): HasFrom = copy(citationAbbreviations = file)
        fun natbib(enabled: Boolean = true): HasFrom = copy(natbib = enabled)
        fun biblatex(enabled: Boolean = true): HasFrom = copy(biblatex = enabled)
        fun mathml(enabled: Boolean = true): HasFrom = copy(mathml = enabled)
        fun webtex(url: String? = null): HasFrom = copy(webtex = url)
        fun mathjax(url: String? = null): HasFrom = copy(mathjax = url)
        fun katex(url: String? = null): HasFrom = copy(katex = url)
        fun gladtex(enabled: Boolean = true): HasFrom = copy(gladtex = enabled)
        fun trace(enabled: Boolean = true): HasFrom = copy(trace = enabled)
        fun dumpArgs(enabled: Boolean = true): HasFrom = copy(dumpArgs = enabled)
        fun ignoreArgs(enabled: Boolean = true): HasFrom = copy(ignoreArgs = enabled)
        fun verbose(enabled: Boolean = true): HasFrom = copy(verbose = enabled)
        fun quiet(enabled: Boolean = true): HasFrom = copy(quiet = enabled)
        fun failIfWarnings(enabled: Boolean = true): HasFrom = copy(failIfWarnings = enabled)
        fun log(file: String): HasFrom = copy(log = file)
    }
    
    // ========================================================================
    // NEEDS TO
    // ========================================================================
    
    /**
     * State where input format and input source are set but output format is not yet set.
     */
    @PandocDsl
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
        fun standalone(enabled: Boolean = true): NeedsTo = copy(standalone = enabled)
        fun template(file: String): NeedsTo = copy(template = file)
        fun metadata(key: String, value: String): NeedsTo = copy(metadata = metadata + (key to value))
        fun variable(key: String, value: String): NeedsTo = copy(variables = variables + (key to value))
        fun toc(depth: Int? = null): NeedsTo = copy(toc = true, tocDepth = depth)
        fun wrap(option: WrapOption): NeedsTo = copy(wrap = option)
        fun ascii(enabled: Boolean = true): NeedsTo = copy(ascii = enabled)
        fun numberSections(enabled: Boolean = true): NeedsTo = copy(numberSections = enabled)
        fun numberOffset(vararg offsets: Int): NeedsTo = copy(numberOffset = offsets.toList())
        fun topLevelDivision(division: TopLevelDivision): NeedsTo = copy(topLevelDivision = division)
        fun extractMedia(path: String): NeedsTo = copy(extractMedia = path)
        fun resourcePath(vararg paths: String): NeedsTo = copy(resourcePath = paths.toList())
        fun includeInHeader(file: String): NeedsTo = copy(includeInHeader = (includeInHeader ?: emptyList()) + file)
        fun includeBeforeBody(file: String): NeedsTo = copy(includeBeforeBody = (includeBeforeBody ?: emptyList()) + file)
        fun includeAfterBody(file: String): NeedsTo = copy(includeAfterBody = (includeAfterBody ?: emptyList()) + file)
        fun highlightStyle(style: String): NeedsTo = copy(highlightStyle = style)
        fun syntaxDefinition(file: String): NeedsTo = copy(syntaxDefinition = file)
        fun dpi(value: Int): NeedsTo = copy(dpi = value)
        fun eol(option: EOL): NeedsTo = copy(eol = option)
        fun columns(value: Int): NeedsTo = copy(columns = value)
        fun preserveTabs(enabled: Boolean = true): NeedsTo = copy(preserveTabs = enabled)
        fun tabStop(value: Int): NeedsTo = copy(tabStop = value)
        fun pdfEngine(engine: String): NeedsTo = copy(pdfEngine = engine)
        fun pdfEngineOpt(option: String): NeedsTo = copy(pdfEngineOpt = (pdfEngineOpt ?: emptyList()) + option)
        fun selfContained(enabled: Boolean = true): NeedsTo = copy(selfContained = enabled)
        fun embedResources(enabled: Boolean = true): NeedsTo = copy(embedResources = enabled)
        fun linkImages(enabled: Boolean = true): NeedsTo = copy(linkImages = enabled)
        fun requestHeader(name: String, value: String): NeedsTo = copy(requestHeaders = (requestHeaders ?: emptyMap()) + (name to value))
        fun noCheckCertificate(enabled: Boolean = true): NeedsTo = copy(noCheckCertificate = enabled)
        fun abbreviations(file: String): NeedsTo = copy(abbreviations = file)
        fun indentedCodeClasses(classes: String): NeedsTo = copy(indentedCodeClasses = classes)
        fun defaultImageExtension(extension: String): NeedsTo = copy(defaultImageExtension = extension)
        fun filter(program: String): NeedsTo = copy(filters = (filters ?: emptyList()) + program)
        fun luaFilter(script: String): NeedsTo = copy(luaFilters = (luaFilters ?: emptyList()) + script)
        fun shiftHeadingLevelBy(value: Int): NeedsTo = copy(shiftHeadingLevelBy = value)
        fun baseHeaderLevel(value: Int): NeedsTo = copy(baseHeaderLevel = value)
        fun trackChanges(mode: TrackChanges): NeedsTo = copy(trackChanges = mode)
        fun stripComments(enabled: Boolean = true): NeedsTo = copy(stripComments = enabled)
        fun referenceLinks(enabled: Boolean = true): NeedsTo = copy(referenceLinks = enabled)
        fun referenceLocation(location: ReferenceLocation): NeedsTo = copy(referenceLocation = location)
        fun figureCaptionPosition(position: CaptionPosition): NeedsTo = copy(figureCaptionPosition = position)
        fun tableCaptionPosition(position: CaptionPosition): NeedsTo = copy(tableCaptionPosition = position)
        fun markdownHeadings(style: MarkdownHeadingStyle): NeedsTo = copy(markdownHeadings = style)
        fun listTables(enabled: Boolean = true): NeedsTo = copy(listTables = enabled)
        fun listings(enabled: Boolean = true): NeedsTo = copy(listings = enabled)
        fun incremental(enabled: Boolean = true): NeedsTo = copy(incremental = enabled)
        fun slideLevel(value: Int): NeedsTo = copy(slideLevel = value)
        fun sectionDivs(enabled: Boolean = true): NeedsTo = copy(sectionDivs = enabled)
        fun htmlQTags(enabled: Boolean = true): NeedsTo = copy(htmlQTags = enabled)
        fun emailObfuscation(mode: EmailObfuscation): NeedsTo = copy(emailObfuscation = mode)
        fun idPrefix(prefix: String): NeedsTo = copy(idPrefix = prefix)
        fun titlePrefix(prefix: String): NeedsTo = copy(titlePrefix = prefix)
        fun css(url: String): NeedsTo = copy(css = url)
        fun citeproc(enabled: Boolean = true): NeedsTo = copy(citeproc = enabled)
        fun bibliography(file: String): NeedsTo = copy(bibliography = file)
        fun csl(file: String): NeedsTo = copy(csl = file)
        fun citationAbbreviations(file: String): NeedsTo = copy(citationAbbreviations = file)
        fun natbib(enabled: Boolean = true): NeedsTo = copy(natbib = enabled)
        fun biblatex(enabled: Boolean = true): NeedsTo = copy(biblatex = enabled)
        fun mathml(enabled: Boolean = true): NeedsTo = copy(mathml = enabled)
        fun webtex(url: String? = null): NeedsTo = copy(webtex = url)
        fun mathjax(url: String? = null): NeedsTo = copy(mathjax = url)
        fun katex(url: String? = null): NeedsTo = copy(katex = url)
        fun gladtex(enabled: Boolean = true): NeedsTo = copy(gladtex = enabled)
        fun trace(enabled: Boolean = true): NeedsTo = copy(trace = enabled)
        fun dumpArgs(enabled: Boolean = true): NeedsTo = copy(dumpArgs = enabled)
        fun ignoreArgs(enabled: Boolean = true): NeedsTo = copy(ignoreArgs = enabled)
        fun verbose(enabled: Boolean = true): NeedsTo = copy(verbose = enabled)
        fun quiet(enabled: Boolean = true): NeedsTo = copy(quiet = enabled)
        fun failIfWarnings(enabled: Boolean = true): NeedsTo = copy(failIfWarnings = enabled)
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
    @PandocDsl
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
        fun standalone(enabled: Boolean = true): Complete = copy(standalone = enabled)
        fun template(file: String): Complete = copy(template = file)
        fun metadata(key: String, value: String): Complete = copy(metadata = metadata + (key to value))
        fun variable(key: String, value: String): Complete = copy(variables = variables + (key to value))
        fun toc(depth: Int? = null): Complete = copy(toc = true, tocDepth = depth)
        fun wrap(option: WrapOption): Complete = copy(wrap = option)
        fun ascii(enabled: Boolean = true): Complete = copy(ascii = enabled)
        fun numberSections(enabled: Boolean = true): Complete = copy(numberSections = enabled)
        fun numberOffset(vararg offsets: Int): Complete = copy(numberOffset = offsets.toList())
        fun topLevelDivision(division: TopLevelDivision): Complete = copy(topLevelDivision = division)
        fun extractMedia(path: String): Complete = copy(extractMedia = path)
        fun resourcePath(vararg paths: String): Complete = copy(resourcePath = paths.toList())
        fun includeInHeader(file: String): Complete = copy(includeInHeader = (includeInHeader ?: emptyList()) + file)
        fun includeBeforeBody(file: String): Complete = copy(includeBeforeBody = (includeBeforeBody ?: emptyList()) + file)
        fun includeAfterBody(file: String): Complete = copy(includeAfterBody = (includeAfterBody ?: emptyList()) + file)
        fun highlightStyle(style: String): Complete = copy(highlightStyle = style)
        fun syntaxDefinition(file: String): Complete = copy(syntaxDefinition = file)
        fun dpi(value: Int): Complete = copy(dpi = value)
        fun eol(option: EOL): Complete = copy(eol = option)
        fun columns(value: Int): Complete = copy(columns = value)
        fun preserveTabs(enabled: Boolean = true): Complete = copy(preserveTabs = enabled)
        fun tabStop(value: Int): Complete = copy(tabStop = value)
        fun pdfEngine(engine: String): Complete = copy(pdfEngine = engine)
        fun pdfEngineOpt(option: String): Complete = copy(pdfEngineOpt = (pdfEngineOpt ?: emptyList()) + option)
        fun selfContained(enabled: Boolean = true): Complete = copy(selfContained = enabled)
        fun embedResources(enabled: Boolean = true): Complete = copy(embedResources = enabled)
        fun linkImages(enabled: Boolean = true): Complete = copy(linkImages = enabled)
        fun requestHeader(name: String, value: String): Complete = copy(requestHeaders = (requestHeaders ?: emptyMap()) + (name to value))
        fun noCheckCertificate(enabled: Boolean = true): Complete = copy(noCheckCertificate = enabled)
        fun abbreviations(file: String): Complete = copy(abbreviations = file)
        fun indentedCodeClasses(classes: String): Complete = copy(indentedCodeClasses = classes)
        fun defaultImageExtension(extension: String): Complete = copy(defaultImageExtension = extension)
        fun filter(program: String): Complete = copy(filters = (filters ?: emptyList()) + program)
        fun luaFilter(script: String): Complete = copy(luaFilters = (luaFilters ?: emptyList()) + script)
        fun shiftHeadingLevelBy(value: Int): Complete = copy(shiftHeadingLevelBy = value)
        fun baseHeaderLevel(value: Int): Complete = copy(baseHeaderLevel = value)
        fun trackChanges(mode: TrackChanges): Complete = copy(trackChanges = mode)
        fun stripComments(enabled: Boolean = true): Complete = copy(stripComments = enabled)
        fun referenceLinks(enabled: Boolean = true): Complete = copy(referenceLinks = enabled)
        fun referenceLocation(location: ReferenceLocation): Complete = copy(referenceLocation = location)
        fun figureCaptionPosition(position: CaptionPosition): Complete = copy(figureCaptionPosition = position)
        fun tableCaptionPosition(position: CaptionPosition): Complete = copy(tableCaptionPosition = position)
        fun markdownHeadings(style: MarkdownHeadingStyle): Complete = copy(markdownHeadings = style)
        fun listTables(enabled: Boolean = true): Complete = copy(listTables = enabled)
        fun listings(enabled: Boolean = true): Complete = copy(listings = enabled)
        fun incremental(enabled: Boolean = true): Complete = copy(incremental = enabled)
        fun slideLevel(value: Int): Complete = copy(slideLevel = value)
        fun sectionDivs(enabled: Boolean = true): Complete = copy(sectionDivs = enabled)
        fun htmlQTags(enabled: Boolean = true): Complete = copy(htmlQTags = enabled)
        fun emailObfuscation(mode: EmailObfuscation): Complete = copy(emailObfuscation = mode)
        fun idPrefix(prefix: String): Complete = copy(idPrefix = prefix)
        fun titlePrefix(prefix: String): Complete = copy(titlePrefix = prefix)
        fun css(url: String): Complete = copy(css = url)
        fun citeproc(enabled: Boolean = true): Complete = copy(citeproc = enabled)
        fun bibliography(file: String): Complete = copy(bibliography = file)
        fun csl(file: String): Complete = copy(csl = file)
        fun citationAbbreviations(file: String): Complete = copy(citationAbbreviations = file)
        fun natbib(enabled: Boolean = true): Complete = copy(natbib = enabled)
        fun biblatex(enabled: Boolean = true): Complete = copy(biblatex = enabled)
        fun mathml(enabled: Boolean = true): Complete = copy(mathml = enabled)
        fun webtex(url: String? = null): Complete = copy(webtex = url)
        fun mathjax(url: String? = null): Complete = copy(mathjax = url)
        fun katex(url: String? = null): Complete = copy(katex = url)
        fun gladtex(enabled: Boolean = true): Complete = copy(gladtex = enabled)
        fun trace(enabled: Boolean = true): Complete = copy(trace = enabled)
        fun dumpArgs(enabled: Boolean = true): Complete = copy(dumpArgs = enabled)
        fun ignoreArgs(enabled: Boolean = true): Complete = copy(ignoreArgs = enabled)
        fun verbose(enabled: Boolean = true): Complete = copy(verbose = enabled)
        fun quiet(enabled: Boolean = true): Complete = copy(quiet = enabled)
        fun failIfWarnings(enabled: Boolean = true): Complete = copy(failIfWarnings = enabled)
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
