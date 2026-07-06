package com.example.learning_2.core.common

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DateValidatorTest {

    @Test
    fun `rejects invalid format`() {
        val invalidDates = listOf("06-01-2025", "2025/06/01", "20250601", "not-a-date", "2025-13-40")
        invalidDates.forEach { date ->
            assertFalse("$date should be invalid", DateValidator.isValidFormat(date))
        }
    }

    @Test
    fun `accepts correct format`() {
        val validDates = listOf("2025-06-01", "2024-12-31", "2026-01-15")
        validDates.forEach { date ->
            assertTrue("$date should be valid", DateValidator.isValidFormat(date))
        }
    }

    @Test
    fun `isAfter compares calendar dates`() {
        assertTrue(DateValidator.isAfter("2025-06-05", "2025-06-01"))
        assertFalse(DateValidator.isAfter("2025-06-01", "2025-06-05"))
        assertFalse(DateValidator.isAfter("2025-06-01", "2025-06-01"))
    }

    @Test
    fun `isWithinRange is inclusive of bounds`() {
        assertTrue(DateValidator.isWithinRange("2025-06-01", "2025-06-01", "2025-06-10"))
        assertTrue(DateValidator.isWithinRange("2025-06-10", "2025-06-01", "2025-06-10"))
        assertTrue(DateValidator.isWithinRange("2025-06-05", "2025-06-01", "2025-06-10"))
        assertFalse(DateValidator.isWithinRange("2025-05-31", "2025-06-01", "2025-06-10"))
        assertFalse(DateValidator.isWithinRange("2025-06-11", "2025-06-01", "2025-06-10"))
    }
}
