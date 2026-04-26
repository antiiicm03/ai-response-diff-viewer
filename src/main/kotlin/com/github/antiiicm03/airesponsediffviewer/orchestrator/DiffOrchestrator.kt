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
    private val applyService : FileApplyService,
    private val errorHandler: ErrorHandler
) {
    private var currentSession: DiffSession? = null

    fun run(response: AiResponse) {
        val blocks = parser.extractCodeBlocks(response)

        if (blocks.isEmpty()) {
            errorHandler.handle(DiffViewerError.NoCodeBlockFound)
            return
        }

        val target = resolver.resolveTarget()

        if (target == null) {
            errorHandler.handle(DiffViewerError.NoTargetFileFound)
            return
        }

        if (resolver is EditorContextResolver && applyService is IntelliJFileApplyService) {
            resolver.lastResolvedDocument?.let { applyService.setTargetDocument(it)}
        }

        val session = DiffSession(
            originalCode = target.code,
            suggestedCode = blocks.first().content,
            filePath = target.filePath,
            language = blocks.first().language
        )

        currentSession = session
        viewer.openDiff(session)
    }

    fun applyChanges() {
        val session = currentSession
        if (session == null) {
            errorHandler.handle(DiffViewerError.NoTargetFileFound)
            return
        }
        applyService.applyChanges(session)
        currentSession = null
    }

    fun rejectChanges() {
        applyService.rejectChanges()
        currentSession = null
    }
}