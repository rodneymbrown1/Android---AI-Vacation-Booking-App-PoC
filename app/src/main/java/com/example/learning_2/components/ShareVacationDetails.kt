package com.example.learning_2.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ShareVacationDetails(
    vacationTitle: String,
    vacationDetails: String,
    excursionDetails: List<String>,
    context: Context,
    modifier: Modifier = Modifier
) {
    fun shareDetails() {
        // Combine vacation details with excursion details
        val detailsToShare = buildString {
            append(vacationDetails)
            append("\n\nExcursions:\n")
            if (excursionDetails.isNotEmpty()) {
                excursionDetails.forEach { append("- $it\n") }
            } else {
                append("No excursions available.")
            }
        }

        // Copy to clipboard
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Vacation Details", detailsToShare)
        clipboard.setPrimaryClip(clip)

        // Show confirmation toast
        Toast.makeText(context, "Vacation details copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    Button(
        onClick = { shareDetails() },
    ) {
        Text("Share")
    }
}
