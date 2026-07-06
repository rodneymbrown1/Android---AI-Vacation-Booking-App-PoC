package com.example.learning_2.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.learning_2.core.database.entity.Excursion
import kotlinx.coroutines.flow.Flow

@Dao
interface ExcursionDao {

    // Get all excursions
    @Query("SELECT * FROM Excursion")
    suspend fun getAll(): List<Excursion>

    // Get excursions by IDs
    @Query("SELECT * FROM Excursion WHERE id IN (:excursionIds)")
    suspend fun loadAllByIds(excursionIds: LongArray): List<Excursion>

    // Find an excursion by name
    @Query("SELECT * FROM Excursion WHERE name LIKE :name LIMIT 1")
    suspend fun findByName(name: String): Excursion?

    // Reactive stream of excursions for a vacation, used by the detail screen.
    @Query("SELECT * FROM Excursion WHERE vacation_id = :vacationId")
    fun observeExcursionsForVacation(vacationId: Long): Flow<List<Excursion>>

    @Query("SELECT * FROM Excursion WHERE vacation_id = :vacationId")
    suspend fun getExcursionsForVacation(vacationId: Long): List<Excursion>

    // Insert multiple excursions
    @Insert
    suspend fun insertAll(vararg excursions: Excursion): List<Long>

    // Delete an excursion
    @Delete
    suspend fun delete(excursion: Excursion)

    // Update a specific Excursion
    @Update
    suspend fun update(excursion: Excursion)
}
