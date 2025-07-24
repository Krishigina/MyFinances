package com.myfinances.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.myfinances.data.manager.SnackbarManager
import com.myfinances.di.ViewModelFactory
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.ui.components.AppSnackbar
import com.myfinances.ui.components.BottomNavigationBar
import com.myfinances.ui.components.MainFloatingActionButton
import com.myfinances.ui.components.MainTopBar
import com.myfinances.ui.navigation.graph.AppNavHost
import com.myfinances.ui.viewmodel.ScaffoldState
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NavigationGraph(
    navController: NavHostController,
    viewModelFactory: ViewModelFactory,
    snackbarManager: SnackbarManager,
) {
    var scaffoldState by remember { mutableStateOf(ScaffoldState()) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarManager) {
        snackbarManager.messages.collectLatest { messages ->
            messages.firstOrNull()?.let { message ->
                snackbarHostState.showSnackbar(message.message)
                snackbarManager.setMessageShown(message.id)
            }
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isBottomBarVisible = remember(currentRoute) {
        when (currentRoute) {
            Destination.Expenses.route,
            Destination.Income.route,
            Destination.Account.route,
            Destination.Articles.route,
            Destination.Settings.route -> true
            else -> currentRoute?.startsWith("history") == true ||
                    currentRoute?.startsWith("analysis") == true
        }
    }

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
            if (isBottomBarVisible) {
                BottomNavigationBar(
                    navController = navController,
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        },
        floatingActionButton = {
            if (scaffoldState.isFabVisible) {
                MainFloatingActionButton {
                    val type = if (currentRoute == Destination.Expenses.route) {
                        TransactionTypeFilter.EXPENSE
                    } else {
                        TransactionTypeFilter.INCOME
                    }
                    navController.navigate(
                        Destination.AddEditTransaction.createRoute(
                            transactionType = type,
                            parentRoute = currentRoute ?: Destination.Expenses.route
                        )
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .imePadding()
                    .padding(horizontal = 16.dp)
            ) { data ->
                AppSnackbar(snackbarData = data)
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            AppNavHost(
                navController = navController,
                modifier = Modifier.padding(paddingValues),
                viewModelFactory = viewModelFactory,
                onScaffoldStateChanged = { newState ->
                    scaffoldState = newState
                }
            )
        }
    }
}