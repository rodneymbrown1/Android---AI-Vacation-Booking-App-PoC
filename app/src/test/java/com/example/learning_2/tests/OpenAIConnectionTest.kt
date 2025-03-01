package com.example.learning_2.tests

import com.example.learning_2.components.HTTP.OpenAIConnection
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test

class OpenAIConnectionTest {
    @Test
    fun testOpenAIResponse() {
        // 🔹 Send real request to OpenAI
        val response = OpenAIConnection.fetchOpenAIResponse("Generate three travel excursions.")

        // ✅ Check if response is not empty
        Assert.assertNotNull("Response should not be null", response)
        Assert.assertFalse("Response should not be empty", response.trim { it <= ' ' }.isEmpty())

        // ✅ Check for a valid JSON format
        try {
            JSONObject("{ \"content\": $response }") // Wrap to avoid errors
        } catch (e: Exception) {
            Assert.fail("Response is not valid JSON: " + e.message)
        }

        // ✅ Ensure it does not contain an API error message
        Assert.assertFalse(
            "API should not return an error",
            response.contains("❌ OpenAI API Error")
        )
    }
}