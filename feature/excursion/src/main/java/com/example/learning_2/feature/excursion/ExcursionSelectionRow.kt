package com.example.learning_2.feature.excursion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.learning_2.core.database.entity.Excursion

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
