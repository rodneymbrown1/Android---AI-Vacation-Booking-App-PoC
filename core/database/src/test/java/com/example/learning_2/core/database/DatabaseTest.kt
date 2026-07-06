package com.example.learning_2.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.learning_2.core.database.dao.ExcursionDao
import com.example.learning_2.core.database.dao.VacationDao
import com.example.learning_2.core.database.entity.Excursion
import com.example.learning_2.core.database.entity.Vacation
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.concurrent.Executors

@Config(manifest = Config.NONE)
@RunWith(AndroidJUnit4::class)
@LargeTest
class DatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var vacationDao: VacationDao
    private lateinit var excursionDao: ExcursionDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries() // Only for testing
            .setTransactionExecutor(Executors.newSingleThreadExecutor()) // Ensures transactions work in tests
            .build()

        vacationDao = db.vacationDao()
        excursionDao = db.excursionDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun testInsertAndFetchVacation() = runBlocking {
        val vacation = Vacation(0, "Hawaii Trip", "Beach Resort", "2025-07-01", "2025-07-10")
        vacationDao.insertAll(vacation)

        val fetchedVacation = vacationDao.getById(1)
        assertNotNull(fetchedVacation)
        assertEquals("Hawaii Trip", fetchedVacation?.title)
        assertEquals("Beach Resort", fetchedVacation?.hotel)
    }

    @Test
    fun testInsertAndFetchExcursions() = runBlocking {
        val vacation = Vacation(0, "Mountain Retreat", "Summit Lodge", "2025-08-05", "2025-08-12")
        vacationDao.insertAll(vacation)

        val insertedVacation = vacationDao.findByName("Mountain Retreat")
        assertNotNull(insertedVacation)
        val vacationId = insertedVacation!!.id

        val excursion1 = Excursion(0, "Hiking Tour", "Explore the mountain trails", "2025-08-06", vacationId)
        val excursion2 = Excursion(0, "Lake Boating", "Enjoy a scenic boat ride", "2025-08-07", vacationId)
        excursionDao.insertAll(excursion1, excursion2)

        val excursions = excursionDao.getExcursionsForVacation(vacationId)
        assertEquals(2, excursions.size)
    }

    @Test
    fun testUpdateVacation() = runBlocking {
        val vacation = Vacation(0, "Beach Trip", "Seaside Resort", "2025-09-01", "2025-09-07")
        vacationDao.insertAll(vacation)

        val insertedVacation = vacationDao.findByName("Beach Trip")
        assertNotNull(insertedVacation)
        val vacationId = insertedVacation!!.id

        val updatedVacation = insertedVacation.copy(title = "Updated Beach Trip")
        vacationDao.update(updatedVacation)

        val fetchedVacation = vacationDao.getById(vacationId)
        assertNotNull(fetchedVacation)
        assertEquals("Updated Beach Trip", fetchedVacation?.title)
    }

    @Test
    fun testDeleteExcursion() = runBlocking {
        // Insert a vacation
        val vacation = Vacation(0, "Desert Safari", "Sands Hotel", "2025-10-01", "2025-10-05")
        val vacationId = vacationDao.insertAll(vacation)[0]

        // Insert multiple excursions
        excursionDao.insertAll(
            Excursion(0, "Camel Ride", "Desert experience", "2025-10-02", vacationId),
            Excursion(0, "ATV Adventure", "Ride through dunes", "2025-10-03", vacationId)
        )

        // Fetch all excursions and pick one randomly
        val excursions = excursionDao.getExcursionsForVacation(vacationId)
        assert(excursions.isNotEmpty()) { "No excursions found!" }
        val randomExcursion = excursions.random()

        // Delete the random excursion
        excursionDao.delete(randomExcursion)

        // Verify it's deleted
        val remainingExcursions = excursionDao.getExcursionsForVacation(vacationId)
        assert(!remainingExcursions.contains(randomExcursion)) { "Excursion was not deleted!" }
    }
}
