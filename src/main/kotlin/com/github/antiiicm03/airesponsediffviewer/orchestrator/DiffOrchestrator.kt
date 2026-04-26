package com.github.antiiicm03.airesponsediffviewer.orchestrator

import com.github.antiiicm03.airesponsediffviewer.model.AiResponse
import com.github.antiiicm03.airesponsediffviewer.service.CodeBlockParser
import com.github.antiiicm03.airesponsediffviewer.service.ContextResolver
import com.github.antiiicm03.airesponsediffviewer.service.DiffService
import com.github.antiiicm03.airesponsediffviewer.service.DiffViewerError
import com.github.antiiicm03.airesponsediffviewer.service.DiffViewerManager
import com.github.antiiicm03.airesponsediffviewer.service.ErrorHandler



class DiffOrchestrator(
    private val parser: CodeBlockParser,
    private val resolver: ContextResolver,
    private val diffService: DiffService,
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

        val session = diffService.prepareDiff(
            original = target.code,
            suggested = blocks.first().content
        )

        viewer.openDiff(session)
    }
}