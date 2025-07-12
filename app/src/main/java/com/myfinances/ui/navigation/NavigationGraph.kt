package com.myfinances.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.ui.screens.account.AccountScreen
import com.myfinances.ui.screens.add_edit_transaction.AddEditTransactionScreen
import com.myfinances.ui.screens.add_edit_transaction.AddEditTransactionViewModel
import com.myfinances.ui.screens.articles.ArticlesScreen
import com.myfinances.ui.screens.expenses.ExpensesScreen
import com.myfinances.ui.screens.history.HistoryScreen
import com.myfinances.ui.screens.income.IncomeScreen
import com.myfinances.ui.screens.settings.SettingsScreen

/**
 * Определяет навигационный граф для основных экранов приложения,
 * которые переключаются через BottomNavigationBar.
 *
 * @param navController Контроллер навигации, управляющий стеком экранов.
 */
@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModelProvider: () -> ViewModel?
) {
    NavHost(
        navController = navController,
        startDestination = Destination.Expenses.route,
        modifier = modifier
    ) {
        composable(Destination.Expenses.route) {
            ExpensesScreen(navController = navController)
        }
        composable(Destination.Income.route) {
            IncomeScreen(navController = navController)
        }
        composable(Destination.Account.route) {
            AccountScreen(navController = navController)
        }
        composable(Destination.Articles.route) {
            ArticlesScreen()
        }
        composable(Destination.Settings.route) {
            SettingsScreen()
        }
        composable(
            route = Destination.History.route,
            arguments = listOf(
                navArgument("transactionType") {
                    type = NavType.EnumType(TransactionTypeFilter::class.java)
                },
                navArgument("parentRoute") { type = NavType.StringType }
            )
        ) {
            HistoryScreen(
                navController = navController
            )
        }
        composable(
            route = Destination.AddEditTransaction.route,
            arguments = listOf(
                navArgument("transactionId") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("transactionType") {
                    type = NavType.EnumType(TransactionTypeFilter::class.java)
                }
            )
        ) {
            val viewModel = viewModelProvider()
            if (viewModel is AddEditTransactionViewModel) {
                AddEditTransactionScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}