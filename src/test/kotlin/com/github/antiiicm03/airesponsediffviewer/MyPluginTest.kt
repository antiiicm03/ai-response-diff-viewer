package com.github.antiiicm03.airesponsediffviewer

import com.intellij.testFramework.TestDataPath
import com.intellij.testFramwork.fixtures.BasePlatformTestCase

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class MyPluginTest : BasePlatformTestCase() {
    fun testPluginLoadds() {
        assertNotNull(project)
    }
}