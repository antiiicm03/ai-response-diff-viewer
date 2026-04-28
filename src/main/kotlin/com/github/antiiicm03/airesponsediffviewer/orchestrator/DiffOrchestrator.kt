package com.github.antiiicm03.airesponsediffviewer.orchestrator

import com.github.antiiicm03.airesponsediffviewer.model.AiResponse
import com.github.antiiicm03.airesponsediffviewer.model.DiffSession
import com.github.antiiicm03.airesponsediffviewer.service.CodeBlockParser
import com.github.antiiicm03.airesponsediffviewer.service.ContextResolver
import com.github.antiiicm03.airesponsediffviewer.service.DiffViewerError
import com.github.antiiicm03.airesponsediffviewer.service.DiffViewerManager
import com.github.antiiicm03.airesponsediffviewer.service.ErrorHandler
import com.github.antiiicm03.airesponsediffviewer.service.FileApplyService
import com.github.antiiicm03.airesponsediffviewer.service.impl.EditorContextResolver
import com.github.antiiicm03.airesponsediffviewer.service.impl.IntelliJFileApplyService


class DiffOrchestrator(
    private val parser: CodeBlockParser,
    private val resolver: ContextResolver,
    private val viewer: DiffViewerManager,
    private val applyService: FileApplyService,
    private val errorHandler: ErrorHandler
) {
    private var currentSession: DiffSession? = null

    fun run(response: AiResponse): Boolean {
        val blocks = parser.extractCodeBlocks(response)
        if (blocks.isEmpty()) {
            errorHandler.handle(DiffViewerError.NoCodeBlockFound)
            return false
        }

        val target = resolver.resolveTarget()
        if (target == null) {
            errorHandler.handle(DiffViewerError.NoTargetFileFound)
            return false
        }

        if (resolver is EditorContextResolver && applyService is IntelliJFileApplyService) {
            resolver.lastResolvedDocument?.let { applyService.setTargetDocument(it) }
        }

        val firstBlock = blocks.first()

        val session = DiffSession(
            originalCode = target.code,
            suggestedCode = firstBlock.content,
            filePath = target.filePath,
            language = firstBlock.language
        )

        if (session.originalCode.trimIndent().trim() ==
            session.suggestedCode.trimIndent().trim()) {
            errorHandler.handle(DiffViewerError.IdenticalContent)
            return false
        }

        val fileExtension = target.filePath?.substringAfterLast(".")
        val suggestionLanguage = firstBlock.language
        if (fileExtension != null && suggestionLanguage != null &&
            !isLanguageCompatible(fileExtension, suggestionLanguage)) {
            errorHandler.handle(
                DiffViewerError.LanguageMismatch(
                    fileLanguage = fileExtension,
                    suggestionLanguage = suggestionLanguage
                )
            )
        }

        currentSession = session
        viewer.openDiff(session)
        return true
    }

    fun applyChanges() {
        val session = currentSession
        if (session == null) {
            errorHandler.handle(DiffViewerError.NoActiveDiffSession)
            return
        }
        applyService.applyChanges(session)
        currentSession = null
    }

    fun rejectChanges() {
        applyService.rejectChanges()
        currentSession = null
    }

    private fun isLanguageCompatible(fileExtension: String, language: String): Boolean {
        val compatibilityMap = mapOf(
            "kt" to listOf("kotlin", "kt"),
            "java" to listOf("java"),
            "py" to listOf("python", "py"),
            "js" to listOf("javascript", "js"),
            "ts" to listOf("typescript", "ts"),
            "go" to listOf("go", "golang"),
            "rs" to listOf("rust", "rs"),
            "cs" to listOf("csharp", "cs", "c#"),
            "cpp" to listOf("cpp", "c++"),
            "c" to listOf("c")
        )
        val compatible = compatibilityMap[fileExtension.lowercase()] ?: return true
        return language.lowercase() in compatible
    }
}