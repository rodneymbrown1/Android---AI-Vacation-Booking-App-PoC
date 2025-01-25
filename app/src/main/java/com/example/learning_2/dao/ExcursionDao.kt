package com.example.learning_2.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.learning_2.entities.Excursion

@Dao
interface ExcursionDao {

    // Get all excursions
    @Query("SELECT * FROM Excursion")
    suspend fun getAll(): List<Excursion>

    // Get excursions by IDs
    @Query("SELECT * FROM Excursion WHERE id IN (:excursionIds)")
    suspend fun loadAllByIds(excursionIds: IntArray): List<Excursion>

    // Find an excursion by name
    @Query("SELECT * FROM Excursion WHERE name LIKE :name LIMIT 1")
    suspend fun findByName(name: String): Excursion?

    @Query("SELECT * FROM Excursion WHERE vacation_id = :vacationId")
    suspend fun getExcursionsForVacation(vacationId: Int): List<Excursion>

    // Insert multiple excursions
    @Insert
    suspend fun insertAll(vararg excursions: Excursion)

    // Delete an excursion
    @Delete
    suspend fun delete(excursion: Excursion)

    // Update a specific Excursion
    @Update
    suspend fun update(excursion: Excursion)
}
