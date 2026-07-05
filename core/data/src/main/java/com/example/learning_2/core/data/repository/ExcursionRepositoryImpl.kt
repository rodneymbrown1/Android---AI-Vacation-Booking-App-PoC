package com.example.learning_2.core.data.repository

import com.example.learning_2.core.common.AppError
import com.example.learning_2.core.database.dao.ExcursionDao
import com.example.learning_2.core.database.entity.Excursion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

class ExcursionRepositoryImpl @Inject constructor(
    private val excursionDao: ExcursionDao
) : ExcursionRepository {

    override fun observeExcursionsForVacation(vacationId: Long): Flow<List<Excursion>> =
        excursionDao.observeExcursionsForVacation(vacationId).catch { throw AppError.Database(it) }

    override suspend fun insertExcursion(excursion: Excursion): Long = dbCall {
        excursionDao.insertAll(excursion).first()
    }

    override suspend fun updateExcursion(excursion: Excursion) = dbCall {
        excursionDao.update(excursion)
    }

    override suspend fun deleteExcursion(excursion: Excursion) = dbCall {
        excursionDao.delete(excursion)
    }
}
