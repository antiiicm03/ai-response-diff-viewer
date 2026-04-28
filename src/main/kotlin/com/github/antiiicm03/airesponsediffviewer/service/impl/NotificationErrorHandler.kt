package com.github.antiiicm03.airesponsediffviewer.service.impl

import com.github.antiiicm03.airesponsediffviewer.service.DiffViewerError
import com.github.antiiicm03.airesponsediffviewer.service.ErrorHandler
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

class NotificationErrorHandler(private val project: Project) : ErrorHandler {
    override fun handle(error: DiffViewerError) {
        val (message, type) = when (error) {
            is DiffViewerError.EmptyResponse ->
                "The pasted response is empty. Please paste an AI response first." to
                        NotificationType.WARNING

            is DiffViewerError.NoCodeBlockFound ->
                "No valid code block detected. " +
                        "Please paste the full AI response, including the markdown code block:\n" +
                        "```language\ncode\n```" to
                        NotificationType.WARNING

            is DiffViewerError.NoTargetFileFound ->
                "No file is currently open in the editor. " +
                        "Open a file to use as the comparison target." to
                        NotificationType.WARNING

            is DiffViewerError.IdenticalContent ->
                "No meaningful changes detected. " +
                        "The AI suggestion matches the current code." to
                        NotificationType.INFORMATION

            is DiffViewerError.NoActiveDiffSession ->
                "No active diff session. " +
                        "Please run Compare before accepting changes." to
                        NotificationType.WARNING

            is DiffViewerError.LanguageMismatch ->
                "Language mismatch detected: current file is ${error.fileLanguage}, " +
                        "AI suggestion is ${error.suggestionLanguage}. " +
                        "Please verify the suggestion manually." to
                        NotificationType.WARNING

            is DiffViewerError.ParsingFailed ->
                "Parsing failed: ${error.reason}" to
                        NotificationType.ERROR
        }

        NotificationGroupManager.getInstance()
            .getNotificationGroup("AI Diff Viewer")
            .createNotification(message, type)
            .notify(project)
    }
}