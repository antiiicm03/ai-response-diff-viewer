package com.github.antiiicm03.airesponsediffviewer.toolWindow

import com.github.antiiicm03.airesponsediffviewer.model.AiResponse
import com.github.antiiicm03.airesponsediffviewer.service.impl.MarkdownCodeBlockParser
import com.github.antiiicm03.airesponsediffviewer.service.impl.NotificationErrorHandler
import com.github.antiiicm03.airesponsediffviewer.service.DiffViewerError
import com.intellij.diff.comparison.trimEnd
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JPanel

class AiDiffToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = AiDiffPanel(project)
        val content = ContentFactory.getInstance().createContent(panel.getContent(), null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

}

class AiDiffPanel(private val project: Project){
    private val parser = MarkdownCodeBlockParser()
    private val errorHandler = NotificationErrorHandler(project)

    private val responseTextArea = JBTextArea().apply {
        lineWrap = true
        wrapStyleWord = true
        emptyText.text = "Paste AI response here..."
    }

    private val compareButton = JButton("Compare").apply {
        addActionListener { onCompareClicked() }
    }

    private val statusLabel = JBLabel("Ready")

    fun getContent(): JBPanel<*> {
        return JBPanel<JBPanel<*>>(BorderLayout()).apply {
            border = BorderFactory.createEmptyBorder(8, 8, 8, 8)

            val scrollPane = JBScrollPane(responseTextArea).apply {
                preferredSize = Dimension(400, 200)
            }
            add(scrollPane, BorderLayout.CENTER)

            val bottomPanel = JBPanel<JBPanel<*>>(BorderLayout()).apply {
                border = BorderFactory.createEmptyBorder(8, 0, 0, 0)
                add(compareButton, BorderLayout.EAST)
                add(statusLabel, BorderLayout.WEST)
            }
            add(bottomPanel, BorderLayout.SOUTH)
        }
    }

    private fun onCompareClicked() {
        val rawText = responseTextArea.text.trim()

        if (rawText.isBlank()) {
            errorHandler.handle(DiffViewerError.EmptyResponse)
            statusLabel.text = "Error: empty response"
            return
        }

        val response = AiResponse(rawText)
        val blocks = parser.extractCodeBlocks(response)

        if (blocks.isEmpty()) {
            errorHandler.handle(DiffViewerError.NoCodeBlockFound)
            statusLabel.text = "Error: no code block found"
            return
        }

        val firstBlock = blocks.first()
        val langInfo = firstBlock.language ?: "unknown"
        statusLabel.text = "Found ${blocks.size} block(s) - language $langInfo"
    }
}