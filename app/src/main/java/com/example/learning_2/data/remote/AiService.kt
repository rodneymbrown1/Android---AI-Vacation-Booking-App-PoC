package com.example.learning_2.data.remote

import com.example.learning_2.BuildConfig
import com.example.learning_2.data.remote.model.ChatMessage
import com.example.learning_2.data.remote.model.OpenAIRequest
import com.example.learning_2.data.remote.model.OpenAIResponse
import com.example.learning_2.entities.Excursion
import com.example.learning_2.entities.Vacation
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiService @Inject constructor() {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    suspend fun generateExcursions(vacation: Vacation): List<Excursion> {
        val prompt = buildPrompt(vacation)
        val response: OpenAIResponse = client.post("https://api.openai.com/v1/chat/completions") {
            header("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
            contentType(ContentType.Application.Json)
            setBody(
                OpenAIRequest(
                    messages = listOf(
                        ChatMessage(role = "system", content = "You are a helpful assistant that generates vacation excursion plans."),
                        ChatMessage(role = "user", content = prompt)
                    )
                )
            )
        }.body()

        return parseExcursions(response.choices.firstOrNull()?.message?.content.orEmpty(), vacation.id)
    }

    private fun buildPrompt(vacation: Vacation): String =
        """Generate 3 unique excursions for a vacation titled '${vacation.title}' at '${vacation.hotel}'
        |from ${vacation.startDate} to ${vacation.endDate}.
        |Each excursion should have a Name, Description, and a date within the vacation period.
        |Return ONLY a valid JSON array with no other text:
        |[{"name":"...","description":"...","date":"YYYY-MM-DD"},...]""".trimMargin()

    private fun parseExcursions(json: String, vacationId: Long): List<Excursion> {
        val cleanJson = json.trim().removePrefix("```json").removeSuffix("```").trim()
        val array = JSONArray(cleanJson)
        return (0 until array.length()).map { i ->
            val obj = array.getJSONObject(i)
            Excursion(
                id = 0,
                name = obj.getString("name"),
                description = obj.getString("description"),
                date = obj.getString("date"),
                vacationId = vacationId
            )
        }
    }
}
