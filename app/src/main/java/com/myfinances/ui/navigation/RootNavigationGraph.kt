package com.myfinances.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.myfinances.MyFinancesApplication
import com.myfinances.domain.usecase.IsPinSetUseCase
import com.myfinances.ui.navigation.PinMode
import com.myfinances.ui.screens.MainScreen
import com.myfinances.ui.screens.pin.PinScreen
import com.myfinances.ui.screens.pin.PinScreenViewModel
import com.myfinances.ui.screens.splash.SplashScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Корневой навигационный граф приложения.
 * Отвечает за навигацию самого верхнего уровня, в данном случае,
 * между экраном-заставкой (SplashScreen) и основным экраном приложения (MainScreen).
 */
@Composable
fun RootNavigationGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext
    val coroutineScope = rememberCoroutineScope()

    val appComponent = remember(context) {
        (context as MyFinancesApplication).appComponent
    }
    val viewModelFactory = remember {
        appComponent.viewModelComponentFactory().create().getViewModelFactory()
    }
    val snackbarManager = remember {
        appComponent.provideSnackbarManager()
    }
    val hapticFeedbackManager = remember {
        appComponent.provideHapticFeedbackManager()
    }
    val isPinSetUseCase = remember {
        appComponent.isPinSetUseCase()
    }

    NavHost(
        navController = navController,
        startDestination = Route.Splash
    ) {
        composable(Route.Splash) {
            SplashScreen(
                onSplashFinished = {
                    coroutineScope.launch {
                        val isPinSet = isPinSetUseCase().first()
                        val destination = if (isPinSet) Route.PinAuth else Route.Main

                        navController.navigate(destination) {
                            popUpTo(Route.Splash) {
                                inclusive = true
                            }
                        }
                    }
                }
            )
        }
        composable(Route.PinAuth) {
            val pinViewModel: PinScreenViewModel = viewModel(factory = viewModelFactory)
            pinViewModel.initialize(PinMode.VERIFY)

            PinScreen(
                navController = navController,
                viewModel = pinViewModel,
                onAuthSuccess = {
                    navController.navigate(Route.Main) {
                        popUpTo(Route.PinAuth) { inclusive = true }
                    }
                }
            )
        }
        composable(Route.Main) {
            MainScreen(
                viewModelFactory = viewModelFactory,
                snackbarManager = snackbarManager,
                hapticFeedbackManager = hapticFeedbackManager
            )
        }
    }
}