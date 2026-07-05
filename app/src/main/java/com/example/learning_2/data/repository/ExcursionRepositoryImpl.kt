package com.example.learning_2.data.repository

import com.example.learning_2.dao.ExcursionDao
import com.example.learning_2.entities.Excursion
import javax.inject.Inject

class ExcursionRepositoryImpl @Inject constructor(
    private val excursionDao: ExcursionDao
) : ExcursionRepository {

    override suspend fun getExcursionsForVacation(vacationId: Long): List<Excursion> =
        excursionDao.getExcursionsForVacation(vacationId)

    override suspend fun insertExcursion(excursion: Excursion): Long =
        excursionDao.insertAll(excursion).first()

    override suspend fun updateExcursion(excursion: Excursion) =
        excursionDao.update(excursion)

    override suspend fun deleteExcursion(excursion: Excursion) =
        excursionDao.delete(excursion)
}
