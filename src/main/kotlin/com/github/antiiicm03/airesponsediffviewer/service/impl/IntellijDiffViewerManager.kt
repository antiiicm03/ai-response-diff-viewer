package com.github.antiiicm03.airesponsediffviewer.service.impl

import com.github.antiiicm03.airesponsediffviewer.model.DiffSession
import com.github.antiiicm03.airesponsediffviewer.service.DiffViewerManager
import com.intellij.diff.DiffContentFactory
import com.intellij.diff.requests.SimpleDiffRequest
import com.intellij.diff.DiffManager
import com.intellij.openapi.project.Project

class IntellijDiffViewerManager(private val project: Project) : DiffViewerManager {
    override fun openDiff(session: DiffSession) {
        val contentFactory = DiffContentFactory.getInstance()
        val originalContent = contentFactory.create(session.originalCode)
        val suggestedContent = contentFactory.create(session.suggestedCode)

        val request = SimpleDiffRequest(
            "AI Suggestion Review",
            originalContent,
            suggestedContent,
            "Current Code",
            "AI Suggestion"
        )

        DiffManager.getInstance().showDiff(project, request)
    }
}