package com.myfinances.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.ui.screens.account.AccountScreen
import com.myfinances.ui.screens.articles.ArticlesScreen
import com.myfinances.ui.screens.expenses.ExpensesScreen
import com.myfinances.ui.screens.history.HistoryScreen
import com.myfinances.ui.screens.income.IncomeScreen
import com.myfinances.ui.screens.settings.SettingsScreen

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Destination.Expenses.route) {

        navigation(
            route = Destination.Expenses.route,
            startDestination = Destination.ExpensesList.route
        ) {
            composable(route = Destination.ExpensesList.route) {
                ExpensesScreen(navController = navController)
            }
        }

        composable(
            route = Destination.History.route,
            arguments = listOf(
                navArgument("transactionType") {
                    type = NavType.EnumType(TransactionTypeFilter::class.java)
                    defaultValue = TransactionTypeFilter.ALL
                },
                navArgument("parentRoute") {
                    type = NavType.StringType
                }
            )
        ) {
            HistoryScreen(navController = navController)
        }

        composable(route = Destination.Income.route) {
            IncomeScreen()
        }
        composable(route = Destination.Account.route) {
            AccountScreen()
        }
        composable(route = Destination.Articles.route) {
            ArticlesScreen()
        }
        composable(route = Destination.Settings.route) {
            SettingsScreen()
        }
    }
}