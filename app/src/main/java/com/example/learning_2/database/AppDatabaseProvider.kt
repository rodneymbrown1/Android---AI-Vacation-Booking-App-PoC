package com.example.learning_2.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.learning_2.entities.Excursion
import com.example.learning_2.entities.Vacation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AppDatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "vacation_database"
            )
                .addCallback(DatabaseCallback(context))
                .fallbackToDestructiveMigration() // Reset data on schema change
                .build()
            INSTANCE = instance
            instance
        }
    }

    class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            populateDatabase(context)
        }

        private fun populateDatabase(context: Context) {
            val database = getDatabase(context)
            val vacationDao = database.vacationDao()
            val excursionDao = database.excursionDao()

            CoroutineScope(Dispatchers.IO).launch {
                // Predefined vacations
                val vacation1 = Vacation(
                    title = "Hawaii Getaway",
                    hotel = "Ocean View Resort",
                    startDate = "2025-06-01",
                    endDate = "2025-06-07"
                )
                val vacation2 = Vacation(
                    title = "New York Adventure",
                    hotel = "Grand Manhattan Hotel",
                    startDate = "2025-09-15",
                    endDate = "2025-09-20"
                )

                // Insert vacations
                val vacationId1 = vacationDao.insertAll(vacation1)[0].toInt()
                val vacationId2 = vacationDao.insertAll(vacation2)[0].toInt()

                // Predefined excursions
                val excursions = listOf(
                    Excursion(
                        name = "Snorkeling",
                        description = "Explore coral reefs in Hawaii",
                        date = "2025-06-02",
                        vacationId = vacationId1.toLong()
                    ),
                    Excursion(
                        name = "Hiking",
                        description = "Trail to the volcano peak",
                        date = "2025-06-04",
                        vacationId = vacationId1.toLong()
                    ),
                    Excursion(
                        name = "Broadway Show",
                        description = "Watch a live Broadway performance",
                        date = "2025-09-16",
                        vacationId = vacationId2.toLong()
                    ),
                    Excursion(
                        name = "Statue of Liberty Tour",
                        description = "Visit the iconic Statue of Liberty",
                        date = "2025-09-17",
                        vacationId = vacationId2.toLong()
                    )
                )

                // Insert excursions
                excursionDao.insertAll(*excursions.toTypedArray())
            }
        }
    }
}
