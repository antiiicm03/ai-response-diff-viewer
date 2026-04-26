package com.github.antiiicm03.airesponsediffviewer.service

import com.github.antiiicm03.airesponsediffviewer.model.DiffSession

interface FileApplyService {
    fun applyChanges(session: DiffSession)
    fun rejectChanges()
}