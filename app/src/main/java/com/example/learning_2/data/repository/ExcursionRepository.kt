package com.example.learning_2.data.repository

import com.example.learning_2.entities.Excursion

interface ExcursionRepository {
    suspend fun getExcursionsForVacation(vacationId: Long): List<Excursion>
    suspend fun insertExcursion(excursion: Excursion): Long
    suspend fun updateExcursion(excursion: Excursion)
    suspend fun deleteExcursion(excursion: Excursion)
}
