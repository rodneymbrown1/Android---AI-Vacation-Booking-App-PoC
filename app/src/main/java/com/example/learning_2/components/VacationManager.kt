package com.example.learning_2.components

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.learning_2.database.AppDatabase
import com.example.learning_2.entities.Vacation
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VacationManager(database: AppDatabase, onNext: (Int) -> Unit) {
    val vacationDao = database.vacationDao()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var vacations by remember { mutableStateOf<List<Vacation>>(emptyList()) }
    var selectedVacation by remember { mutableStateOf<Vacation?>(null) }
    var title by remember { mutableStateOf("") }
    var hotel by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        vacations = vacationDao.getAll()
    }

    fun isDateValid(date: String): Boolean {
        val regex = Regex("^\\d{4}-\\d{2}-\\d{2}$")
        return regex.matches(date)
    }

    fun isEndDateAfterStartDate(): Boolean {
        return try {
            val start = LocalDate.parse(startDate)
            val end = LocalDate.parse(endDate)
            end.isAfter(start)
        } catch (e: Exception) {
            false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Vacation Manager", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = hotel,
            onValueChange = { hotel = it },
            label = { Text("Hotel/Place to Stay") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = startDate,
            onValueChange = { startDate = it },
            label = { Text("Start Date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth(),
            isError = startDate.isNotEmpty() && !isDateValid(startDate)
        )
        if (startDate.isNotEmpty() && !isDateValid(startDate)) {
            Text("Invalid start date format. Use YYYY-MM-DD.", color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = endDate,
            onValueChange = { endDate = it },
            label = { Text("End Date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth(),
            isError = endDate.isNotEmpty() && (!isDateValid(endDate) || (startDate.isNotEmpty() && !isEndDateAfterStartDate()))
        )
        if (endDate.isNotEmpty() && !isDateValid(endDate)) {
            Text("Invalid end date format. Use YYYY-MM-DD.", color = MaterialTheme.colorScheme.error)
        } else if (startDate.isNotEmpty() && endDate.isNotEmpty() && !isEndDateAfterStartDate()) {
            Text("End date must be after the start date.", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Existing Vacations
        Text("Existing Vacations", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        vacations.forEach { vacation ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Title: ${vacation.title}", style = MaterialTheme.typography.bodyMedium)
                    Text("Hotel: ${vacation.hotel}", style = MaterialTheme.typography.bodySmall)
                    Text("Dates: ${vacation.startDate} - ${vacation.endDate}", style = MaterialTheme.typography.bodySmall)
                }
                Row {
                    TextButton(onClick = {
                        selectedVacation = vacation
                        title = vacation.title
                        hotel = vacation.hotel
                        startDate = vacation.startDate
                        endDate = vacation.endDate
                    }) {
                        Text("Edit")
                    }
                    TextButton(onClick = {
                        scope.launch {
                            val excursionCount = vacationDao.getExcursionCountForVacation(vacation.id)
                            if (excursionCount > 0) {
                                Toast.makeText(
                                    context,
                                    "Cannot delete vacation with associated excursions.",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                vacationDao.delete(vacation)
                                vacations = vacationDao.getAll()
                            }
                        }
                    }) {
                        Text("Delete")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add/Update and Next Buttons
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                if (!isDateValid(startDate) || !isDateValid(endDate)) {
                    Toast.makeText(context, "Please enter valid dates in YYYY-MM-DD format.", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (!isEndDateAfterStartDate()) {
                    Toast.makeText(context, "End date must be after the start date.", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                scope.launch {
                    if (selectedVacation == null) {
                        val newVacation = Vacation(
                            id = 0,
                            title = title,
                            hotel = hotel,
                            startDate = startDate,
                            endDate = endDate
                        )
                        vacationDao.insertAll(newVacation)
                    } else {
                        val updatedVacation = selectedVacation!!.copy(
                            title = title,
                            hotel = hotel,
                            startDate = startDate,
                            endDate = endDate
                        )
                        vacationDao.update(updatedVacation)
                    }
                    vacations = vacationDao.getAll()
                    selectedVacation = null
                    title = ""
                    hotel = ""
                    startDate = ""
                    endDate = ""
                }
            }) {
                Text(if (selectedVacation == null) "Add Vacation" else "Update Vacation")
            }

            if (selectedVacation != null) {
                Button(
                    onClick = { selectedVacation?.id?.let { onNext(it) } }
                ) {
                    Text("Next")
                }
            }
        }
    }
}

