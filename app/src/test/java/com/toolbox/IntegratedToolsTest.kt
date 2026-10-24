package com.toolbox

import com.toolbox.dashboard.allTools
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class IntegratedToolsTest {

    @Test
    fun testIntegratedToolsExist() {
        val toolIds = allTools.map { it.id }.toSet()
        val expectedTools = listOf(
            "advanced_math",
            "omni_web_browser",
            "epic_dev_hub",
            "omni_browser_suite",
            "omni_utility_suite"
        )

        for (id in expectedTools) {
            assertTrue("Expected tool $id to be registered in allTools", toolIds.contains(id))
            val def = allTools.find { it.id == id }
            assertNotNull("ToolDefinition for $id should not be null", def)
            assertTrue("Description for $id should not be blank", def!!.description.isNotBlank())
        }
    }
}
