import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.learning_2.components.VacationDetailView
import com.example.learning_2.components.VacationManager
import com.example.learning_2.database.AppDatabase

@Composable
fun VacationApp(database: AppDatabase) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "vacationManager") {
        composable("vacationManager") {
            VacationManager(
                database = database,
                onNext = { vacationId ->
                    navController.navigate("vacationDetailView/$vacationId")
                }
            )
        }
        composable("vacationDetailView/{vacationId}") { backStackEntry ->
            val vacationId = backStackEntry.arguments?.getString("vacationId")?.toInt() ?: 0
            VacationDetailView(
                database = database,
                vacationId = vacationId,
                navController = navController
            )
        }
    }
}

