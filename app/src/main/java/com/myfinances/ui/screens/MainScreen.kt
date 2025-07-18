package com.myfinances.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.myfinances.MyFinancesApplication
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.ui.components.AppSnackbar
import com.myfinances.ui.components.BottomNavigationBar
import com.myfinances.ui.components.MainFloatingActionButton
import com.myfinances.ui.components.MainTopBar
import com.myfinances.ui.navigation.Destination
import com.myfinances.ui.navigation.NavigationGraph
import com.myfinances.ui.viewmodel.ScaffoldState

@Composable
fun MainScreen() {
    val mainNavController = rememberNavController()

    val appComponent = (LocalContext.current.applicationContext as MyFinancesApplication).appComponent
    val viewModelFactory = remember {
        appComponent.viewModelComponentFactory().create().getViewModelFactory()
    }

    var scaffoldState by remember { mutableStateOf(ScaffoldState()) }
    val snackbarHostState = remember { scaffoldState.snackbarHostState }

    Scaffold(
        topBar = {
            MainTopBar(
                title = scaffoldState.topBarState.title,
                navigationIcon = {
                    scaffoldState.topBarState.navigationAction?.let { action ->
                        IconButton(onClick = action.onAction, enabled = action.isEnabled) {
                            action.content()
                        }
                    }
                },
                actions = {
                    scaffoldState.topBarState.actions.forEach { action ->
                        IconButton(onClick = action.onAction, enabled = action.isEnabled) {
                            action.content()
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (scaffoldState.isBottomBarVisible) {
                BottomNavigationBar(
                    navController = mainNavController,
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        },
        floatingActionButton = {
            if (scaffoldState.isFabVisible) {
                MainFloatingActionButton {
                    val currentRoute = mainNavController.currentDestination?.route ?: Destination.Expenses.route
                    val type = if (currentRoute == Destination.Expenses.route) {
                        TransactionTypeFilter.EXPENSE
                    } else {
                        TransactionTypeFilter.INCOME
                    }
                    mainNavController.navigate(
                        Destination.AddEditTransaction.createRoute(
                            transactionType = type,
                            parentRoute = currentRoute
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavigationGraph(
                navController = mainNavController,
                modifier = Modifier.padding(paddingValues),
                viewModelFactory = viewModelFactory,
                onScaffoldStateChanged = { newState ->
                    scaffoldState = newState
                }
            )
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .imePadding()
                    .padding(bottom = paddingValues.calculateBottomPadding(), start = 16.dp, end = 16.dp)
            ) { data ->
                AppSnackbar(snackbarData = data)
            }
        }
    }
}