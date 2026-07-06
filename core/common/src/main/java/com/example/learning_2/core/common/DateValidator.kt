package com.example.learning_2.core.common

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Single source of truth for the app's YYYY-MM-DD date format, replacing the
 * copies previously duplicated across the vacation and excursion screens.
 */
object DateValidator {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val formatRegex = Regex("^\\d{4}-\\d{2}-\\d{2}$")

    fun isValidFormat(date: String): Boolean =
        formatRegex.matches(date) && parseOrNull(date) != null

    fun isAfter(date: String, other: String): Boolean {
        val d = parseOrNull(date) ?: return false
        val o = parseOrNull(other) ?: return false
        return d.isAfter(o)
    }

    fun isWithinRange(date: String, startDate: String, endDate: String): Boolean {
        val d = parseOrNull(date) ?: return false
        val start = parseOrNull(startDate) ?: return false
        val end = parseOrNull(endDate) ?: return false
        return !d.isBefore(start) && !d.isAfter(end)
    }

    private fun parseOrNull(date: String): LocalDate? = try {
        LocalDate.parse(date, formatter)
    } catch (e: DateTimeParseException) {
        null
    }
}
