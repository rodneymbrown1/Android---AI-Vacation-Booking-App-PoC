package com.example.learning_2.core.data.repository

import com.example.learning_2.core.common.AppError
import com.example.learning_2.core.database.dao.VacationDao
import com.example.learning_2.core.database.entity.Vacation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

class VacationRepositoryImpl @Inject constructor(
    private val vacationDao: VacationDao
) : VacationRepository {

    override fun observeVacations(): Flow<List<Vacation>> =
        vacationDao.observeAll().catch { throw AppError.Database(it) }

    override suspend fun getVacationById(id: Long): Vacation? = dbCall {
        vacationDao.getById(id)
    }

    override suspend fun insertVacation(vacation: Vacation): Long = dbCall {
        vacationDao.insertAll(vacation).first()
    }

    override suspend fun updateVacation(vacation: Vacation) = dbCall {
        vacationDao.update(vacation)
    }

    override suspend fun deleteVacation(vacation: Vacation) = dbCall {
        vacationDao.delete(vacation)
    }

    override suspend fun getExcursionCountForVacation(vacationId: Long): Long = dbCall {
        vacationDao.getExcursionCountForVacation(vacationId)
    }
}
