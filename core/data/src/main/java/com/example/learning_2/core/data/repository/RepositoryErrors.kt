package com.example.learning_2.core.data.repository

import com.example.learning_2.core.common.AppError

/** Wraps a persistence-layer call so any unexpected failure surfaces as a typed [AppError.Database]. */
internal suspend fun <T> dbCall(block: suspend () -> T): T = try {
    block()
} catch (e: AppError) {
    throw e
} catch (e: Exception) {
    throw AppError.Database(e)
}
