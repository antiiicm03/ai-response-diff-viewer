package com.github.antiiicm03.airesponsediffviewer.model

data class DiffSession(
    val originalCode: String,
    val suggestedCode: String,
    val filePath: String?,
    val language: String?
)