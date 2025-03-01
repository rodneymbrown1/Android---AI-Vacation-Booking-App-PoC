package com.example.learning_2.tests

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.learning_2.database.AppDatabase
import com.example.learning_2.dao.VacationDao
import com.example.learning_2.dao.ExcursionDao
import com.example.learning_2.entities.Vacation
import com.example.learning_2.entities.Excursion
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executors

class DatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var vacationDao: VacationDao
    private lateinit var excursionDao: ExcursionDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase::class.java
        ).setTransactionExecutor(Executors.newSingleThreadExecutor())
                .allowMainThreadQueries()
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
        // Insert a vacation
        val vacation = Vacation(0, "Hawaii Trip", "Beach Resort", "2025-07-01", "2025-07-10")
        vacationDao.insertAll(vacation)

        // Fetch the inserted vacation
        val fetchedVacation = vacationDao.getById(1)
        assertNotNull(fetchedVacation)
        assertEquals("Hawaii Trip", fetchedVacation?.title)
        assertEquals("Beach Resort", fetchedVacation?.hotel)
        assertEquals("2025-07-01", fetchedVacation?.startDate)
        assertEquals("2025-07-10", fetchedVacation?.endDate)
    }

    @Test
    fun testInsertAndFetchExcursions() = runBlocking {
        // Insert a vacation
        val vacation = Vacation(0, "Mountain Retreat", "Summit Lodge", "2025-08-05", "2025-08-12")
        vacationDao.insertAll(vacation)

        // Fetch the vacation ID
        val insertedVacation = vacationDao.findByName("Mountain Retreat")
        assertNotNull(insertedVacation)
        val vacationId = insertedVacation!!.id

        // Insert excursions
        val excursion1 = Excursion(0, "Hiking Tour", "Explore the mountain trails", "2025-08-06", vacationId)
        val excursion2 = Excursion(0, "Lake Boating", "Enjoy a scenic boat ride", "2025-08-07", vacationId)
        excursionDao.insertAll(excursion1, excursion2)

        // Fetch excursions for vacation ID
        val excursions = excursionDao.getExcursionsForVacation(vacationId)
        assertEquals(2, excursions.size)
        assertEquals("Hiking Tour", excursions[0].name)
        assertEquals("Lake Boating", excursions[1].name)
    }

    @Test
    fun testUpdateVacation() = runBlocking {
        // Insert a vacation
        val vacation = Vacation(0, "Beach Trip", "Seaside Resort", "2025-09-01", "2025-09-07")
        vacationDao.insertAll(vacation)

        // Fetch the vacation ID
        val insertedVacation = vacationDao.findByName("Beach Trip")
        assertNotNull(insertedVacation)
        val vacationId = insertedVacation!!.id

        // Update vacation title
        val updatedVacation = insertedVacation.copy(title = "Updated Beach Trip")
        vacationDao.update(updatedVacation)

        // Fetch updated vacation
        val fetchedVacation = vacationDao.getById(vacationId)
        assertNotNull(fetchedVacation)
        assertEquals("Updated Beach Trip", fetchedVacation?.title)
    }

    @Test
    fun testDeleteExcursion() = runBlocking {
        // Insert a vacation
        val vacation = Vacation(0, "Desert Safari", "Sands Hotel", "2025-10-01", "2025-10-05")
        vacationDao.insertAll(vacation)

        // Fetch vacation ID
        val insertedVacation = vacationDao.findByName("Desert Safari")
        assertNotNull(insertedVacation)
        val vacationId = insertedVacation!!.id

        // Insert an excursion
        val excursion = Excursion(0, "Camel Ride", "Experience the desert on camelback", "2025-10-02", vacationId)
        excursionDao.insertAll(excursion)

        // Verify excursion is inserted
        val excursionsBeforeDelete = excursionDao.getExcursionsForVacation(vacationId)
        assertEquals(1, excursionsBeforeDelete.size)

        // Delete excursion
        excursionDao.delete(excursion)

        // Verify excursion is deleted
        val excursionsAfterDelete = excursionDao.getExcursionsForVacation(vacationId)
        assertEquals(0, excursionsAfterDelete.size)
    }
}
