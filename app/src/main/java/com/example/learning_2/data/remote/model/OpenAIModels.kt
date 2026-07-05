package com.example.learning_2.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIRequest(
    val model: String = "gpt-4o-mini",
    val messages: List<ChatMessage>,
    val temperature: Double = 0.7
)

@Serializable
data class ChatMessage(
    val role: String,
    val content: String
)

@Serializable
data class OpenAIResponse(
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: ChatMessage,
    @SerialName("finish_reason") val finishReason: String? = null
)
