package com.github.antiiicm03.airesponsediffviewer.service

sealed class DiffViewerError {
    object NoCodeBlockFound : DiffViewerError()
    object NoTargetFileFound : DiffViewerError()
    object EmptyResponse : DiffViewerError()
    object IdenticalContent : DiffViewerError()
    data class ParsingFailed(val reason: String) : DiffViewerError()
}

interface ErrorHandler {
    fun handle(error: DiffViewerError)
}