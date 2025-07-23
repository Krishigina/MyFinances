package com.myfinances.ui.navigation.graph

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.myfinances.di.ViewModelFactory
import com.myfinances.ui.viewmodel.ScaffoldState

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModelFactory: ViewModelFactory,
    onScaffoldStateChanged: (ScaffoldState) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "main_graph",
        modifier = modifier
    ) {
        mainGraph(navController, viewModelFactory, onScaffoldStateChanged)
        detailsGraph(navController, viewModelFactory, onScaffoldStateChanged)
    }
}