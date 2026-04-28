package com.github.antiiicm03.airesponsediffviewer.parser

import com.github.antiiicm03.airesponsediffviewer.model.AiResponse
import com.github.antiiicm03.airesponsediffviewer.service.impl.MarkdownCodeBlockParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MarkDownCodeBlockParserTest {
    private val parser = MarkdownCodeBlockParser()

    @Test
    fun testExtractsSingleCodeBlock() {
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
    fun testReturnsEmptyWhenNoCodeBlock() {
        val response = AiResponse("This response has no code block.")
        val blocks = parser.extractCodeBlocks(response)
        assertTrue(blocks.isEmpty())
    }

    @Test
    fun testHandlesEmptyResponse() {
        val response = AiResponse("")
        val blocks = parser.extractCodeBlocks(response)
        assertTrue(blocks.isEmpty())
    }

    @Test
    fun testExtractMultipleBlocks() {
        val response = AiResponse(
            """
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

    @Test
    fun testDetectsLanguageTag() {
        val response = AiResponse("""
```python
            def hello():
                print("hello")
```
        """.trimIndent())

        val blocks = parser.extractCodeBlocks(response)
        assertEquals("python", blocks.first().language)
    }

    @Test
    fun testHandlesMissingLanguageTag() {
        val response = AiResponse("```\nsome code without language\n```")

        val blocks = parser.extractCodeBlocks(response)
        assertEquals(1, blocks.size)
        assertNull(blocks.first().language)
    }
}