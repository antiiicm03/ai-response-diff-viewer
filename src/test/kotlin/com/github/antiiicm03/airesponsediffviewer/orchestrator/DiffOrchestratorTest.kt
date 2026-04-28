package com.github.antiiicm03.airesponsediffviewer.orchestrator

import com.github.antiiicm03.airesponsediffviewer.model.AiResponse
import com.github.antiiicm03.airesponsediffviewer.model.CodeBlock
import com.github.antiiicm03.airesponsediffviewer.model.DiffSession
import com.github.antiiicm03.airesponsediffviewer.model.TargetContext
import com.github.antiiicm03.airesponsediffviewer.service.CodeBlockParser
import com.github.antiiicm03.airesponsediffviewer.service.ContextResolver
import com.github.antiiicm03.airesponsediffviewer.service.DiffViewerError
import com.github.antiiicm03.airesponsediffviewer.service.DiffViewerManager
import com.github.antiiicm03.airesponsediffviewer.service.ErrorHandler
import com.github.antiiicm03.airesponsediffviewer.service.FileApplyService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DiffOrchestratorTest {


    private inner class FakeParser(
        private val blocks: List<CodeBlock>
    ) : CodeBlockParser {
        override fun extractCodeBlocks(response: AiResponse) = blocks
    }

    private inner class FakeResolver(
        private val context: TargetContext?
    ) : ContextResolver {
        override fun resolveTarget() = context
    }

    private inner class FakeViewer(
        private val onOpen: (DiffSession) -> Unit = {}
    ) : DiffViewerManager {
        override fun openDiff(session: DiffSession) = onOpen(session)
    }

    private inner class FakeApplyService : FileApplyService {
        override fun applyChanges(session: DiffSession) {}
        override fun rejectChanges() {}
    }

    private inner class FakeErrorHandler : ErrorHandler {
        var capturedError: DiffViewerError? = null
        override fun handle(error: DiffViewerError) { capturedError = error }
    }


    private fun makeOrchestrator(
        blocks: List<CodeBlock> = listOf(
            CodeBlock(
                content = "fun hello() = println(\"Hello, World!\")",
                language = "kotlin"
            )
        ),
        context: TargetContext? = TargetContext(
            code = "fun hello() = println(\"Hello\")",
            filePath = "/Main.kt"
        ),
        onDiffOpened: (DiffSession) -> Unit = {}
    ): Pair<DiffOrchestrator, FakeErrorHandler> {
        val errorHandler = FakeErrorHandler()
        val orchestrator = DiffOrchestrator(
            parser = FakeParser(blocks),
            resolver = FakeResolver(context),
            viewer = FakeViewer(onDiffOpened),
            applyService = FakeApplyService(),
            errorHandler = errorHandler
        )
        return orchestrator to errorHandler
    }


    @Test
    fun testReturnsFalseWhenNoCodeBlock() {
        val (orchestrator, errorHandler) = makeOrchestrator(blocks = emptyList())

        val result = orchestrator.run(AiResponse("no code here"))

        assertFalse(result)
        assertEquals(DiffViewerError.NoCodeBlockFound, errorHandler.capturedError)
    }

    @Test
    fun testReturnsFalseWhenNoTargetFile() {
        val (orchestrator, errorHandler) = makeOrchestrator(context = null)

        val result = orchestrator.run(AiResponse("```kotlin\nval x = 1\n```"))

        assertFalse(result)
        assertEquals(DiffViewerError.NoTargetFileFound, errorHandler.capturedError)
    }

    @Test
    fun testReturnsFalseWhenIdenticalContent() {
        val sameCode = "fun hello() = println(\"Hello\")"
        val (orchestrator, errorHandler) = makeOrchestrator(
            blocks = listOf(CodeBlock(content = sameCode, language = "kotlin")),
            context = TargetContext(code = sameCode, filePath = "/Main.kt")
        )

        val result = orchestrator.run(AiResponse("```kotlin\n$sameCode\n```"))

        assertFalse(result)
        assertEquals(DiffViewerError.IdenticalContent, errorHandler.capturedError)
    }

    @Test
    fun testReturnsTrueOnSuccessfulFlow() {
        var diffOpened = false
        val (orchestrator, _) = makeOrchestrator(
            onDiffOpened = { diffOpened = true }
        )

        val result = orchestrator.run(AiResponse("```kotlin\nfun hello() = println(\"Hi\")\n```"))

        assertTrue(result)
        assertTrue(diffOpened)
    }

    @Test
    fun testApplyChangesWithoutSessionTriggersError() {
        val (orchestrator, errorHandler) = makeOrchestrator()

        orchestrator.applyChanges()

        assertEquals(DiffViewerError.NoActiveDiffSession, errorHandler.capturedError)
    }
}