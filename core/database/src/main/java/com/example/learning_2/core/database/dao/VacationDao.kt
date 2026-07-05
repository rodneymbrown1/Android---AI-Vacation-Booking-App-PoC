package com.example.learning_2.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.learning_2.core.database.entity.Vacation
import kotlinx.coroutines.flow.Flow

@Dao
interface VacationDao {

    // Reactive stream of all vacations, used by screens that list/observe them.
    @Query("SELECT * FROM Vacation")
    fun observeAll(): Flow<List<Vacation>>

    // One-shot retrieval, used for AI prompt generation / non-observing reads.
    @Query("SELECT * FROM Vacation")
    suspend fun getAll(): List<Vacation>

    // Retrieve vacations by their unique IDs
    @Query("SELECT * FROM Vacation WHERE id IN (:vacationIds)")
    suspend fun loadAllByIds(vacationIds: LongArray): List<Vacation>

    @Query("SELECT * FROM Vacation WHERE id = :vacationId LIMIT 1")
    suspend fun getById(vacationId: Long): Vacation?

    // Find a vacation by its name
    @Query("SELECT * FROM Vacation WHERE title LIKE :name LIMIT 1")
    suspend fun findByName(name: String): Vacation?

    // Insert multiple vacations
    @Insert
    suspend fun insertAll(vararg vacations: Vacation): List<Long>

    // Delete a specific vacation
    @Delete
    suspend fun delete(vacation: Vacation)

    @Update
    suspend fun update(selectedVacation: Vacation)

    // Check if a vacation has associated excursions
    @Query("SELECT COUNT(*) FROM Excursion WHERE vacation_id = :vacationId")
    suspend fun getExcursionCountForVacation(vacationId: Long): Long
}
