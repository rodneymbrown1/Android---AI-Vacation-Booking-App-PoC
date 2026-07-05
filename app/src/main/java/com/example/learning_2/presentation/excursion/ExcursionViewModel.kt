package com.example.learning_2.presentation.excursion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learning_2.data.remote.AiService
import com.example.learning_2.data.repository.ExcursionRepository
import com.example.learning_2.data.repository.VacationRepository
import com.example.learning_2.entities.Excursion
import com.example.learning_2.entities.Vacation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExcursionViewModel @Inject constructor(
    private val excursionRepository: ExcursionRepository,
    private val vacationRepository: VacationRepository,
    private val aiService: AiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExcursionUiState())
    val uiState: StateFlow<ExcursionUiState> = _uiState.asStateFlow()

    fun loadVacationAndExcursions(vacationId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                val vacation = vacationRepository.getVacationById(vacationId)
                val excursions = excursionRepository.getExcursionsForVacation(vacationId)
                vacation to excursions
            }
                .onSuccess { (vacation, excursions) ->
                    _uiState.update { it.copy(vacation = vacation, excursions = excursions, isLoading = false) }
                    vacation?.let { generateAiSuggestions(it) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    private fun generateAiSuggestions(vacation: Vacation) {
        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingAi = true) }
            runCatching { aiService.generateExcursions(vacation) }
                .onSuccess { suggestions ->
                    _uiState.update { it.copy(aiSuggestions = suggestions, isGeneratingAi = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isGeneratingAi = false, error = e.message) }
                }
        }
    }

    fun addExcursion(name: String, description: String, date: String, vacationId: Long) {
        viewModelScope.launch {
            val excursion = Excursion(name = name, description = description, date = date, vacationId = vacationId)
            runCatching { excursionRepository.insertExcursion(excursion) }
                .onSuccess { refreshExcursions(vacationId) }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun updateExcursion(excursion: Excursion, vacationId: Long) {
        viewModelScope.launch {
            runCatching { excursionRepository.updateExcursion(excursion) }
                .onSuccess { refreshExcursions(vacationId) }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun deleteExcursion(excursion: Excursion, vacationId: Long) {
        viewModelScope.launch {
            runCatching { excursionRepository.deleteExcursion(excursion) }
                .onSuccess { refreshExcursions(vacationId) }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    private suspend fun refreshExcursions(vacationId: Long) {
        runCatching { excursionRepository.getExcursionsForVacation(vacationId) }
            .onSuccess { excursions -> _uiState.update { it.copy(excursions = excursions) } }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
