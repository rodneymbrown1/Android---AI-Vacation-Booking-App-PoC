package com.example.learning_2.presentation.vacation

import com.example.learning_2.entities.Vacation

data class VacationUiState(
    val vacations: List<Vacation> = emptyList(),
    val selectedVacation: Vacation? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
