package com.github.antiiicm03.airesponsediffviewer.toolWindow

import com.github.antiiicm03.airesponsediffviewer.model.AiResponse
import com.github.antiiicm03.airesponsediffviewer.orchestrator.DiffOrchestrator
import com.github.antiiicm03.airesponsediffviewer.service.DiffViewerError
import com.github.antiiicm03.airesponsediffviewer.service.impl.MarkdownCodeBlockParser
import com.github.antiiicm03.airesponsediffviewer.service.impl.NotificationErrorHandler
import com.github.antiiicm03.airesponsediffviewer.service.impl.EditorContextResolver
import com.github.antiiicm03.airesponsediffviewer.service.DiffViewerManager
import com.github.antiiicm03.airesponsediffviewer.service.impl.IntellijDiffViewerManager
import com.github.antiiicm03.airesponsediffviewer.service.impl.IntelliJFileApplyService
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
import java.awt.FlowLayout
import javax.swing.BorderFactory
import javax.swing.JButton

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
    private val resolver = EditorContextResolver(project)
    private val errorHandler = NotificationErrorHandler(project)
    private val viewer = IntellijDiffViewerManager(project)
    private val applyService = IntelliJFileApplyService(project)

    private val orchestrator = DiffOrchestrator(
        parser = parser,
        resolver = resolver,
        viewer = viewer,
        applyService = applyService,
        errorHandler = errorHandler
    )

    private val responseTextArea = JBTextArea().apply {
        lineWrap = true
        wrapStyleWord = true
        emptyText.text = "Paste AI response here..."
    }

    private val compareButton = JButton("Compare").apply {
        addActionListener { onCompareClicked() }
    }

    private val acceptButton = JButton("Accept").apply {
        isEnabled = false
        addActionListener { onAcceptClicked() }
    }

    private val rejectButton = JButton("Reject").apply {
        isEnabled = false
        addActionListener { onRejectClicked() }
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

                add(statusLabel, BorderLayout.WEST)

                val buttonPanel = JBPanel<JBPanel<*>>(FlowLayout(FlowLayout.RIGHT, 4, 0)).apply {
                    add(rejectButton)
                    add(acceptButton)
                    add(compareButton)
                }
                add(buttonPanel, BorderLayout.EAST)
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
        orchestrator.run(AiResponse(rawText))

        acceptButton.isEnabled = true
        rejectButton.isEnabled = true
        statusLabel.text = "Review the diff, then Accept or Reject"
    }

    private fun onAcceptClicked() {
        orchestrator.applyChanges()
        resetButtons()
        statusLabel.text = "Changes applied"
    }

    private fun onRejectClicked() {
        orchestrator.rejectChanges()
        resetButtons()
        statusLabel.text = "Changes rejected"
    }

    private fun resetButtons() {
        acceptButton.isEnabled = false
        rejectButton.isEnabled = false
    }
}