package com.example.learning_2.data.repository

import com.example.learning_2.dao.VacationDao
import com.example.learning_2.entities.Vacation
import javax.inject.Inject

class VacationRepositoryImpl @Inject constructor(
    private val vacationDao: VacationDao
) : VacationRepository {

    override suspend fun getAllVacations(): List<Vacation> =
        vacationDao.getAll()

    override suspend fun getVacationById(id: Long): Vacation? =
        vacationDao.getById(id)

    override suspend fun insertVacation(vacation: Vacation): Long =
        vacationDao.insertAll(vacation).first()

    override suspend fun updateVacation(vacation: Vacation) =
        vacationDao.update(vacation)

    override suspend fun deleteVacation(vacation: Vacation) =
        vacationDao.delete(vacation)

    override suspend fun getExcursionCountForVacation(vacationId: Long): Long =
        vacationDao.getExcursionCountForVacation(vacationId)
}
