package com.github.antiiicm03.airesponsediffviewer.service

import com.github.antiiicm03.airesponsediffviewer.model.TargetContext

interface ContextResolver {
    fun resolveTarget(): TargetContext?
}