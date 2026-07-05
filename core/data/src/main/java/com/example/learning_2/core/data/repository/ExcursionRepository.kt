package com.example.learning_2.core.data.repository

import com.example.learning_2.core.database.entity.Excursion
import kotlinx.coroutines.flow.Flow

interface ExcursionRepository {
    fun observeExcursionsForVacation(vacationId: Long): Flow<List<Excursion>>
    suspend fun insertExcursion(excursion: Excursion): Long
    suspend fun updateExcursion(excursion: Excursion)
    suspend fun deleteExcursion(excursion: Excursion)
}
