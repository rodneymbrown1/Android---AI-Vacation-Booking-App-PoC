package com.example.learning_2.presentation.excursion

import com.example.learning_2.entities.Excursion
import com.example.learning_2.entities.Vacation

data class ExcursionUiState(
    val vacation: Vacation? = null,
    val excursions: List<Excursion> = emptyList(),
    val aiSuggestions: List<Excursion> = emptyList(),
    val isLoading: Boolean = false,
    val isGeneratingAi: Boolean = false,
    val error: String? = null
)
