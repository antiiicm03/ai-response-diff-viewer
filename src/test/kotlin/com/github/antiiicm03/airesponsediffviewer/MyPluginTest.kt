package com.github.antiiicm03.airesponsediffviewer

import com.github.antiiicm03.airesponsediffviewer.toolWindow.AiDiffToolWindowFactory
import org.junit.Assert.assertNotNull
import org.junit.Test

class MyPluginTest {
    @Test
    fun testAiDiffToolWindowFactoryCanBeInstantiated() {
        val factory = AiDiffToolWindowFactory()
        assertNotNull(factory)
    }
}