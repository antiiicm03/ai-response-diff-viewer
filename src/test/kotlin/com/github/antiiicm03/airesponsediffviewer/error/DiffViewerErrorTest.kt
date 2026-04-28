package com.github.antiiicm03.airesponsediffviewer.error

import com.github.antiiicm03.airesponsediffviewer.service.DiffViewerError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class DiffViewerErrorTest {

    @Test
    fun testAllErrorTypesInstantiate() {
        val errors = listOf(
            DiffViewerError.NoCodeBlockFound,
            DiffViewerError.NoTargetFileFound,
            DiffViewerError.EmptyResponse,
            DiffViewerError.IdenticalContent,
            DiffViewerError.NoActiveDiffSession,
            DiffViewerError.ParsingFailed("test reason"),
            DiffViewerError.LanguageMismatch("kt", "python")
        )

        errors.forEach { assertNotNull(it) }
    }

    @Test
    fun testLanguageMismatchCarriesData() {
        val error = DiffViewerError.LanguageMismatch(
            fileLanguage = "kt",
            suggestionLanguage = "python"
        )

        assertEquals("kt", error.fileLanguage)
        assertEquals("python", error.suggestionLanguage)
    }

    @Test
    fun testParsingFailedCarriesReason() {
        val error = DiffViewerError.ParsingFailed("unexpected token")
        assertEquals("unexpected token", error.reason)
    }
}