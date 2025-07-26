package com.myfinances.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.myfinances.data.manager.HapticFeedbackManager
import com.myfinances.data.manager.SnackbarManager
import com.myfinances.di.ViewModelFactory
import com.myfinances.ui.navigation.NavigationGraph

@Composable
fun MainScreen(
    viewModelFactory: ViewModelFactory,
    snackbarManager: SnackbarManager,
    hapticFeedbackManager: HapticFeedbackManager
) {
    val mainNavController = rememberNavController()

    NavigationGraph(
        navController = mainNavController,
        viewModelFactory = viewModelFactory,
        snackbarManager = snackbarManager,
        hapticFeedbackManager = hapticFeedbackManager
    )
}