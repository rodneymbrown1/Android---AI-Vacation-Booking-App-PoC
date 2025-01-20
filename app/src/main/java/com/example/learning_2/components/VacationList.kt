package com.example.learning_2.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.learning_2.entities.Vacation

@Composable
fun VacationList(
    vacations: List<Vacation> = listOf(),
    onEdit: (Vacation) -> Unit = {},
    onDelete: (Vacation) -> Unit = {}
) {
    vacations.forEach { vacation ->
        Row {
            Text("Destination: ${vacation.title}, " + "Hotel: ${vacation.hotel}" +
                    "Start: ${vacation.startDate}" +
            "End: ${vacation.endDate}" )
            TextButton(onClick = { onEdit(vacation) }) { Text("Edit") }
            TextButton(onClick = { onDelete(vacation) }) { Text("Delete") }
        }
    }
}
