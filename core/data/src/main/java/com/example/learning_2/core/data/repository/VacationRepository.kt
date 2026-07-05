package com.example.learning_2.core.data.repository

import com.example.learning_2.core.database.entity.Vacation
import kotlinx.coroutines.flow.Flow

interface VacationRepository {
    fun observeVacations(): Flow<List<Vacation>>
    suspend fun getVacationById(id: Long): Vacation?
    suspend fun insertVacation(vacation: Vacation): Long
    suspend fun updateVacation(vacation: Vacation)
    suspend fun deleteVacation(vacation: Vacation)
    suspend fun getExcursionCountForVacation(vacationId: Long): Long
}
