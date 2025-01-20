package com.example.learning_2.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learning_2.database.AppDatabase
import com.example.learning_2.entities.Excursion
import com.example.learning_2.entities.Vacation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.learning_2.components.ShareVacationDetails

@Composable
fun VacationDetailView(database: AppDatabase, vacationId: Int = 0, navController: NavController) {
    val vacationDao = database.vacationDao()
    val excursionDao = database.excursionDao()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Vacation state
    var vacation by remember { mutableStateOf<Vacation?>(null) }
    var title by remember { mutableStateOf("") }
    var hotel by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    // Excursion state
    var excursions by remember { mutableStateOf<List<Excursion>>(emptyList()) }
    var isEditingExcursion by remember { mutableStateOf(false) }
    var currentExcursion by remember { mutableStateOf<Excursion?>(null) }
    var excursionName by remember { mutableStateOf("") }
    var excursionDescription by remember { mutableStateOf("") }
    var excursionDate by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Generate vacation details for sharing
    fun getVacationDetails(): String {
        val excursionDetails = if (excursions.isNotEmpty()) {
            excursions.joinToString("\n") { "- ${it.name}: ${it.description} (${it.date})" }
        } else {
            "No excursions available."
        }

        return """
            Vacation Details:
            Title: ${vacation?.title ?: "N/A"}
            Hotel: ${vacation?.hotel ?: "N/A"}
            Start Date: ${vacation?.startDate ?: "N/A"}
            End Date: ${vacation?.endDate ?: "N/A"}
            
            Excursions:
            $excursionDetails
        """.trimIndent()
    }

    // Load vacation and excursions from the database
    LaunchedEffect(vacationId) {
        withContext(Dispatchers.IO) {
            val fetchedVacation = vacationDao.getById(vacationId)
            val fetchedExcursions = excursionDao.getExcursionsForVacation(vacationId)

            withContext(Dispatchers.Main) {
                vacation = fetchedVacation
                vacation?.let {
                    title = it.title
                    hotel = it.hotel
                    startDate = it.startDate
                    endDate = it.endDate
                }
                excursions = fetchedExcursions
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = if (vacation == null) "Add Vacation" else "Edit Vacation",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Fields for Vacation
        LabeledInputField(label = "Title", value = title, onValueChange = { title = it })
        LabeledInputField(label = "Hotel/Place to Stay", value = hotel, onValueChange = { hotel = it })
        LabeledInputField(label = "Start Date", value = startDate, onValueChange = { startDate = it })
        LabeledInputField(label = "End Date", value = endDate, onValueChange = { endDate = it })

        Spacer(modifier = Modifier.height(16.dp))

        // Excursion List
        Text("Excursions", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        excursions.forEach { excursion ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Name: ${excursion.name}", style = MaterialTheme.typography.bodyMedium)
                    Text("Description: ${excursion.description}", style = MaterialTheme.typography.bodySmall)
                    Text("Date: ${excursion.date}", style = MaterialTheme.typography.bodySmall)
                }
                Row {
                    TextButton(onClick = {
                        isEditingExcursion = true
                        currentExcursion = excursion
                        excursionName = excursion.name
                        excursionDescription = excursion.description
                        excursionDate = excursion.date
                    }) {
                        Text("Edit")
                    }
                    TextButton(onClick = {
                        scope.launch {
                            excursionDao.delete(excursion)
                            excursions = excursionDao.getExcursionsForVacation(vacationId)
                        }
                    }) {
                        Text("Delete")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add/Update Excursion Form
        Text(
            text = if (isEditingExcursion) "Edit Excursion" else "Add New Excursion",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        LabeledInputField(label = "Excursion Name", value = excursionName, onValueChange = { excursionName = it })
        LabeledInputField(label = "Excursion Description", value = excursionDescription, onValueChange = { excursionDescription = it })
        LabeledInputField(label = "Excursion Date (YYYY-MM-DD)", value = excursionDate, onValueChange = { excursionDate = it })

        Row() {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    // Validate date format
                    if (!isDateValid(excursionDate)) {
                        errorMessage = "Invalid date format. Use YYYY-MM-DD."
                        showErrorDialog = true
                        return@Button
                    }

                    // Validate date range
                    if (!isDateWithinRange(excursionDate, startDate, endDate)) {
                        errorMessage = "Excursion date must be within the vacation period: $startDate to $endDate."
                        showErrorDialog = true
                        return@Button
                    }

                    scope.launch {
                        try {
                            withContext(Dispatchers.IO) {
                                if (isEditingExcursion && currentExcursion != null) {
                                    excursionDao.update(
                                        currentExcursion!!.copy(
                                            name = excursionName,
                                            description = excursionDescription,
                                            date = excursionDate
                                        )
                                    )
                                } else {
                                    excursionDao.insertAll(
                                        Excursion(
                                            id = 0, // Auto-generated by the database
                                            name = excursionName,
                                            description = excursionDescription,
                                            date = excursionDate,
                                            vacationId = vacationId
                                        )
                                    )
                                }
                                excursions = excursionDao.getExcursionsForVacation(vacationId)
                            }
                            Toast.makeText(context, "Excursion saved successfully!", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(context, "Error saving excursion.", Toast.LENGTH_SHORT).show()
                        }
                        isEditingExcursion = false
                        currentExcursion = null
                        excursionName = ""
                        excursionDescription = ""
                        excursionDate = ""
                    }
                },
//                modifier = Modifier.align(Alignment.End)
            ) {
                Text(if (isEditingExcursion) "Update Excursion" else "Add Excursion")
            }

            // Error Dialog
            if (showErrorDialog) {
                AlertDialog(
                    onDismissRequest = { showErrorDialog = false },
                    confirmButton = {
                        TextButton(onClick = { showErrorDialog = false }) {
                            Text("OK")
                        }
                    },
                    title = { Text("Validation Error") },
                    text = { Text(errorMessage) }
                )
            }

// Generate the excursion details as a list of strings
            val excursionDetails = excursions.map { "${it.name}: ${it.description} (${it.date})" }

// Pass to ShareVacationDetails
            ShareVacationDetails(
                vacationTitle = vacation?.title ?: "Vacation",
                vacationDetails = """
        Title: ${vacation?.title ?: "N/A"}
        Hotel: ${vacation?.hotel ?: "N/A"}
        Start Date: ${vacation?.startDate ?: "N/A"}
        End Date: ${vacation?.endDate ?: "N/A"}
    """.trimIndent(),
                excursionDetails = excursionDetails,
                context = context,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row() {
            // Back Button
            Button(
                onClick = { navController.popBackStack() },
            ) {
                Text("Back")
            }
        }
    }



}

// Helper function to validate the date format
fun isDateValid(date: String): Boolean {
    val regex = Regex("^\\d{4}-\\d{2}-\\d{2}$")
    return regex.matches(date)
}

// Helper function to validate if a date is within a given range
fun isDateWithinRange(date: String, startDate: String, endDate: String): Boolean {
    return try {
        val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val excursionDate = java.time.LocalDate.parse(date, formatter)
        val vacationStart = java.time.LocalDate.parse(startDate, formatter)
        val vacationEnd = java.time.LocalDate.parse(endDate, formatter)

        !excursionDate.isBefore(vacationStart) && !excursionDate.isAfter(vacationEnd)
    } catch (e: Exception) {
        false
    }


}

