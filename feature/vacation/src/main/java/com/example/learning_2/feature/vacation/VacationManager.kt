package com.example.learning_2.feature.vacation

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.learning_2.core.common.DateValidator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VacationManager(
    onNavigateToDetail: (Long) -> Unit,
    viewModel: VacationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var hotel by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    LaunchedEffect(uiState.selectedVacation) {
        uiState.selectedVacation?.let {
            title = it.title
            hotel = it.hotel
            startDate = it.startDate
            endDate = it.endDate
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    fun isEndAfterStart(): Boolean = DateValidator.isAfter(endDate, startDate)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Vacation Manager", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = hotel, onValueChange = { hotel = it }, label = { Text("Hotel / Place to Stay") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = startDate,
            onValueChange = { startDate = it },
            label = { Text("Start Date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth(),
            isError = startDate.isNotEmpty() && !DateValidator.isValidFormat(startDate)
        )
        if (startDate.isNotEmpty() && !DateValidator.isValidFormat(startDate)) {
            Text("Invalid format. Use YYYY-MM-DD.", color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = endDate,
            onValueChange = { endDate = it },
            label = { Text("End Date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth(),
            isError = endDate.isNotEmpty() && (!DateValidator.isValidFormat(endDate) || (startDate.isNotEmpty() && !isEndAfterStart()))
        )
        if (endDate.isNotEmpty() && startDate.isNotEmpty() && DateValidator.isValidFormat(endDate) && !isEndAfterStart()) {
            Text("End date must be after start date.", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(8.dp))
        }

        Text("Existing Vacations", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        uiState.vacations.forEach { vacation ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Title: ${vacation.title}", style = MaterialTheme.typography.bodyMedium)
                    Text("Hotel: ${vacation.hotel}", style = MaterialTheme.typography.bodySmall)
                    Text("Dates: ${vacation.startDate} - ${vacation.endDate}", style = MaterialTheme.typography.bodySmall)
                }
                Row {
                    TextButton(onClick = { viewModel.selectVacation(vacation) }) { Text("Edit") }
                    TextButton(onClick = { viewModel.deleteVacation(vacation) }) { Text("Delete") }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                if (!DateValidator.isValidFormat(startDate) || !DateValidator.isValidFormat(endDate)) {
                    Toast.makeText(context, "Please enter valid dates (YYYY-MM-DD).", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (!isEndAfterStart()) {
                    Toast.makeText(context, "End date must be after start date.", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                val selected = uiState.selectedVacation
                if (selected == null) {
                    viewModel.addVacation(title, hotel, startDate, endDate)
                } else {
                    viewModel.updateVacation(selected.copy(title = title, hotel = hotel, startDate = startDate, endDate = endDate))
                    viewModel.selectVacation(null)
                }
                title = ""; hotel = ""; startDate = ""; endDate = ""
            }) {
                Text(if (uiState.selectedVacation == null) "Add Vacation" else "Update Vacation")
            }

            uiState.selectedVacation?.let { vacation ->
                Button(onClick = { onNavigateToDetail(vacation.id) }) { Text("Next") }
            }
        }
    }
}
