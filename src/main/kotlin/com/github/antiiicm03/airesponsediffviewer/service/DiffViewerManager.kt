package com.github.antiiicm03.airesponsediffviewer.service

import com.github.antiiicm03.airesponsediffviewer.model.DiffSession

fun interface DiffViewerManager {
    fun openDiff(session: DiffSession)
}