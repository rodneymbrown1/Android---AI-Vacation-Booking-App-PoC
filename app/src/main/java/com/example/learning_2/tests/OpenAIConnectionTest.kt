package com.example.learning_2.tests

import org.json.JSONArray
import org.junit.Assert
import org.junit.Test

class OpenAIConnectionTest {
    @Test
    fun testOpenAIResponse() {
        // Mocked API response (manually created for testing)
        val mockResponse = ("["
                + "{\"name\": \"Hiking Adventure\", \"description\": \"Explore the mountains.\", \"date\": \"2025-06-11\"},"
                + "{\"name\": \"Boat Tour\", \"description\": \"Enjoy a sunset cruise.\", \"date\": \"2025-06-12\"},"
                + "{\"name\": \"City Tour\", \"description\": \"Discover the city's landmarks.\", \"date\": \"2025-06-13\"}"
                + "]")

        try {
            val jsonArray = JSONArray(mockResponse)
            Assert.assertEquals(3, jsonArray.length().toLong())

            // Validate first excursion
            val firstExcursion = jsonArray.getJSONObject(0)
            Assert.assertEquals("Hiking Adventure", firstExcursion.getString("name"))
            Assert.assertEquals("Explore the mountains.", firstExcursion.getString("description"))
            Assert.assertEquals("2025-06-11", firstExcursion.getString("date"))
        } catch (e: Exception) {
            Assert.fail("Exception thrown: " + e.message)
        }
    }
}