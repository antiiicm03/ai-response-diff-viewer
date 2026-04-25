package com.github.antiiicm03.airesponsediffviewer.toolWindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout

class AiDiffToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = createPlaceholderPanel()
        val content = ContentFactory.getInstance().createContent(panel, null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    private fun createPlaceholderPanel(): JBPanel<*> {
        return JBPanel<JBPanel<*>>(BorderLayout()).apply {
            add(JBLabel("AI Diff Viewer — Phase 1 skeleton"), BorderLayout.CENTER)
        }
    }
}