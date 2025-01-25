package com.example.learning_2.components

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.learning_2.database.AppDatabase
import com.example.learning_2.entities.Vacation
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VacationManager(database: AppDatabase, onNext: (Int) -> Unit) {
    val vacationDao = database.vacationDao()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var vacations by remember { mutableStateOf<List<Vacation>>(emptyList()) }
    var selectedVacation by remember { mutableStateOf<Vacation?>(null) }
    var title by remember { mutableStateOf("") }
    var hotel by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        vacations = vacationDao.getAll()
    }


}
