package com.myfinances.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.myfinances.ui.screens.ArticlesScreen
import com.myfinances.ui.screens.ExpensesScreen
import com.myfinances.ui.screens.IncomeScreen
import com.myfinances.ui.screens.ScoreScreen
import com.myfinances.ui.screens.SettingsScreen

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Destination.Expenses.route) {
        composable(route = Destination.Expenses.route) {
            ExpensesScreen()
        }
        composable(route = Destination.Income.route) {
            IncomeScreen()
        }
        composable(route = Destination.Score.route) {
            ScoreScreen()
        }
        composable(route = Destination.Articles.route) {
            ArticlesScreen()
        }
        composable(route = Destination.Settings.route) {
            SettingsScreen()
        }
    }
}