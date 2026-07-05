package com.example.learning_2.feature.vacation

import com.example.learning_2.core.common.AppError
import com.example.learning_2.core.database.entity.Vacation

data class VacationUiState(
    val vacations: List<Vacation> = emptyList(),
    val selectedVacation: Vacation? = null,
    val isLoading: Boolean = false,
    val error: AppError? = null
)
