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

    -

    Column(Modifier.padding(16.dp)) {
        Text("Excursions", style = MaterialTheme.typography.titleLarge)
    }

}
