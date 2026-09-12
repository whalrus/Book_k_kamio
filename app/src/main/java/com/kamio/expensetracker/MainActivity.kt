package com.kamio.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kamio.expensetracker.ui.screens.AddExpenseScreen
import com.kamio.expensetracker.ui.screens.DashboardScreen
import com.kamio.expensetracker.ui.screens.HistoryScreen
import com.kamio.expensetracker.ui.screens.SettingsScreen
import com.kamio.expensetracker.ui.theme.KamioLedgerTheme
import com.kamio.expensetracker.viewmodel.ExpenseViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ExpenseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KamioLedgerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(viewModel)
                }
            }
        }
    }
}

private object Routes {
    const val DASHBOARD = "dashboard"
    const val ADD_EXPENSE = "add_expense"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
}

@Composable
private fun AppNavHost(viewModel: ExpenseViewModel) {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.DASHBOARD) {
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                viewModel = viewModel,
                onAddExpense = { navController.navigate(Routes.ADD_EXPENSE) },
                onOpenHistory = { navController.navigate(Routes.HISTORY) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }
        composable(Routes.ADD_EXPENSE) {
            AddExpenseScreen(
                onSave = { amountCents, category, note, date ->
                    viewModel.addExpense(amountCents, category, note, date)
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() }
            )
        }
        composable(Routes.HISTORY) {
            HistoryScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(viewModel = viewModel, onDone = { navController.popBackStack() })
        }
    }
}
