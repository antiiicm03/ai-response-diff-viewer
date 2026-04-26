package com.github.antiiicm03.airesponsediffviewer.service

import com.github.antiiicm03.airesponsediffviewer.model.DiffSession

interface DiffService {
    fun prepareDiff(original: String, suggested: String): DiffSession
}