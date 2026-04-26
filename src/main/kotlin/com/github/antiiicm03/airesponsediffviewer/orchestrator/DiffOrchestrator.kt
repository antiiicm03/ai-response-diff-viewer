package com.github.antiiicm03.airesponsediffviewer.orchestrator

import com.github.antiiicm03.airesponsediffviewer.model.AiResponse
import com.github.antiiicm03.airesponsediffviewer.model.DiffSession
import com.github.antiiicm03.airesponsediffviewer.service.CodeBlockParser
import com.github.antiiicm03.airesponsediffviewer.service.ContextResolver
import com.github.antiiicm03.airesponsediffviewer.service.DiffViewerError
import com.github.antiiicm03.airesponsediffviewer.service.DiffViewerManager
import com.github.antiiicm03.airesponsediffviewer.service.ErrorHandler



class DiffOrchestrator(
    private val parser: CodeBlockParser,
    private val resolver: ContextResolver,
    private val viewer: DiffViewerManager,
    private val errorHandler: ErrorHandler
) {
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

        val session = DiffSession(
            originalCode = target.code,
            suggestedCode = blocks.first().content,
            filePath = target.filePath,
            language = blocks.first().language
        )

        viewer.openDiff(session)
    }
}