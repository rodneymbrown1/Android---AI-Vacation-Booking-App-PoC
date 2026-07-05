package com.example.learning_2.components

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.learning_2.entities.Excursion
import com.example.learning_2.presentation.excursion.ExcursionViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun VacationDetailView(
    vacationId: Long,
    navController: NavController,
    viewModel: ExcursionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var excursionName by remember { mutableStateOf("") }
    var excursionDescription by remember { mutableStateOf("") }
    var excursionDate by remember { mutableStateOf("") }
    var isEditingExcursion by remember { mutableStateOf(false) }
    var currentExcursion by remember { mutableStateOf<Excursion?>(null) }
    var selectedAiExcursion by remember { mutableStateOf<Excursion?>(null) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf("") }

    LaunchedEffect(vacationId) {
        viewModel.loadVacationAndExcursions(vacationId)
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    val vacation = uiState.vacation
    val startDate = vacation?.startDate.orEmpty()
    val endDate = vacation?.endDate.orEmpty()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isGeneratingAi) {
            Row(modifier = Modifier.padding(bottom = 8.dp)) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generating AI excursion suggestions...")
            }
        }

        ExcursionSelectionRow(
            excursions = uiState.aiSuggestions,
            selectedExcursion = selectedAiExcursion,
            onSelectionChange = { excursion ->
                selectedAiExcursion = excursion
                excursionName = excursion.name
                excursionDescription = excursion.description
                excursionDate = excursion.date
            },
            onEdit = { excursion ->
                isEditingExcursion = true
                currentExcursion = excursion
                excursionName = excursion.name
                excursionDescription = excursion.description
                excursionDate = excursion.date
            },
            onDelete = { excursion -> viewModel.deleteExcursion(excursion, vacationId) }
        )

        uiState.excursions.forEach { excursion ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
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
                    }) { Text("Edit") }
                    TextButton(onClick = { viewModel.deleteExcursion(excursion, vacationId) }) { Text("Delete") }
                }
            }
        }

        Text(
            text = if (isEditingExcursion) "Edit Excursion" else "Add New Excursion",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        LabeledInputField(label = "Excursion Name", value = excursionName, onValueChange = { excursionName = it })
        LabeledInputField(label = "Description", value = excursionDescription, onValueChange = { excursionDescription = it })
        LabeledInputField(label = "Date (YYYY-MM-DD)", value = excursionDate, onValueChange = { excursionDate = it })

        Row {
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                if (!isDateValid(excursionDate)) {
                    validationError = "Invalid date format. Use YYYY-MM-DD."
                    showErrorDialog = true
                    return@Button
                }
                if (!isDateWithinRange(excursionDate, startDate, endDate)) {
                    validationError = "Excursion date must be within the vacation period: $startDate to $endDate."
                    showErrorDialog = true
                    return@Button
                }
                if (isEditingExcursion && currentExcursion != null) {
                    viewModel.updateExcursion(
                        currentExcursion!!.copy(name = excursionName, description = excursionDescription, date = excursionDate),
                        vacationId
                    )
                } else {
                    viewModel.addExcursion(excursionName, excursionDescription, excursionDate, vacationId)
                }
                isEditingExcursion = false
                currentExcursion = null
                excursionName = ""; excursionDescription = ""; excursionDate = ""
                Toast.makeText(context, "Excursion saved.", Toast.LENGTH_SHORT).show()
            }) {
                Text(if (isEditingExcursion) "Update Excursion" else "Add Excursion")
            }

            if (showErrorDialog) {
                AlertDialog(
                    onDismissRequest = { showErrorDialog = false },
                    confirmButton = { TextButton(onClick = { showErrorDialog = false }) { Text("OK") } },
                    title = { Text("Validation Error") },
                    text = { Text(validationError) }
                )
            }

            val excursionDetails = uiState.excursions.map { "${it.name}: ${it.description} (${it.date})" }
            ShareVacationDetails(
                vacationTitle = vacation?.title ?: "Vacation",
                vacationDetails = """
                    Title: ${vacation?.title ?: "N/A"}
                    Hotel: ${vacation?.hotel ?: "N/A"}
                    Start Date: $startDate
                    End Date: $endDate
                """.trimIndent(),
                excursionDetails = excursionDetails,
                context = context
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }) { Text("Back") }
    }
}

fun isDateValid(date: String): Boolean = Regex("^\\d{4}-\\d{2}-\\d{2}$").matches(date)

fun isDateWithinRange(date: String, startDate: String, endDate: String): Boolean = try {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val d = LocalDate.parse(date, formatter)
    val s = LocalDate.parse(startDate, formatter)
    val e = LocalDate.parse(endDate, formatter)
    !d.isBefore(s) && !d.isAfter(e)
} catch (ex: Exception) { false }
