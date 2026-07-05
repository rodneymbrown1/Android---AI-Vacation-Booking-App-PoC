package com.example.learning_2.presentation.vacation

import com.example.learning_2.data.repository.VacationRepository
import com.example.learning_2.entities.Vacation
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        coEvery { repository.getAllVacations() } returns sampleVacations
        viewModel = VacationViewModel(repository)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads vacations into state`() = runTest {
        advanceUntilIdle()
        assertEquals(sampleVacations, viewModel.uiState.value.vacations)
        assertTrue(!viewModel.uiState.value.isLoading)
    }

    @Test
    fun `addVacation calls repository and reloads list`() = runTest {
        coEvery { repository.insertVacation(any()) } returns 3L
        coEvery { repository.getAllVacations() } returns sampleVacations

        viewModel.addVacation("Paris", "Eiffel Hotel", "2025-11-01", "2025-11-07")
        advanceUntilIdle()

        coVerify { repository.insertVacation(any()) }
        coVerify(atLeast = 2) { repository.getAllVacations() }
    }

    @Test
    fun `updateVacation calls repository update and reloads`() = runTest {
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
    }

    @Test
    fun `deleteVacation with excursions sets error state`() = runTest {
        val vacation = sampleVacations.first()
        coEvery { repository.getExcursionCountForVacation(vacation.id) } returns 3L

        viewModel.deleteVacation(vacation)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.error != null)
    }

    @Test
    fun `selectVacation updates selectedVacation in state`() {
        val vacation = sampleVacations.first()
        viewModel.selectVacation(vacation)
        assertEquals(vacation, viewModel.uiState.value.selectedVacation)
    }

    @Test
    fun `clearError removes error from state`() = runTest {
        val vacation = sampleVacations.first()
        coEvery { repository.getExcursionCountForVacation(vacation.id) } returns 1L
        viewModel.deleteVacation(vacation)
        advanceUntilIdle()

        viewModel.clearError()
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `repository failure sets error state`() = runTest {
        coEvery { repository.getAllVacations() } throws RuntimeException("DB error")
        viewModel.loadVacations()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.error != null)
        assertTrue(!viewModel.uiState.value.isLoading)
    }
}
