package com.example.learning_2.components

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.learning_2.feature.vacation.VacationDetailView
import com.example.learning_2.feature.vacation.VacationManager

@Composable
fun VacationApp() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "vacationManager") {
        composable("vacationManager") {
            VacationManager(
                onNavigateToDetail = { vacationId ->
                    navController.navigate("vacationDetail/$vacationId")
                }
            )
        }
        composable("vacationDetail/{vacationId}") { backStackEntry ->
            val vacationId = backStackEntry.arguments?.getString("vacationId")?.toLong() ?: 0L
            VacationDetailView(
                vacationId = vacationId,
                navController = navController
            )
        }
    }
}
