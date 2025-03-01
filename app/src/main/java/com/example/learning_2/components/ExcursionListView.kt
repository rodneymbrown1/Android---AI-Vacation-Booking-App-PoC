package com.example.learning_2.components

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.learning_2.entities.Excursion

@Composable
fun ExcursionListView(
    excursions: List<Excursion> = listOf(
        Excursion(1, "Snorkeling", "Explore underwater reefs.", "2025-06-01", vacationId = 1),
        Excursion(2, "Hiking", "Trail to the highest mountain.", "2025-06-02", vacationId = 1),
        Excursion(3, "City Tour", "Visit local attractions.", "2025-06-03", vacationId = 2)
    ),
    vacationStartDate: String,
    vacationEndDate: String,
    onAdd: (Excursion) -> Unit = {},
    onUpdate: (Excursion) -> Unit = {},
    onDelete: (Excursion) -> Unit = {}
) {
    var isEditing by remember { mutableStateOf(false) }
    var currentExcursion by remember { mutableStateOf<Excursion?>(null) }
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var vacationId by remember { mutableStateOf(0) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showAlertDialog by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

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

    Column(Modifier.padding(16.dp)) {
        Text("Excursions", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        // Excursion List
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
                    Text("Vacation ID: ${excursion.vacationId}", style = MaterialTheme.typography.bodySmall)
                }
                Row {
                    TextButton(onClick = {
                        isEditing = true
                        currentExcursion = excursion
                        name = excursion.name
                        description = excursion.description
                        date = excursion.date
                        vacationId = excursion.vacationId
                    }) {
                        Text("Edit")
                    }
                    TextButton(onClick = { onDelete(excursion) }) {
                        Text("Delete")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add/Update Form
        Text(
            text = if (isEditing) "Edit Excursion" else "Add New Excursion",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(label = { Text("Excursion Name") }, value = name, onValueChange = { name = it })
        OutlinedTextField(label = { Text("Excursion Description") }, value = description, onValueChange = { description = it })
        OutlinedTextField(
            label = { Text("Excursion Date (YYYY-MM-DD)") },
            value = date,
            onValueChange = { date = it }
        )
        OutlinedTextField(
            label = { Text("Vacation ID") },
            value = vacationId.toString(),
            onValueChange = { vacationId = it.toIntOrNull() ?: 0 }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (!isDateValid(date)) {
                    Toast.makeText(context, "Please enter valid dates in YYYY-MM-DD format.", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (!isDateWithinRange(date, vacationStartDate, vacationEndDate)) {
                    Toast.makeText(context, "Excursion date must fall within the vacation period: $vacationStartDate to $vacationEndDate.", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                alertMessage = "Alert: ${if (isEditing) "Updating" else "Adding"} Excursion: $name on $date"
                showAlertDialog = true

                if (isEditing && currentExcursion != null) {
                    onUpdate(
                        currentExcursion!!.copy(
                            name = name,
                            description = description,
                            date = date,
                            vacationId = vacationId
                        )
                    )
                } else {
                    onAdd(
                        Excursion(
                            id = excursions.size + 1,
                            name = name,
                            description = description,
                            date = date,
                            vacationId = vacationId
                        )
                    )
                }
                // Reset state
                isEditing = false
                currentExcursion = null
                name = ""
                description = ""
                date = ""
                vacationId = 0
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(if (isEditing) "Update" else "Add")
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

        // Alert Dialog
        if (showAlertDialog) {
            AlertDialog(
                onDismissRequest = { showAlertDialog = false },
                confirmButton = {
                    TextButton(onClick = { showAlertDialog = false }) {
                        Text("OK")
                    }
                },
                title = { Text("Alert") },
                text = { Text(alertMessage) }
            )
        }
    }
}



