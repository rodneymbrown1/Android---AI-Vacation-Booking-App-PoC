package com.example.learning_2.feature.excursion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learning_2.core.common.AppError
import com.example.learning_2.core.common.toAppError
import com.example.learning_2.core.data.repository.ExcursionRepository
import com.example.learning_2.core.data.repository.VacationRepository
import com.example.learning_2.core.database.entity.Excursion
import com.example.learning_2.core.database.entity.Vacation
import com.example.learning_2.core.network.AiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExcursionViewModel @Inject constructor(
    private val excursionRepository: ExcursionRepository,
    private val vacationRepository: VacationRepository,
    private val aiService: AiService
) : ViewModel() {

    private data class InternalState(
        val vacation: Vacation? = null,
        val aiSuggestions: List<Excursion> = emptyList(),
        val isLoading: Boolean = false,
        val isGeneratingAi: Boolean = false,
        val error: AppError? = null
    )

    private val currentVacationId = MutableStateFlow<Long?>(null)
    private val internalState = MutableStateFlow(InternalState())

    // Reactive stream of excursions for whichever vacation is currently loaded;
    // Room re-emits automatically after any insert/update/delete, so no manual refresh is needed.
    private val excursions = currentVacationId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else excursionRepository.observeExcursionsForVacation(id)
    }

    val uiState: StateFlow<ExcursionUiState> = combine(internalState, excursions) { internal, excursionList ->
        ExcursionUiState(
            vacation = internal.vacation,
            excursions = excursionList,
            aiSuggestions = internal.aiSuggestions,
            isLoading = internal.isLoading,
            isGeneratingAi = internal.isGeneratingAi,
            error = internal.error
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ExcursionUiState())

    fun loadVacationAndExcursions(vacationId: Long) {
        currentVacationId.value = vacationId
        viewModelScope.launch {
            internalState.update { it.copy(isLoading = true, error = null) }
            runCatching { vacationRepository.getVacationById(vacationId) }
                .onSuccess { vacation ->
                    internalState.update { it.copy(vacation = vacation, isLoading = false) }
                    vacation?.let { generateAiSuggestions(it) }
                }
                .onFailure { e ->
                    internalState.update { it.copy(error = e.toAppError(), isLoading = false) }
                }
        }
    }

    private fun generateAiSuggestions(vacation: Vacation) {
        viewModelScope.launch {
            internalState.update { it.copy(isGeneratingAi = true) }
            runCatching { aiService.generateExcursions(vacation) }
                .onSuccess { suggestions ->
                    internalState.update { it.copy(aiSuggestions = suggestions, isGeneratingAi = false) }
                }
                .onFailure { e ->
                    internalState.update { it.copy(isGeneratingAi = false, error = e.toAppError()) }
                }
        }
    }

    fun addExcursion(name: String, description: String, date: String) {
        val vacationId = currentVacationId.value ?: return
        viewModelScope.launch {
            val excursion = Excursion(name = name, description = description, date = date, vacationId = vacationId)
            runCatching { excursionRepository.insertExcursion(excursion) }
                .onFailure { e -> internalState.update { it.copy(error = e.toAppError()) } }
        }
    }

    fun updateExcursion(excursion: Excursion) {
        viewModelScope.launch {
            runCatching { excursionRepository.updateExcursion(excursion) }
                .onFailure { e -> internalState.update { it.copy(error = e.toAppError()) } }
        }
    }

    fun deleteExcursion(excursion: Excursion) {
        viewModelScope.launch {
            runCatching { excursionRepository.deleteExcursion(excursion) }
                .onFailure { e -> internalState.update { it.copy(error = e.toAppError()) } }
        }
    }

    fun clearError() {
        internalState.update { it.copy(error = null) }
    }
}
