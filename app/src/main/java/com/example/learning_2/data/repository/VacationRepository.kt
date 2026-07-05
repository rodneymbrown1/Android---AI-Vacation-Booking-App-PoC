package com.example.learning_2.data.repository

import com.example.learning_2.entities.Vacation

interface VacationRepository {
    suspend fun getAllVacations(): List<Vacation>
    suspend fun getVacationById(id: Long): Vacation?
    suspend fun insertVacation(vacation: Vacation): Long
    suspend fun updateVacation(vacation: Vacation)
    suspend fun deleteVacation(vacation: Vacation)
    suspend fun getExcursionCountForVacation(vacationId: Long): Long
}
