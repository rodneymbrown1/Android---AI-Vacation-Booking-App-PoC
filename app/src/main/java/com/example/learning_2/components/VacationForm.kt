package com.example.learning_2.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun VacationForm(
    onAddVacation: (String, String) -> Unit = { _, _ -> }
) {
    var destination by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    Row {
        BasicTextField(
            value = destination,
            onValueChange = { destination = it },
            modifier = Modifier.weight(1f)
        )

        BasicTextField(
            value = date,
            onValueChange = { date = it },
            modifier = Modifier.weight(1f)
        )

        Button(onClick = { onAddVacation(destination, date) }) {
            Text("Add Vacation")
        }
    }
}
