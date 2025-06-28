package com.myfinances.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.myfinances.ui.screens.MainScreen
import com.myfinances.ui.screens.splash.SplashScreen

/**
 * Корневой навигационный граф приложения.
 * Отвечает за навигацию самого верхнего уровня, в данном случае,
 * между экраном-заставкой (SplashScreen) и основным экраном приложения (MainScreen).
 */
@Composable
fun RootNavigationGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.Splash
    ) {
        composable(Route.Splash) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Route.Main) {
                        popUpTo(Route.Splash) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(Route.Main) {
            MainScreen()
        }
    }
}