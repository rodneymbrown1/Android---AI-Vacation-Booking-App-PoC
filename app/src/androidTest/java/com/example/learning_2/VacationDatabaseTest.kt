package com.example.learning_2

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.learning_2.dao.ExcursionDao
import com.example.learning_2.dao.VacationDao
import com.example.learning_2.database.AppDatabase
import com.example.learning_2.database.AppDatabaseProvider
import com.example.learning_2.entities.Excursion
import com.example.learning_2.entities.Vacation
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
@RunWith(AndroidJUnit4::class)
class VacationDatabaseTest {

    private lateinit var database: AppDatabase
    private lateinit var vacationDao: VacationDao
    private lateinit var excursionDao: ExcursionDao

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // For testing purposes
            .build()
        vacationDao = database.vacationDao()
        excursionDao = database.excursionDao()

        // Preload data explicitly
        runBlocking {
            preloadDatabase()
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    private suspend fun preloadDatabase() {
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
                id = 1,
                name = "Snorkeling",
                description = "Explore coral reefs in Hawaii",
                date = "2025-06-02",
                vacationId = vacationId1
            ),
            Excursion(
                id = 2,
                name = "Hiking",
                description = "Trail to the volcano peak",
                date = "2025-06-04",
                vacationId = vacationId1
            ),
            Excursion(
                id = 3,
                name = "Broadway Show",
                description = "Watch a live Broadway performance",
                date = "2025-09-16",
                vacationId = vacationId2
            ),
            Excursion(
                id = 4,
                name = "Statue of Liberty Tour",
                description = "Visit the iconic Statue of Liberty",
                date = "2025-09-17",
                vacationId = vacationId2
            )
        )

        // Insert excursions
        excursionDao.insertAll(*excursions.toTypedArray())
    }

    @Test
    fun testPreloadedVacations() = runBlocking {
        val vacations = vacationDao.getAll()
        assertEquals(2, vacations.size)
        assertNotNull(vacations.find { it.title == "Hawaii Getaway" })
        assertNotNull(vacations.find { it.title == "New York Adventure" })
    }

    @Test
    fun testPreloadedExcursions() = runBlocking {
        val vacation1 = vacationDao.getAll().find { it.title == "Hawaii Getaway" }!!
        val excursions1 = excursionDao.getExcursionsForVacation(vacation1.id)
        assertEquals(2, excursions1.size)
        assertNotNull(excursions1.find { it.name == "Snorkeling" })
        assertNotNull(excursions1.find { it.name == "Hiking" })
    }
}

