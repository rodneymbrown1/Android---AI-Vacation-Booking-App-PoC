package com.example.learning_2.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.learning_2.entities.Excursion

@Composable
fun ExcursionSelectionRow(
    excursions: List<Excursion>,
    selectedExcursion: Excursion?,
    onSelectionChange: (Excursion) -> Unit,
    onEdit: (Excursion) -> Unit,
    onDelete: (Excursion) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Excursions", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // The dropdown lists all available excursion names.
            ExcursionDropdown(
                excursions = excursions,
                selectedExcursion = selectedExcursion,
                onSelectionChange = onSelectionChange
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Display Edit and Delete buttons if an excursion is selected.
            selectedExcursion?.let { excursion ->
                TextButton(onClick = { onEdit(excursion) }) {
                    Text("Edit")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = { onDelete(excursion) }) {
                    Text("Delete")
                }
            }
        }
    }
}
