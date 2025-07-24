package com.myfinances.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.myfinances.MyFinancesApplication
import com.myfinances.ui.navigation.NavigationGraph

@Composable
fun MainScreen() {
    val mainNavController = rememberNavController()

    val appComponent = (LocalContext.current.applicationContext as MyFinancesApplication).appComponent
    val viewModelFactory = remember {
        appComponent.viewModelComponentFactory().create().getViewModelFactory()
    }
    val snackbarManager = remember {
        appComponent.provideSnackbarManager()
    }

    NavigationGraph(
        navController = mainNavController,
        viewModelFactory = viewModelFactory,
        snackbarManager = snackbarManager
    )
}