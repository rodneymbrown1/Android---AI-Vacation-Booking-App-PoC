package com.example.learning_2.feature.excursion

import com.example.learning_2.core.common.AppError
import com.example.learning_2.core.database.entity.Excursion
import com.example.learning_2.core.database.entity.Vacation

data class ExcursionUiState(
    val vacation: Vacation? = null,
    val excursions: List<Excursion> = emptyList(),
    val aiSuggestions: List<Excursion> = emptyList(),
    val isLoading: Boolean = false,
    val isGeneratingAi: Boolean = false,
    val error: AppError? = null
)
