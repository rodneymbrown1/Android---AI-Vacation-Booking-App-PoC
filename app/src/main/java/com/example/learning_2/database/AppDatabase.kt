package com.example.learning_2.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.learning_2.dao.ExcursionDao
import com.example.learning_2.dao.VacationDao
import com.example.learning_2.entities.Excursion
import com.example.learning_2.entities.Vacation

@Database(entities = [Vacation::class, Excursion::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vacationDao(): VacationDao
    abstract fun excursionDao(): ExcursionDao
}
