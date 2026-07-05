package com.example.learning_2.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.learning_2.core.database.dao.ExcursionDao
import com.example.learning_2.core.database.dao.VacationDao
import com.example.learning_2.core.database.entity.Excursion
import com.example.learning_2.core.database.entity.Vacation

@Database(entities = [Vacation::class, Excursion::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vacationDao(): VacationDao
    abstract fun excursionDao(): ExcursionDao
}
