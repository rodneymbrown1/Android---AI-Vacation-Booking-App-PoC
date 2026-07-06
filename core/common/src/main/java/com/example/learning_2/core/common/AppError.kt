package com.example.learning_2.core.common

/**
 * Typed failure surfaced from the data layer so ViewModels can render a specific,
 * actionable message instead of a raw exception string.
 */
sealed class AppError(message: String, cause: Throwable? = null) : Exception(message, cause) {

    class Network(cause: Throwable? = null) :
        AppError("Network connection problem. Check your connection and try again.", cause)

    class Api(val code: Int, cause: Throwable? = null) : AppError(apiMessage(code), cause)

    class Parse(cause: Throwable? = null) :
        AppError("Received an unexpected response. Please try again.", cause)

    class Database(cause: Throwable? = null) :
        AppError("A local data error occurred.", cause)

    class Validation(message: String) : AppError(message)

    class Unknown(cause: Throwable? = null) :
        AppError(cause?.message ?: "Something went wrong.", cause)

    private companion object {
        fun apiMessage(code: Int): String = when (code) {
            401, 403 -> "Authentication failed. Check your API key."
            429 -> "Rate limit exceeded. Wait a moment and try again."
            in 500..599 -> "The AI service is temporarily unavailable. Try again shortly."
            else -> "The AI service returned an unexpected error ($code)."
        }
    }
}

fun Throwable.toAppError(): AppError = this as? AppError ?: AppError.Unknown(this)
