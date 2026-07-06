package com.example.learning_2.feature.vacation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learning_2.core.common.AppError
import com.example.learning_2.core.common.toAppError
import com.example.learning_2.core.data.repository.VacationRepository
import com.example.learning_2.core.database.entity.Vacation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VacationViewModel @Inject constructor(
    private val repository: VacationRepository
) : ViewModel() {

    private data class InternalState(
        val selectedVacation: Vacation? = null,
        val error: AppError? = null
    )

    private val internalState = MutableStateFlow(InternalState())

    // Room re-emits this automatically after every insert/update/delete, so the UI
    // never needs to manually reload the list after a mutation.
    private val vacations = repository.observeVacations()
        .catch { e ->
            internalState.update { it.copy(error = e.toAppError()) }
            emit(emptyList())
        }

    val uiState: StateFlow<VacationUiState> = combine(vacations, internalState) { vacationList, internal ->
        VacationUiState(
            vacations = vacationList,
            selectedVacation = internal.selectedVacation,
            isLoading = false,
            error = internal.error
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, VacationUiState(isLoading = true))

    fun addVacation(title: String, hotel: String, startDate: String, endDate: String) {
        viewModelScope.launch {
            val vacation = Vacation(title = title, hotel = hotel, startDate = startDate, endDate = endDate)
            runCatching { repository.insertVacation(vacation) }
                .onFailure { e -> internalState.update { it.copy(error = e.toAppError()) } }
        }
    }

    fun updateVacation(vacation: Vacation) {
        viewModelScope.launch {
            runCatching { repository.updateVacation(vacation) }
                .onFailure { e -> internalState.update { it.copy(error = e.toAppError()) } }
        }
    }

    fun deleteVacation(vacation: Vacation) {
        viewModelScope.launch {
            runCatching {
                val count = repository.getExcursionCountForVacation(vacation.id)
                if (count > 0) {
                    throw AppError.Validation("Cannot delete a vacation with excursions. Remove excursions first.")
                }
                repository.deleteVacation(vacation)
            }.onFailure { e -> internalState.update { it.copy(error = e.toAppError()) } }
        }
    }

    fun selectVacation(vacation: Vacation?) {
        internalState.update { it.copy(selectedVacation = vacation) }
    }

    fun clearError() {
        internalState.update { it.copy(error = null) }
    }
}
