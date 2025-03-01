package com.example.learning_2.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.learning_2.entities.Excursion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExcursionDropdown(
    excursions: List<Excursion>,
    selectedExcursion: Excursion?,
    onSelectionChange: (Excursion) -> Unit
) {
    println("Excursion Dropdown, excursions: ")
    println(excursions)
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
