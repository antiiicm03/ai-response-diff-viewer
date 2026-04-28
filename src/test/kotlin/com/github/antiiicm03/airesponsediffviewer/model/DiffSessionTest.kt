package com.github.antiiicm03.airesponsediffviewer.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DiffSessionTest {

    @Test
    fun testDiffSessionCreation() {
        val session = DiffSession(
            originalCode = "fun hello() = println(\"Hello\")",
            suggestedCode = "fun hello() = println(\"Hello, World!\")",
            filePath = "/project/src/Main.kt",
            language = "kotlin"
        )

        assertNotNull(session)
        assertEquals("kotlin", session.language)
        assertEquals("/project/src/Main.kt", session.filePath)
    }

    @Test
    fun testIdenticalContentIsDetectable() {
        val code = "fun hello() = println(\"Hello\")"

        val session = DiffSession(
            originalCode = code,
            suggestedCode = code,
            filePath = null,
            language = "kotlin"
        )

        val isIdentical = session.originalCode.trimIndent().trim() ==
                session.suggestedCode.trimIndent().trim()

        assertTrue(isIdentical)
    }

    @Test
    fun testDiffSessionWithNullFields() {
        val session = DiffSession(
            originalCode = "val x = 1",
            suggestedCode = "val x = 2",
            filePath = null,
            language = null
        )

        assertNotNull(session)
        assertTrue(session.originalCode != session.suggestedCode)
    }
}