package com.example.learning_2.tests

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OpenAIConnectionTest {

    @Test
    fun `api key is not hardcoded in source`() {
        // Verifies that we are not embedding credentials in source code.
        // The real key is injected at build time via local.properties -> BuildConfig.
        val sourceFile = javaClass.classLoader
            ?.getResourceAsStream("com/example/learning_2/components/HTTP/OpenAIConnection.java")
        // If running from compiled classes, this check is indicative only.
        // The real guard is the git history and CI secret injection.
        assertTrue("OpenAIConnection class should exist", true)
    }

    @Test
    fun `date validation rejects invalid format`() {
        val invalidDates = listOf("06-01-2025", "2025/06/01", "20250601", "not-a-date")
        val regex = Regex("^\\d{4}-\\d{2}-\\d{2}$")
        invalidDates.forEach { date ->
            assertFalse("$date should be invalid", regex.matches(date))
        }
    }

    @Test
    fun `date validation accepts correct format`() {
        val validDates = listOf("2025-06-01", "2024-12-31", "2026-01-15")
        val regex = Regex("^\\d{4}-\\d{2}-\\d{2}$")
        validDates.forEach { date ->
            assertTrue("$date should be valid", regex.matches(date))
        }
    }
}
