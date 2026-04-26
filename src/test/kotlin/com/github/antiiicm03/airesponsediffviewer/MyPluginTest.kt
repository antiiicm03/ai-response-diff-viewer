package com.github.antiiicm03.airesponsediffviewer

import com.github.antiiicm03.airesponsediffviewer.model.AiResponse
import com.github.antiiicm03.airesponsediffviewer.service.impl.MarkdownCodeBlockParser
import com.github.antiiicm03.airesponsediffviewer.toolWindow.AiDiffToolWindowFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MyPluginTest {

    private val parser = MarkdownCodeBlockParser()

    @Test
    fun testAiDiffToolWindowFactoryCanBeInstantiated() {
        val factory = AiDiffToolWindowFactory()
        assertNotNull(factory)
    }

    @Test
    fun testParserExtractsCodeBlock() {
        val response = AiResponse("""
            Here is the solution:
```kotlin
            fun hello() = println("Hello")
```
        """.trimIndent())

        val blocks = parser.extractCodeBlocks(response)
        assertEquals(1, blocks.size)
        assertEquals("kotlin", blocks.first().language)
    }

    @Test
    fun testParserReturnsEmptyOnNoCodeBlock() {
        val response = AiResponse("This response has no code block.")
        val blocks = parser.extractCodeBlocks(response)
        assertTrue(blocks.isEmpty())
    }

    @Test
    fun testParserHandlesEmptyResponse() {
        val response = AiResponse("")
        val blocks = parser.extractCodeBlocks(response)
        assertTrue(blocks.isEmpty())
    }

    @Test
    fun testParserExtractsMultipleBlocks() {
        val response = AiResponse("""
            First:
```java
            int x = 1;
```
            Second:
```kotlin
            val x = 1
```
        """.trimIndent())

        val blocks = parser.extractCodeBlocks(response)
        assertEquals(2, blocks.size)
    }
}