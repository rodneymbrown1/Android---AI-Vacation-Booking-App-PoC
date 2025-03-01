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
import com.example.learning_2.components.HTTP.OpenAIConnection
import java.util.concurrent.Executors

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
    var openAIexcursions by remember { mutableStateOf<List<Excursion>>(emptyList()) }
    var vacationExcursions by remember { mutableStateOf<List<Excursion>>(emptyList()) }
    var isEditingExcursion by remember { mutableStateOf(false) }
    var currentExcursion by remember { mutableStateOf<Excursion?>(null) }
    var excursionName by remember { mutableStateOf("") }
    var excursionDescription by remember { mutableStateOf("") }
    var excursionDate by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var apiResponse by remember { mutableStateOf("Fetching API data...") }
    var selectedExcursion by remember { mutableStateOf<Excursion?>(null) }
    val onSelectionChange: (Excursion) -> Unit = { excursion ->
        selectedExcursion = excursion
        excursionName = excursion.name
        excursionDescription = excursion.description
        excursionDate = excursion.date
    }
    // Define the onEdit callback variable
    val onEdit: (Excursion) -> Unit = { excursion ->
        isEditingExcursion = true
        currentExcursion = excursion
        excursionName = excursion.name
        excursionDescription = excursion.description
        excursionDate = excursion.date
    }
    // Define the onDelete callback variable
    val onDelete: (Excursion) -> Unit = { excursion ->
        scope.launch {
            excursionDao.delete(excursion)
            // Refresh the excursions list after deletion
            excursions = excursionDao.getExcursionsForVacation(vacationId)
        }
    }
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
//========================================================================================================================
// Load vacation and excursions from the database
    LaunchedEffect(vacationId) {
        withContext(Dispatchers.IO) {
            val fetchedVacation = vacationDao.getById(vacationId)
            val fetchedExcursions = excursionDao.getExcursionsForVacation(vacationId)
            vacationExcursions = fetchedExcursions

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
//========================================================================================================================
// OPEN AI RESPONSE
    LaunchedEffect(title, hotel, startDate, endDate) {
        if (title.isNotBlank() && hotel.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank()) {
            scope.launch(Dispatchers.IO) {
                val promptJson = """
                {
                    "role": "system",
                    "content": "You are a helpful assistant that generates vacation excursion plans."
                },
                {
                    "role": "user",
                    "content": "Generate 3 unique excursions for a vacation titled '$title' at '$hotel' from $startDate to $endDate. 
                    Each excursion should have:
                    - Name
                    - Description
                    - A date within the vacation period.

                    Return the response strictly as a valid JSON array, following this format:
                    [
                        {"name": "Excursion Name", "description": "Excursion Description", "date": "YYYY-MM-DD"},
                        {"name": "Excursion Name", "description": "Excursion Description", "date": "YYYY-MM-DD"},
                        {"name": "Excursion Name", "description": "Excursion Description", "date": "YYYY-MM-DD"}
                    ]
                    Do not add any other text, only return a valid JSON array."
                }
            """.trimIndent()

                val response = OpenAIConnection.fetchOpenAIResponse(promptJson)
                System.out.println(response);
                try {
                    val jsonArray = org.json.JSONArray(response)
                    val newExcursions = mutableListOf<Excursion>()

                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        newExcursions.add(
                            Excursion(
                                id = 0,  // Database auto-generates ID
                                name = obj.getString("name"),
                                description = obj.getString("description"),
                                date = obj.getString("date"),
                                vacationId = vacationId
                            )
                        )
                    }
                    System.out.println(newExcursions)

                    withContext(Dispatchers.Main) {
                        openAIexcursions = newExcursions
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

//========================================================================================================================
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))
//========================================================================================================================
//EXCURSION DROPDOWN
        ExcursionSelectionRow(
            excursions = openAIexcursions,
            selectedExcursion = selectedExcursion,
            onSelectionChange = onSelectionChange,
            onEdit = onEdit,
            onDelete = onDelete
        )
//========================================================================================================================
//ADDED EXCURSION
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
//========================================================================================================================
//DELETE BUTTON
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
//========================================================================================================================
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

//========================================================================================================================
//UPDATE / ADD EXCURSION - BUTTON
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
//========================================================================================================================
//DATABASE
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
//========================================================================================================================
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
//========================================================================================================================
//helper functinons
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




