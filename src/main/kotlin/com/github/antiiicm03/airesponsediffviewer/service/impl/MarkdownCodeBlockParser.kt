package com.github.antiiicm03.airesponsediffviewer.service.impl

import com.github.antiiicm03.airesponsediffviewer.model.AiResponse
import com.github.antiiicm03.airesponsediffviewer.model.CodeBlock
import com.github.antiiicm03.airesponsediffviewer.service.CodeBlockParser

class MarkdownCodeBlockParser : CodeBlockParser {
    private val codeBlockRegex = Regex (
        pattern = "```(\\w*)\\n([\\s\\S]*?)```",
        option = RegexOption.MULTILINE
    )

    override fun extractCodeBlocks(response: AiResponse): List<CodeBlock> {
        if (response.rawText.isBlank()) return emptyList()

        return codeBlockRegex.findAll(response.rawText)
            .map { match ->
                CodeBlock(
                    language = match.groupValues[1].ifBlank { null },
                    content = match.groupValues[2].trimEnd()
                )
            }
        .toList()
    }
}