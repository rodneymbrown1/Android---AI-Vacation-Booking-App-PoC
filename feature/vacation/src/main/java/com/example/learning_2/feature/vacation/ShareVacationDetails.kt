package com.example.learning_2.feature.vacation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Vacation Details", detailsToShare)
        clipboard.setPrimaryClip(clip)

        // Show confirmation toast
        Toast.makeText(context, "Vacation details copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    Button(
        onClick = { shareDetails() },
        modifier = modifier
    ) {
        Text("Share")
    }
}
