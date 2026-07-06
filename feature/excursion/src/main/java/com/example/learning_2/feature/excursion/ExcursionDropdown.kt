package com.example.learning_2.feature.excursion

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.learning_2.core.database.entity.Excursion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExcursionDropdown(
    excursions: List<Excursion>,
    selectedExcursion: Excursion?,
    onSelectionChange: (Excursion) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedExcursion?.name ?: "Select an Excursion",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(), // This anchors the dropdown menu to this TextField
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                excursions.forEach { excursion ->
                    DropdownMenuItem(
                        text = { Text(excursion.name) },
                        onClick = {
                            onSelectionChange(excursion)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
