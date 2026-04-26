package com.github.antiiicm03.airesponsediffviewer.service.impl

import com.github.antiiicm03.airesponsediffviewer.model.TargetContext
import com.github.antiiicm03.airesponsediffviewer.service.ContextResolver
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project

class EditorContextResolver(private val project: Project) : ContextResolver {
    override fun resolveTarget(): TargetContext? {
        val editor = getCurrentEditor() ?: return null

        val selectedText = getSelectedText(editor)
        if (selectedText != null) {
            return TargetContext(
                code = selectedText,
                filePath = getCurrentFilePath()
            )
        }

        val fullContent = getFullFileContent(editor)
        if (fullContent != null) {
            return TargetContext(
                code = fullContent,
                filePath = getCurrentFilePath()
            )
        }

        return null
    }

    private fun getCurrentEditor(): Editor? {
        return FileEditorManager.getInstance(project).selectedTextEditor
    }

    private fun getSelectedText(editor: Editor): String? {
        val selectedText = editor.selectionModel.selectedText
        return if (selectedText.isNullOrBlank()) null else selectedText
    }

    private fun getFullFileContent(editor: Editor): String? {
        val content = editor.document.text
        return if (content.isBlank()) null else content
    }

    private fun getCurrentFilePath(): String? {
        return FileEditorManager.getInstance(project)
            .selectedFiles
            .firstOrNull()
            ?.path
    }
}

