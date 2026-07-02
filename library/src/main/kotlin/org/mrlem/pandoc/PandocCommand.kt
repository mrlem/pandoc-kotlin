/**
 * Fluent API for building and executing Pandoc commands.
 * 
 * This module provides a type-safe, compile-time validated API for using pandoc
 * from Kotlin applications. The API uses a state-encoded builder pattern to ensure
 * that required fields are set before execution.
 */
package org.mrlem.pandoc

import org.mrlem.pandoc.enums.*
import org.mrlem.pandoc.exceptions.PandocExecutionException
import org.mrlem.pandoc.exceptions.PandocNotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Path

/**
 * DSL marker annotation to prevent accidental mixing of different DSL contexts.
 */
@DslMarker
annotation class PandocDsl

/**
 * Sealed hierarchy for Pandoc command building with compile-time safety.
 * 
 * Each subclass represents a different state of configuration:
 * - [Incomplete]: No required fields set yet
 * - [HasFrom]: Input format set, needs output format
 * - [HasInput]: Input files set, needs output format
 * - [NeedsInput]: Output format set, needs input
 * - [NeedsInputSource]: Input and output formats set, needs input source (files or stdin)
 * - [NeedsTo]: Input method set (stdin), needs output format
 * - [HasFromAndTo]: Complete - has input format, output format, and input files
 * - [HasInputAndTo]: Complete - has input files and output format
 * - [HasStdinAndTo]: Complete - has stdin input and output format
 * 
 * Only complete states (HasFromAndTo, HasInputAndTo, HasStdinAndTo) have
 * terminal operations like [execute], [executeAsync], and [flow].
 */
sealed class PandocCommand {
    
    // ========================================================================
    // INCOMPLETE STATE
    // ========================================================================
    
    /**
     * Initial state with no required fields set.
     * 
     * Use [from], [input], or [fromStdin] to begin configuration.
     */
    @PandocDsl
    class Incomplete internal constructor() : PandocCommand() {
        
        /**
         * Set the input format.
         * 
         * @param format The input format (e.g., MARKDOWN, HTML, DOCX)
         * @return A [HasFrom] state ready to set output format
         */
        fun from(format: InputFormat): HasFrom = HasFrom(format)
        
        /**
         * Set input files.
         * 
         * @param files The input file paths
         * @return A [HasInput] state ready to set output format
         */
        fun input(vararg files: String): HasInput = HasInput(files.toList())
        
        /**
         * Set a single input file.
         * 
         * @param file The input file path
         * @return A [HasInput] state ready to set output format
         */
        fun input(file: String): HasInput = HasInput(listOf(file))
        
        /**
         * Set a single input file using Path.
         * 
         * @param file The input file path
         * @return A [HasInput] state ready to set output format
         */
        fun input(file: Path): HasInput = HasInput(listOf(file.toString()))
        
        /**
         * Set a single input file using File.
         * 
         * @param file The input file
         * @return A [HasInput] state ready to set output format
         */
        fun input(file: File): HasInput = HasInput(listOf(file.absolutePath))
        
        /**
         * Set multiple input files using File.
         * 
         * @param files The input files
         * @return A [HasInput] state ready to set output format
         */
        fun input(vararg files: File): HasInput = HasInput(files.map { it.absolutePath })
        
        /**
         * Set multiple input files using Path.
         * 
         * @param files The input file paths
         * @return A [HasInput] state ready to set output format
         */
        fun input(vararg files: Path): HasInput = HasInput(files.map { it.toString() })
        
        /**
         * Specify that input will come from stdin.
         * 
         * @return A [NeedsTo] state ready to set output format
         */
        fun fromStdin(): NeedsTo = NeedsTo(inputFromStdin = true)
        
        /**
         * Set the output format first.
         * 
         * This is useful when you want to specify output format before input.
         * 
         * @param format The output format
         * @return A [NeedsInput] state ready to set input
         */
        fun to(format: OutputFormat): NeedsInput = NeedsInput(format)
    }
    
    // ========================================================================
    // HAS INPUT FORMAT
    // ========================================================================
    
    /**
     * State where input format is set but output format is not yet set.
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
        val output: String? = null
    ) : PandocCommand() {
        
        /**
         * Set the output format.
         * 
         * @param format The output format
         * @return A [NeedsInputSource] state - needs input source (files or stdin)
         */
        fun to(format: OutputFormat): NeedsInputSource = NeedsInputSource(
            from = from,
            to = format,
            standalone = standalone,
            template = template,
            metadata = metadata,
            variables = variables,
            toc = toc,
            tocDepth = tocDepth,
            output = output
        )
        
        // Configuration options that can be set with from
        
        /**
         * Create a standalone document with header and footer.
         * 
         * @param enabled Whether to create a standalone document (default: true)
         * @return A new [HasFrom] with updated configuration
         */
        fun standalone(enabled: Boolean = true): HasFrom = copy(standalone = enabled)
        
        /**
         * Use a custom template file.
         * 
         * @param file The template file path
         * @return A new [HasFrom] with updated configuration
         */
        fun template(file: String): HasFrom = copy(template = file)
        
        /**
         * Set metadata for the document.
         * 
         * @param key The metadata key
         * @param value The metadata value
         * @return A new [HasFrom] with updated configuration
         */
        fun metadata(key: String, value: String): HasFrom = copy(
            metadata = metadata + (key to value)
        )
        
        /**
         * Set variables for the template.
         * 
         * @param key The variable key
         * @param value The variable value
         * @return A new [HasFrom] with updated configuration
         */
        fun variable(key: String, value: String): HasFrom = copy(
            variables = variables + (key to value)
        )
        
        /**
         * Enable table of contents.
         * 
         * @param depth The depth of headings to include (default: null = auto)
         * @return A new [HasFrom] with updated configuration
         */
        fun toc(depth: Int? = null): HasFrom = copy(toc = true, tocDepth = depth)
        
        /**
         * Set the output file.
         * 
         * @param file The output file path
         * @return A new [HasFrom] with updated configuration
         */
        fun output(file: String): HasFrom = copy(output = file)
    }
    
    // ========================================================================
    // HAS INPUT FILES
    // ========================================================================
    
    /**
     * State where input files are set but output format is not yet set.
     */
    @PandocDsl
    
    data class HasInput internal constructor(
        val files: List<String>,
        val standalone: Boolean? = null,
        val template: String? = null,
        val metadata: Map<String, String> = emptyMap(),
        val variables: Map<String, String> = emptyMap(),
        val toc: Boolean? = null,
        val tocDepth: Int? = null,
        val output: String? = null
    ) : PandocCommand() {
        
        /**
         * Set the output format.
         * 
         * @param format The output format
         * @return A [HasInputAndTo] state ready for execution or further configuration
         */
        fun to(format: OutputFormat): HasInputAndTo = HasInputAndTo(
            files = files,
            to = format,
            standalone = standalone,
            template = template,
            metadata = metadata,
            variables = variables,
            toc = toc,
            tocDepth = tocDepth,
            output = output
        )
        
        // Configuration options that can be set with input files
        
        fun standalone(enabled: Boolean = true): HasInput = copy(standalone = enabled)
        fun template(file: String): HasInput = copy(template = file)
        fun metadata(key: String, value: String): HasInput = copy(
            metadata = metadata + (key to value)
        )
        fun variable(key: String, value: String): HasInput = copy(
            variables = variables + (key to value)
        )
        fun toc(depth: Int? = null): HasInput = copy(toc = true, tocDepth = depth)
        fun output(file: String): HasInput = copy(output = file)
    }
    
    // ========================================================================
    // NEEDS INPUT
    // ========================================================================
    
    /**
     * State where output format is set but input is not yet set.
     */
    @PandocDsl
    
    data class NeedsInput internal constructor(val to: OutputFormat) : PandocCommand() {
        
        /**
         * Set the input format.
         * 
         * @param format The input format
         * @return A [HasFromAndTo] state ready for execution or further configuration
         */
        fun from(format: InputFormat): HasFromAndTo = HasFromAndTo(
            from = format,
            to = to
        )
        
        /**
         * Set input files.
         * 
         * @param files The input file paths
         * @return A [HasInputAndTo] state ready for execution or further configuration
         */
        fun input(vararg files: String): HasInputAndTo = HasInputAndTo(
            files = files.toList(),
            to = to
        )
        
        /**
         * Set a single input file.
         * 
         * @param file The input file path
         * @return A [HasInputAndTo] state ready for execution or further configuration
         */
        fun input(file: String): HasInputAndTo = HasInputAndTo(
            files = listOf(file),
            to = to
        )
        
        /**
         * Set a single input file using Path.
         * 
         * @param file The input file path
         * @return A [HasInputAndTo] state ready for execution or further configuration
         */
        fun input(file: Path): HasInputAndTo = HasInputAndTo(
            files = listOf(file.toString()),
            to = to
        )
        
        /**
         * Set a single input file using File.
         * 
         * @param file The input file
         * @return A [HasInputAndTo] state ready for execution or further configuration
         */
        fun input(file: File): HasInputAndTo = HasInputAndTo(
            files = listOf(file.absolutePath),
            to = to
        )
        
        /**
         * Set multiple input files using File.
         * 
         * @param files The input files
         * @return A [HasInputAndTo] state ready for execution or further configuration
         */
        fun input(vararg files: File): HasInputAndTo = HasInputAndTo(
            files = files.map { it.absolutePath },
            to = to
        )
        
        /**
         * Set multiple input files using Path.
         * 
         * @param files The input file paths
         * @return A [HasInputAndTo] state ready for execution or further configuration
         */
        fun input(vararg files: Path): HasInputAndTo = HasInputAndTo(
            files = files.map { it.toString() },
            to = to
        )
    }
    
    // ========================================================================
    // NEEDS TO (STDIN)
    // ========================================================================
    
    /**
     * State where input is from stdin but output format is not yet set.
     */
    @PandocDsl
    
    data class NeedsTo internal constructor(val inputFromStdin: Boolean = true) : PandocCommand() {
        
        /**
         * Set the output format.
         * 
         * @param format The output format
         * @return A [HasStdinAndTo] state ready for execution or further configuration
         */
        fun to(format: OutputFormat): HasStdinAndTo = HasStdinAndTo(
            to = format
        )
    }
    
    // ========================================================================
    // NEEDS INPUT SOURCE
    // ========================================================================
    
    /**
     * State where input format and output format are set but input source is not yet set.
     * 
     * This state requires either input files or stdin to be specified before execution.
     * Only complete states ([HasFromAndTo], [HasInputAndTo], [HasStdinAndTo]) have
     * terminal operations like [execute].
     */
    @PandocDsl
    
    data class NeedsInputSource internal constructor(
        val from: InputFormat,
        val to: OutputFormat,
        val standalone: Boolean? = null,
        val template: String? = null,
        val metadata: Map<String, String> = emptyMap(),
        val variables: Map<String, String> = emptyMap(),
        val toc: Boolean? = null,
        val tocDepth: Int? = null,
        val output: String? = null
    ) : PandocCommand() {
        
        /**
         * Set input files.
         * 
         * @param files The input file paths
         * @return A [HasFromAndTo] state ready for execution or further configuration
         */
        fun input(vararg files: String): HasFromAndTo = HasFromAndTo(
            from = from,
            to = to,
            files = files.toList(),
            standalone = standalone,
            template = template,
            metadata = metadata,
            variables = variables,
            toc = toc,
            tocDepth = tocDepth,
            output = output
        )
        
        /**
         * Set a single input file.
         * 
         * @param file The input file path
         * @return A [HasFromAndTo] state ready for execution or further configuration
         */
        fun input(file: String): HasFromAndTo = input(*arrayOf(file))
        
        /**
         * Set a single input file using Path.
         * 
         * @param file The input file path
         * @return A [HasFromAndTo] state ready for execution or further configuration
         */
        fun input(file: Path): HasFromAndTo = input(file.toString())
        
        /**
         * Set a single input file using File.
         * 
         * @param file The input file
         * @return A [HasFromAndTo] state ready for execution or further configuration
         */
        fun input(file: File): HasFromAndTo = input(file.absolutePath)
        
        /**
         * Set multiple input files using File.
         * 
         * @param files The input files
         * @return A [HasFromAndTo] state ready for execution or further configuration
         */
        fun input(vararg files: File): HasFromAndTo = input(*files.map { it.absolutePath }.toTypedArray())
        
        /**
         * Set multiple input files using Path.
         * 
         * @param files The input file paths
         * @return A [HasFromAndTo] state ready for execution or further configuration
         */
        fun input(vararg files: Path): HasFromAndTo = input(*files.map { it.toString() }.toTypedArray())
        
        /**
         * Specify that input will come from stdin.
         * 
         * @return A [HasStdinAndTo] state ready for execution or further configuration
         */
        fun fromStdin(): HasStdinAndTo = HasStdinAndTo(
            from = from,
            to = to,
            standalone = standalone,
            template = template,
            metadata = metadata,
            variables = variables,
            toc = toc,
            tocDepth = tocDepth,
            output = output
        )
    }
    
    // ========================================================================
    // COMPLETE STATES
    // ========================================================================
    
    /**
     * Complete state with input format, output format, input files, and optional configuration.
     * 
     * This state has all terminal operations ([execute], [executeAsync], [flow]).
     */
    @PandocDsl
    
    data class HasFromAndTo internal constructor(
        val from: InputFormat,
        val to: OutputFormat,
        val files: List<String> = emptyList(),
        val standalone: Boolean? = null,
        val template: String? = null,
        val metadata: Map<String, String> = emptyMap(),
        val variables: Map<String, String> = emptyMap(),
        val toc: Boolean? = null,
        val tocDepth: Int? = null,
        val output: String? = null,
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
        
        // Configuration setters - each returns a new instance
        
        fun input(vararg files: String): HasFromAndTo = copy(files = files.toList())
        fun input(file: String): HasFromAndTo = copy(files = listOf(file))
        fun input(file: Path): HasFromAndTo = copy(files = listOf(file.toString()))
        fun input(file: File): HasFromAndTo = copy(files = listOf(file.absolutePath))
        fun input(vararg files: File): HasFromAndTo = copy(files = files.map { it.absolutePath })
        fun input(vararg files: Path): HasFromAndTo = copy(files = files.map { it.toString() })
        fun standalone(enabled: Boolean = true): HasFromAndTo = copy(standalone = enabled)
        fun template(file: String): HasFromAndTo = copy(template = file)
        fun output(file: String): HasFromAndTo = copy(output = file)
        fun metadata(key: String, value: String): HasFromAndTo = copy(
            metadata = metadata + (key to value)
        )
        fun variable(key: String, value: String): HasFromAndTo = copy(
            variables = variables + (key to value)
        )
        fun toc(depth: Int? = null): HasFromAndTo = copy(toc = true, tocDepth = depth)
        fun wrap(option: WrapOption): HasFromAndTo = copy(wrap = option)
        fun ascii(enabled: Boolean = true): HasFromAndTo = copy(ascii = enabled)
        fun numberSections(enabled: Boolean = true): HasFromAndTo = copy(numberSections = enabled)
        fun numberOffset(vararg offsets: Int): HasFromAndTo = copy(numberOffset = offsets.toList())
        fun topLevelDivision(division: TopLevelDivision): HasFromAndTo = copy(topLevelDivision = division)
        fun extractMedia(path: String): HasFromAndTo = copy(extractMedia = path)
        fun resourcePath(vararg paths: String): HasFromAndTo = copy(resourcePath = paths.toList())
        fun includeInHeader(file: String): HasFromAndTo = copy(
            includeInHeader = (includeInHeader ?: emptyList()) + file
        )
        fun includeBeforeBody(file: String): HasFromAndTo = copy(
            includeBeforeBody = (includeBeforeBody ?: emptyList()) + file
        )
        fun includeAfterBody(file: String): HasFromAndTo = copy(
            includeAfterBody = (includeAfterBody ?: emptyList()) + file
        )
        fun highlightStyle(style: String): HasFromAndTo = copy(highlightStyle = style)
        fun syntaxDefinition(file: String): HasFromAndTo = copy(syntaxDefinition = file)
        fun dpi(value: Int): HasFromAndTo = copy(dpi = value)
        fun eol(option: EOL): HasFromAndTo = copy(eol = option)
        fun columns(value: Int): HasFromAndTo = copy(columns = value)
        fun preserveTabs(enabled: Boolean = true): HasFromAndTo = copy(preserveTabs = enabled)
        fun tabStop(value: Int): HasFromAndTo = copy(tabStop = value)
        fun pdfEngine(engine: String): HasFromAndTo = copy(pdfEngine = engine)
        fun pdfEngineOpt(option: String): HasFromAndTo = copy(
            pdfEngineOpt = (pdfEngineOpt ?: emptyList()) + option
        )
        fun selfContained(enabled: Boolean = true): HasFromAndTo = copy(selfContained = enabled)
        fun embedResources(enabled: Boolean = true): HasFromAndTo = copy(embedResources = enabled)
        fun linkImages(enabled: Boolean = true): HasFromAndTo = copy(linkImages = enabled)
        fun requestHeader(name: String, value: String): HasFromAndTo = copy(
            requestHeaders = (requestHeaders ?: emptyMap()) + (name to value)
        )
        fun noCheckCertificate(enabled: Boolean = true): HasFromAndTo = copy(noCheckCertificate = enabled)
        fun abbreviations(file: String): HasFromAndTo = copy(abbreviations = file)
        fun indentedCodeClasses(classes: String): HasFromAndTo = copy(indentedCodeClasses = classes)
        fun defaultImageExtension(extension: String): HasFromAndTo = copy(defaultImageExtension = extension)
        fun filter(program: String): HasFromAndTo = copy(
            filters = (filters ?: emptyList()) + program
        )
        fun luaFilter(script: String): HasFromAndTo = copy(
            luaFilters = (luaFilters ?: emptyList()) + script
        )
        fun shiftHeadingLevelBy(value: Int): HasFromAndTo = copy(shiftHeadingLevelBy = value)
        fun baseHeaderLevel(value: Int): HasFromAndTo = copy(baseHeaderLevel = value)
        fun trackChanges(mode: TrackChanges): HasFromAndTo = copy(trackChanges = mode)
        fun stripComments(enabled: Boolean = true): HasFromAndTo = copy(stripComments = enabled)
        fun referenceLinks(enabled: Boolean = true): HasFromAndTo = copy(referenceLinks = enabled)
        fun referenceLocation(location: ReferenceLocation): HasFromAndTo = copy(referenceLocation = location)
        fun figureCaptionPosition(position: CaptionPosition): HasFromAndTo = copy(figureCaptionPosition = position)
        fun tableCaptionPosition(position: CaptionPosition): HasFromAndTo = copy(tableCaptionPosition = position)
        fun markdownHeadings(style: MarkdownHeadingStyle): HasFromAndTo = copy(markdownHeadings = style)
        fun listTables(enabled: Boolean = true): HasFromAndTo = copy(listTables = enabled)
        fun listings(enabled: Boolean = true): HasFromAndTo = copy(listings = enabled)
        fun incremental(enabled: Boolean = true): HasFromAndTo = copy(incremental = enabled)
        fun slideLevel(value: Int): HasFromAndTo = copy(slideLevel = value)
        fun sectionDivs(enabled: Boolean = true): HasFromAndTo = copy(sectionDivs = enabled)
        fun htmlQTags(enabled: Boolean = true): HasFromAndTo = copy(htmlQTags = enabled)
        fun emailObfuscation(mode: EmailObfuscation): HasFromAndTo = copy(emailObfuscation = mode)
        fun idPrefix(prefix: String): HasFromAndTo = copy(idPrefix = prefix)
        fun titlePrefix(prefix: String): HasFromAndTo = copy(titlePrefix = prefix)
        fun css(url: String): HasFromAndTo = copy(css = url)
        fun citeproc(enabled: Boolean = true): HasFromAndTo = copy(citeproc = enabled)
        fun bibliography(file: String): HasFromAndTo = copy(bibliography = file)
        fun csl(file: String): HasFromAndTo = copy(csl = file)
        fun citationAbbreviations(file: String): HasFromAndTo = copy(citationAbbreviations = file)
        fun natbib(enabled: Boolean = true): HasFromAndTo = copy(natbib = enabled)
        fun biblatex(enabled: Boolean = true): HasFromAndTo = copy(biblatex = enabled)
        fun mathml(enabled: Boolean = true): HasFromAndTo = copy(mathml = enabled)
        fun webtex(url: String? = null): HasFromAndTo = copy(webtex = url)
        fun mathjax(url: String? = null): HasFromAndTo = copy(mathjax = url)
        fun katex(url: String? = null): HasFromAndTo = copy(katex = url)
        fun gladtex(enabled: Boolean = true): HasFromAndTo = copy(gladtex = enabled)
        fun trace(enabled: Boolean = true): HasFromAndTo = copy(trace = enabled)
        fun dumpArgs(enabled: Boolean = true): HasFromAndTo = copy(dumpArgs = enabled)
        fun ignoreArgs(enabled: Boolean = true): HasFromAndTo = copy(ignoreArgs = enabled)
        fun verbose(enabled: Boolean = true): HasFromAndTo = copy(verbose = enabled)
        fun quiet(enabled: Boolean = true): HasFromAndTo = copy(quiet = enabled)
        fun failIfWarnings(enabled: Boolean = true): HasFromAndTo = copy(failIfWarnings = enabled)
        fun log(file: String): HasFromAndTo = copy(log = file)
        
        // Terminal operations
        
        /**
         * Execute the pandoc command synchronously.
         * 
         * This reads from the specified input files and returns the output as a string.
         * For stdin input, use [execute(String)] on [HasStdinAndTo].
         * 
         * @return The output from pandoc
         * @throws PandocNotFoundException if pandoc is not installed
         * @throws PandocExecutionException if pandoc execution fails
         */
        fun execute(): String = executeSync()
        
        /**
         * Execute the pandoc command asynchronously.
         * 
         * @return The output from pandoc
         * @throws PandocNotFoundException if pandoc is not installed
         * @throws PandocExecutionException if pandoc execution fails
         */
        suspend fun executeAsync(): String = withContext(Dispatchers.IO) {
            executeSync()
        }
        
        /**
         * Execute the pandoc command and return a Flow.
         * 
         * @return A Flow that emits the output from pandoc
         */
        fun flow(): Flow<String> = flow {
            emit(executeSync())
        }
        
        /**
         * Execute the pandoc command and write output to a file.
         * 
         * @param file The output file path
         * @throws PandocNotFoundException if pandoc is not installed
         * @throws PandocExecutionException if pandoc execution fails
         */
        fun executeToFile(file: String) = executeToFileSync(file)
        
        /**
         * Execute the pandoc command asynchronously and write output to a file.
         * 
         * @param file The output file path
         * @throws PandocNotFoundException if pandoc is not installed
         * @throws PandocExecutionException if pandoc execution fails
         */
        suspend fun executeToFileAsync(file: String) = withContext(Dispatchers.IO) {
            executeToFileSync(file)
        }
        
        private fun executeSync(): String {
            val command = buildCommandLine()
            return runPandoc(command)
        }
        
        private fun executeToFileSync(file: String): Unit {
            val command = buildCommandLine(file)
            runPandoc(command)
        }
        
        private fun buildCommandLine(outputFile: String? = null): List<String> {
            val args = mutableListOf<String>("pandoc")
            
            // Input/Output
            args.addAll(listOf("-f", from.value))
            args.addAll(listOf("-t", to.value))
            outputFile?.let { args.addAll(listOf("-o", it)) }
            output?.let { args.addAll(listOf("-o", it)) }
            
            // General options
            standalone?.let { if (it) args.add("--standalone") }
            template?.let { args.addAll(listOf("--template", it)) }
            wrap?.let { args.addAll(listOf("--wrap", it.value)) }
            ascii?.let { if (it) args.add("--ascii") }
            
            // Metadata
            metadata.forEach { (key, value) ->
                args.addAll(listOf("-M", "$key=$value"))
            }
            variables.forEach { (key, value) ->
                args.addAll(listOf("-V", "$key=$value"))
            }
            
            // Table of contents
            toc?.let { if (it) args.add("--toc") }
            tocDepth?.let { args.addAll(listOf("--toc-depth", it.toString())) }
            
            // Files
            args.addAll(files)
            
            return args
        }
        
        private fun runPandoc(command: List<String>): String {
            try {
                // Check if pandoc is available
                val process = ProcessBuilder(command)
                    .redirectErrorStream(false)
                    .start()
                
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
    }
    
    // ========================================================================
    // HAS INPUT AND TO
    // ========================================================================
    
    /**
     * Complete state with input files, output format, and optional configuration.
     * 
     * This state has all terminal operations ([execute], [executeAsync], [flow]).
     */
    @PandocDsl
    
    data class HasInputAndTo internal constructor(
        val files: List<String>,
        val to: OutputFormat,
        val standalone: Boolean? = null,
        val template: String? = null,
        val metadata: Map<String, String> = emptyMap(),
        val variables: Map<String, String> = emptyMap(),
        val toc: Boolean? = null,
        val tocDepth: Int? = null,
        val output: String? = null
    ) : PandocCommand() {
        
        // Configuration setters
        fun standalone(enabled: Boolean = true): HasInputAndTo = copy(standalone = enabled)
        fun template(file: String): HasInputAndTo = copy(template = file)
        fun output(file: String): HasInputAndTo = copy(output = file)
        fun metadata(key: String, value: String): HasInputAndTo = copy(
            metadata = metadata + (key to value)
        )
        fun variable(key: String, value: String): HasInputAndTo = copy(
            variables = variables + (key to value)
        )
        fun toc(depth: Int? = null): HasInputAndTo = copy(toc = true, tocDepth = depth)
        
        // Terminal operations
        fun execute(): String = executeSync()
        suspend fun executeAsync(): String = withContext(Dispatchers.IO) { executeSync() }
        fun flow(): Flow<String> = flow { emit(executeSync()) }
        fun executeToFile(file: String) = executeToFileSync(file)
        suspend fun executeToFileAsync(file: String) = withContext(Dispatchers.IO) { executeToFileSync(file) }
        
        private fun executeSync(): String {
            val command = buildCommandLine()
            return runPandoc(command)
        }
        
        private fun executeToFileSync(file: String) {
            val command = buildCommandLine(file)
            runPandoc(command)
        }
        
        private fun buildCommandLine(outputFile: String? = null): List<String> {
            val args = mutableListOf<String>("pandoc")
            
            args.addAll(listOf("-t", to.value))
            outputFile?.let { args.addAll(listOf("-o", it)) }
            output?.let { args.addAll(listOf("-o", it)) }
            
            standalone?.let { if (it) args.add("--standalone") }
            template?.let { args.addAll(listOf("--template", it)) }
            
            metadata.forEach { (key, value) ->
                args.addAll(listOf("-M", "$key=$value"))
            }
            variables.forEach { (key, value) ->
                args.addAll(listOf("-V", "$key=$value"))
            }
            
            toc?.let { if (it) args.add("--toc") }
            tocDepth?.let { args.addAll(listOf("--toc-depth", it.toString())) }
            
            args.addAll(files)
            
            return args
        }
        
        private fun runPandoc(command: List<String>): String {
            try {
                val process = ProcessBuilder(command)
                    .redirectErrorStream(false)
                    .start()
                
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
    }
    
    // ========================================================================
    // HAS STDIN AND TO
    // ========================================================================
    
    /**
     * Complete state with stdin input, output format, and optional configuration.
     * 
     * This state has terminal operations that accept input content as a string.
     */
    @PandocDsl
    
    data class HasStdinAndTo internal constructor(
        val to: OutputFormat,
        val standalone: Boolean? = null,
        val template: String? = null,
        val metadata: Map<String, String> = emptyMap(),
        val variables: Map<String, String> = emptyMap(),
        val toc: Boolean? = null,
        val tocDepth: Int? = null,
        val output: String? = null,
        val from: InputFormat? = null
    ) : PandocCommand() {
        
        /**
         * Set the input format for stdin content.
         * 
         * @param format The input format
         * @return A [HasFromAndTo] state
         */
        fun from(format: InputFormat): HasFromAndTo = HasFromAndTo(
            from = format,
            to = to,
            standalone = standalone,
            template = template,
            metadata = metadata,
            variables = variables,
            toc = toc,
            tocDepth = tocDepth,
            output = output
        )
        
        // Configuration setters
        fun standalone(enabled: Boolean = true): HasStdinAndTo = copy(standalone = enabled)
        fun template(file: String): HasStdinAndTo = copy(template = file)
        fun output(file: String): HasStdinAndTo = copy(output = file)
        fun metadata(key: String, value: String): HasStdinAndTo = copy(
            metadata = metadata + (key to value)
        )
        fun variable(key: String, value: String): HasStdinAndTo = copy(
            variables = variables + (key to value)
        )
        fun toc(depth: Int? = null): HasStdinAndTo = copy(toc = true, tocDepth = depth)
        
        // Terminal operations for stdin
        
        /**
         * Execute the pandoc command with the given content from stdin.
         * 
         * @param content The input content to convert
         * @return The output from pandoc
         * @throws PandocNotFoundException if pandoc is not installed
         * @throws PandocExecutionException if pandoc execution fails
         */
        fun execute(content: String): String = executeSync(content)
        
        /**
         * Execute the pandoc command asynchronously with the given content from stdin.
         * 
         * @param content The input content to convert
         * @return The output from pandoc
         * @throws PandocNotFoundException if pandoc is not installed
         * @throws PandocExecutionException if pandoc execution fails
         */
        suspend fun executeAsync(content: String): String = withContext(Dispatchers.IO) {
            executeSync(content)
        }
        
        /**
         * Execute the pandoc command with the given content and return a Flow.
         * 
         * @param content The input content to convert
         * @return A Flow that emits the output from pandoc
         */
        fun flow(content: String): Flow<String> = flow {
            emit(executeSync(content))
        }
        
        private fun executeSync(content: String): String {
            val command = buildCommandLine()
            return runPandoc(command, content)
        }
        
        private fun buildCommandLine(): List<String> {
            val args = mutableListOf<String>("pandoc")
            
            from?.let { args.addAll(listOf("-f", it.value)) }
            args.addAll(listOf("-t", to.value))
            output?.let { args.addAll(listOf("-o", it)) }
            
            standalone?.let { if (it) args.add("--standalone") }
            template?.let { args.addAll(listOf("--template", it)) }
            
            metadata.forEach { (key, value) ->
                args.addAll(listOf("-M", "$key=$value"))
            }
            variables.forEach { (key, value) ->
                args.addAll(listOf("-V", "$key=$value"))
            }
            
            toc?.let { if (it) args.add("--toc") }
            tocDepth?.let { args.addAll(listOf("--toc-depth", it.toString())) }
            
            // Read from stdin
            args.add("-")
            
            return args
        }
        
        private fun runPandoc(command: List<String>, content: String): String {
            try {
                val process = ProcessBuilder(command)
                    .redirectErrorStream(false)
                    .start()
                
                // Write content to stdin
                process.outputStream.bufferedWriter().use { writer ->
                    writer.write(content)
                    writer.flush()
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
    }
}

/**
 * Main entry point for the Pandoc fluent API.
 * 
 * Example usage:
 * ```kotlin
 * // Simple conversion
 * val html = Pandoc.convert()
 *     .from(InputFormat.MARKDOWN)
 *     .to(OutputFormat.HTML)
 *     .input("readme.md")
 *     .standalone()
 *     .execute()
 * 
 * // Async conversion
 * suspend fun convertAsync() {
 *     val result = Pandoc.convert()
 *         .from(InputFormat.MARKDOWN)
 *         .to(OutputFormat.PDF)
 *         .input("input.md")
 *         .output("output.pdf")
 *         .executeAsync()
 * }
 * 
 * // String conversion via stdin
 * val html2 = Pandoc.convert()
 *     .fromStdin()
 *     .to(OutputFormat.HTML)
 *     .execute("# Hello World")
 * 
 * // Using Flow
 * Pandoc.convert()
 *     .from(InputFormat.MARKDOWN)
 *     .to(OutputFormat.HTML)
 *     .input("input.md")
 *     .flow()
 *     .collect { html -> println(html) }
 * ```
 */
object Pandoc {
    
    /**
     * Start building a pandoc conversion command.
     * 
     * @return An [Incomplete] state ready for configuration
     */
    fun convert(): PandocCommand.Incomplete = PandocCommand.Incomplete()
    
    /**
     * Convenience function for simple string-to-string conversion.
     * 
     * This is a shorthand for:
     * ```kotlin
     * Pandoc.convert()
     *     .fromStdin()
     *     .to(to)
     *     .from(from)
     *     .execute(content)
     * ```
     * 
     * @param from The input format
     * @param to The output format
     * @param content The content to convert
     * @param block Optional configuration block
     * @return The converted content
     */
    suspend fun convertString(
        from: InputFormat,
        to: OutputFormat,
        content: String,
        block: PandocCommand.HasStdinAndTo.() -> PandocCommand.HasStdinAndTo = { this }
    ): String {
        return block(PandocCommand.HasStdinAndTo(to, from = from)).executeAsync(content)
    }
}
