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
    private val from: InputFormat,
    private val inputSource: InputSource,
    private val to: OutputFormat,
    private val standalone: Boolean? = null,
    private val template: String? = null,
    private val metadata: Map<String, String> = emptyMap(),
    private val variables: Map<String, String> = emptyMap(),
    private val toc: Boolean? = null,
    private val tocDepth: Int? = null,
    private val wrapMode: WrapMode? = null,
    private val ascii: Boolean? = null,
    private val numberSections: Boolean? = null,
    private val numberOffset: List<Int>? = null,
    private val topLevelDivision: TopLevelDivision? = null,
    private val extractMedia: String? = null,
    private val resourcePath: List<String>? = null,
    private val includeInHeader: List<String>? = null,
    private val includeBeforeBody: List<String>? = null,
    private val includeAfterBody: List<String>? = null,
    private val highlightStyle: String? = null,
    private val syntaxDefinition: String? = null,
    private val dpi: Int? = null,
    private val eol: EOL? = null,
    private val columns: Int? = null,
    private val preserveTabs: Boolean? = null,
    private val tabStop: Int? = null,
    private val pdfEngine: String? = null,
    private val pdfEngineOpt: List<String>? = null,
    private val selfContained: Boolean? = null,
    private val embedResources: Boolean? = null,
    private val linkImages: Boolean? = null,
    private val requestHeaders: Map<String, String>? = null,
    private val noCheckCertificate: Boolean? = null,
    private val abbreviations: String? = null,
    private val indentedCodeClasses: String? = null,
    private val defaultImageExtension: String? = null,
    private val filters: List<String>? = null,
    private val luaFilters: List<String>? = null,
    private val shiftHeadingLevelBy: Int? = null,
    private val baseHeaderLevel: Int? = null,
    private val trackChanges: TrackChanges? = null,
    private val stripComments: Boolean? = null,
    private val referenceLinks: Boolean? = null,
    private val referenceLocation: ReferenceLocation? = null,
    private val figureCaptionPosition: CaptionPosition? = null,
    private val tableCaptionPosition: CaptionPosition? = null,
    private val markdownHeadings: MarkdownHeadingStyle? = null,
    private val listTables: Boolean? = null,
    private val listings: Boolean? = null,
    private val incremental: Boolean? = null,
    private val slideLevel: Int? = null,
    private val sectionDivs: Boolean? = null,
    private val htmlQTags: Boolean? = null,
    private val emailObfuscation: EmailObfuscation? = null,
    private val idPrefix: String? = null,
    private val titlePrefix: String? = null,
    private val css: String? = null,
    private val citeproc: Boolean? = null,
    private val bibliography: String? = null,
    private val csl: String? = null,
    private val citationAbbreviations: String? = null,
    private val natbib: Boolean? = null,
    private val biblatex: Boolean? = null,
    private val mathml: Boolean? = null,
    private val webtex: String? = null,
    private val mathjax: String? = null,
    private val katex: String? = null,
    private val gladtex: Boolean? = null,
    private val trace: Boolean? = null,
    private val dumpArgs: Boolean? = null,
    private val ignoreArgs: Boolean? = null,
    private val verbose: Boolean? = null,
    private val quiet: Boolean? = null,
    private val failIfWarnings: Boolean? = null,
    private val log: String? = null,
) : CommandState {
    suspend fun outputString(): String =
        withContext(Dispatchers.IO) {
            val command = buildCommandLine()
            runPandoc(command, inputSource)
        }

    suspend fun outputFile(file: String) =
        withContext(Dispatchers.IO) {
            val command = buildCommandLine(file)
            runPandoc(command, inputSource)
        }

    suspend fun outputWriter(writer: Writer) =
        withContext(Dispatchers.IO) {
            val command = buildCommandLine()
            runPandoc(command, inputSource, writer)
        }

    private fun buildCommandLine(outputFile: String? = null): List<String> {
        val args = mutableListOf("pandoc")

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
        resourcePath?.let { args.addAll(it.flatMap { arg -> listOf("--resource-path", arg) }) }
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

    private fun createPandocProcess(
        command: List<String>,
        inputSource: InputSource,
    ): Process {
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

        return process
    }

    private fun runPandoc(
        command: List<String>,
        inputSource: InputSource,
    ): String {
        try {
            val process = createPandocProcess(command, inputSource)

            val stdout = process.inputStream.bufferedReader().readText()
            val stderr = process.errorStream.bufferedReader().readText()
            val exitCode = process.waitFor()

            if (exitCode != 0) {
                throw PandocExecutionException(
                    message = "Pandoc execution failed with exit code $exitCode",
                    exitCode = exitCode,
                    command = command,
                    stdout = stdout,
                    stderr = stderr,
                )
            }

            return stdout
        } catch (e: Exception) {
            if (e is PandocExecutionException) throw e
            throw PandocNotFoundException("Failed to execute pandoc", e)
        }
    }

    private fun runPandoc(
        command: List<String>,
        inputSource: InputSource,
        writer: Writer,
    ) {
        try {
            val process = createPandocProcess(command, inputSource)

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
                    stderr = stderr,
                )
            }
        } catch (e: Exception) {
            if (e is PandocExecutionException) throw e
            throw PandocNotFoundException("Failed to execute pandoc", e)
        }
    }
}