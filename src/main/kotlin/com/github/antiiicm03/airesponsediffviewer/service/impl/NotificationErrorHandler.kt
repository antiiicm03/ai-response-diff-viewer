package com.github.antiiicm03.airesponsediffviewer.service.impl

import com.github.antiiicm03.airesponsediffviewer.service.DiffViewerError
import com.github.antiiicm03.airesponsediffviewer.service.ErrorHandler
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

class NotificationErrorHandler(private val project: Project) : ErrorHandler {
    override fun handle(error: DiffViewerError) {
        val message = when (error) {
            is DiffViewerError.NoCodeBlockFound ->
                "No code block found in the pasted response. Make sure it contains ``` blocks."
            is DiffViewerError.NoTargetFileFound ->
                "No file is currently open in the editor. Open a file to compare against."
            is DiffViewerError.EmptyResponse ->
                "The pasted response is empty. Please paste an AI response first."
            is DiffViewerError.IdenticalContent ->
                "The suggested code is identical to the current file. No changes to review."
            is DiffViewerError.ParsingFailed ->
                "Parsing failed: ${error.reason}"
        }

        NotificationGroupManager.getInstance()
            .getNotificationGroup("AI Diff Viewer")
            .createNotification(message, NotificationType.WARNING)
            .notify(project)
    }
}