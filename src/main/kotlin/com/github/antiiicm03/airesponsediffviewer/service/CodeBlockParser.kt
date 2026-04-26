package com.github.antiiicm03.airesponsediffviewer.service

import com.github.antiiicm03.airesponsediffviewer.model.AiResponse
import com.github.antiiicm03.airesponsediffviewer.model.CodeBlock

interface CodeBlockParser {
    fun extractCodeBlocks(response: AiResponse): List<CodeBlock>
}