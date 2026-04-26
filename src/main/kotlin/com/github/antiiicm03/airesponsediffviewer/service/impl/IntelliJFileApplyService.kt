package com.github.antiiicm03.airesponsediffviewer.service.impl

import com.github.antiiicm03.airesponsediffviewer.model.DiffSession
import com.github.antiiicm03.airesponsediffviewer.service.FileApplyService
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project

class IntelliJFileApplyService(private val project: Project) : FileApplyService {
    private var targetDocument: Document? = null

    fun setTargetDocument(document: Document) {
        targetDocument = document
    }

    override fun applyChanges(session: DiffSession) {
        val document = targetDocument ?: return

        ApplicationManager.getApplication().invokeLater {
        WriteCommandAction.runWriteCommandAction(
            project,
            "Apply AI Suggestion",
            null,
            {
                document.setText(session.suggestedCode)
            })
            targetDocument = null
        }
    }

    override fun rejectChanges(){
        targetDocument = null
    }
}