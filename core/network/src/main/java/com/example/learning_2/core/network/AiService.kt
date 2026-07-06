package com.example.learning_2.core.network

import com.example.learning_2.core.common.AppError
import com.example.learning_2.core.database.entity.Excursion
import com.example.learning_2.core.database.entity.Vacation
import com.example.learning_2.core.network.model.ChatMessage
import com.example.learning_2.core.network.model.OpenAIRequest
import com.example.learning_2.core.network.model.OpenAIResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ServerResponseException
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
import java.io.IOException
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
        install(HttpTimeout) {
            requestTimeoutMillis = REQUEST_TIMEOUT_MS
            connectTimeoutMillis = CONNECT_TIMEOUT_MS
            socketTimeoutMillis = REQUEST_TIMEOUT_MS
        }
        install(HttpRequestRetry) {
            maxRetries = MAX_RETRIES
            retryOnServerErrors()
            retryOnException(retryOnTimeout = true)
            exponentialDelay()
        }
    }

    suspend fun generateExcursions(vacation: Vacation): List<Excursion> {
        val response = fetchCompletion(vacation)
        return try {
            parseExcursions(response.choices.firstOrNull()?.message?.content.orEmpty(), vacation.id)
        } catch (e: Exception) {
            throw AppError.Parse(e)
        }
    }

    private suspend fun fetchCompletion(vacation: Vacation): OpenAIResponse = try {
        client.post("https://api.openai.com/v1/chat/completions") {
            header("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
            contentType(ContentType.Application.Json)
            setBody(
                OpenAIRequest(
                    messages = listOf(
                        ChatMessage(role = "system", content = "You are a helpful assistant that generates vacation excursion plans."),
                        ChatMessage(role = "user", content = buildPrompt(vacation))
                    )
                )
            )
        }.body()
    } catch (e: ClientRequestException) {
        throw AppError.Api(e.response.status.value, e)
    } catch (e: ServerResponseException) {
        throw AppError.Api(e.response.status.value, e)
    } catch (e: HttpRequestTimeoutException) {
        throw AppError.Network(e)
    } catch (e: IOException) {
        throw AppError.Network(e)
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

    private companion object {
        const val REQUEST_TIMEOUT_MS = 15_000L
        const val CONNECT_TIMEOUT_MS = 10_000L
        const val MAX_RETRIES = 3
    }
}
