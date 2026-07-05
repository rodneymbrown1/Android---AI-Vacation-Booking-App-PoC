package com.example.learning_2.presentation.vacation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learning_2.data.repository.VacationRepository
import com.example.learning_2.entities.Vacation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VacationViewModel @Inject constructor(
    private val repository: VacationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VacationUiState())
    val uiState: StateFlow<VacationUiState> = _uiState.asStateFlow()

    init {
        loadVacations()
    }

    fun loadVacations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { repository.getAllVacations() }
                .onSuccess { vacations ->
                    _uiState.update { it.copy(vacations = vacations, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    fun addVacation(title: String, hotel: String, startDate: String, endDate: String) {
        viewModelScope.launch {
            val vacation = Vacation(title = title, hotel = hotel, startDate = startDate, endDate = endDate)
            runCatching { repository.insertVacation(vacation) }
                .onSuccess { loadVacations() }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun updateVacation(vacation: Vacation) {
        viewModelScope.launch {
            runCatching { repository.updateVacation(vacation) }
                .onSuccess { loadVacations() }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun deleteVacation(vacation: Vacation) {
        viewModelScope.launch {
            runCatching {
                val count = repository.getExcursionCountForVacation(vacation.id)
                if (count > 0) throw IllegalStateException("Cannot delete a vacation with excursions. Remove excursions first.")
                repository.deleteVacation(vacation)
            }
                .onSuccess { loadVacations() }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun selectVacation(vacation: Vacation?) {
        _uiState.update { it.copy(selectedVacation = vacation) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
