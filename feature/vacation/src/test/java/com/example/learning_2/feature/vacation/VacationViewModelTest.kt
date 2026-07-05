package com.example.learning_2.feature.vacation

import com.example.learning_2.core.common.AppError
import com.example.learning_2.core.data.repository.VacationRepository
import com.example.learning_2.core.database.entity.Vacation
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VacationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: VacationRepository
    private lateinit var viewModel: VacationViewModel

    private val sampleVacations = listOf(
        Vacation(id = 1L, title = "Hawaii", hotel = "Beach Resort", startDate = "2025-06-01", endDate = "2025-06-07"),
        Vacation(id = 2L, title = "New York", hotel = "Grand Hotel", startDate = "2025-09-15", endDate = "2025-09-20")
    )

    // Backing Flow the fake repository serves from, standing in for Room's live query.
    private lateinit var vacationsFlow: MutableStateFlow<List<Vacation>>

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        vacationsFlow = MutableStateFlow(sampleVacations)
        repository = mockk()
        every { repository.observeVacations() } returns vacationsFlow
        viewModel = VacationViewModel(repository)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state reflects the observed vacations`() = runTest {
        advanceUntilIdle()
        assertEquals(sampleVacations, viewModel.uiState.value.vacations)
        assertTrue(!viewModel.uiState.value.isLoading)
    }

    @Test
    fun `addVacation calls repository insert`() = runTest {
        coEvery { repository.insertVacation(any()) } returns 3L

        viewModel.addVacation("Paris", "Eiffel Hotel", "2025-11-01", "2025-11-07")
        advanceUntilIdle()

        coVerify { repository.insertVacation(any()) }
    }

    @Test
    fun `updateVacation calls repository update`() = runTest {
        val updated = sampleVacations.first().copy(title = "Hawaii Updated")
        coEvery { repository.updateVacation(updated) } returns Unit

        viewModel.updateVacation(updated)
        advanceUntilIdle()

        coVerify { repository.updateVacation(updated) }
    }

    @Test
    fun `deleteVacation with no excursions succeeds`() = runTest {
        val vacation = sampleVacations.first()
        coEvery { repository.getExcursionCountForVacation(vacation.id) } returns 0L
        coEvery { repository.deleteVacation(vacation) } returns Unit

        viewModel.deleteVacation(vacation)
        advanceUntilIdle()

        coVerify { repository.deleteVacation(vacation) }
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `deleteVacation with excursions sets a validation error`() = runTest {
        val vacation = sampleVacations.first()
        coEvery { repository.getExcursionCountForVacation(vacation.id) } returns 3L

        viewModel.deleteVacation(vacation)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.error is AppError.Validation)
    }

    @Test
    fun `selectVacation updates selectedVacation in state`() = runTest {
        advanceUntilIdle()
        val vacation = sampleVacations.first()
        viewModel.selectVacation(vacation)
        advanceUntilIdle()

        assertEquals(vacation, viewModel.uiState.value.selectedVacation)
    }

    @Test
    fun `clearError removes error from state`() = runTest {
        val vacation = sampleVacations.first()
        coEvery { repository.getExcursionCountForVacation(vacation.id) } returns 1L
        viewModel.deleteVacation(vacation)
        advanceUntilIdle()

        viewModel.clearError()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `observeVacations failure surfaces as a database error`() = runTest {
        every { repository.observeVacations() } returns flow { throw RuntimeException("DB error") }
        val failingViewModel = VacationViewModel(repository)
        advanceUntilIdle()

        assertTrue(failingViewModel.uiState.value.error is AppError.Database)
    }
}
