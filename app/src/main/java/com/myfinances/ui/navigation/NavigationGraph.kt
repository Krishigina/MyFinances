package com.myfinances.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.myfinances.ui.screens.account.AccountScreen
import com.myfinances.ui.screens.articles.ArticlesScreen
import com.myfinances.ui.screens.expenses.ExpensesScreen
import com.myfinances.ui.screens.income.IncomeScreen
import com.myfinances.ui.screens.settings.SettingsScreen
import com.myfinances.ui.screens.splash.SplashScreen

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Destination.Splash.route) {
        composable(route = Destination.Expenses.route) {
            ExpensesScreen()
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
        composable(route = Destination.Splash.route) {
            SplashScreen(navController = navController)
        }
    }
}