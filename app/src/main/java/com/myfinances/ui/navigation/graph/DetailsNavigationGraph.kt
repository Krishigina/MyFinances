package com.myfinances.ui.navigation.graph

import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.myfinances.R
import com.myfinances.di.ViewModelFactory
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.ui.navigation.Destination
import com.myfinances.ui.screens.add_edit_transaction.AddEditTransactionScreen
import com.myfinances.ui.screens.add_edit_transaction.AddEditTransactionViewModel
import com.myfinances.ui.screens.analysis.AnalysisScreen
import com.myfinances.ui.screens.analysis.AnalysisViewModel
import com.myfinances.ui.screens.history.HistoryScreen
import com.myfinances.ui.screens.history.HistoryViewModel
import com.myfinances.ui.viewmodel.ScaffoldState
import com.myfinances.ui.viewmodel.TopBarAction
import com.myfinances.ui.viewmodel.TopBarState

fun NavGraphBuilder.detailsGraph(
    navController: NavHostController, // <- ИЗМЕНЕНИЕ ЗДЕСЬ
    viewModelFactory: ViewModelFactory,
    onScaffoldStateChanged: (ScaffoldState) -> Unit
) {
    composable(
        route = Destination.History.route,
        arguments = listOf(
            navArgument("transactionType") {
                type = NavType.EnumType(TransactionTypeFilter::class.java)
            },
            navArgument("parentRoute") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val viewModel: HistoryViewModel = viewModel(factory = viewModelFactory)

        @Suppress("DEPRECATION")
        val transactionType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            backStackEntry.arguments?.getSerializable("transactionType", TransactionTypeFilter::class.java)
        } else {
            backStackEntry.arguments?.getSerializable("transactionType") as? TransactionTypeFilter
        } ?: TransactionTypeFilter.ALL
        val parentRoute = backStackEntry.arguments?.getString("parentRoute") ?: Destination.Expenses.route


        LaunchedEffect(key1 = viewModel) {
            viewModel.initialize(transactionType, parentRoute)
            onScaffoldStateChanged(
                ScaffoldState(
                    topBarState = TopBarState(
                        title = navController.context.getString(R.string.top_bar_history_title),
                        navigationAction = TopBarAction(
                            id = "back",
                            onAction = { navController.popBackStack() },
                            content = {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = stringResource(id = R.string.action_back)
                                )
                            }
                        ),
                        actions = listOf(
                            TopBarAction(
                                id = "analysis",
                                onAction = {
                                    navController.navigate(
                                        Destination.Analysis.createRoute(
                                            filter = transactionType,
                                            parentRoute = parentRoute
                                        )
                                    ) {
                                        popUpTo(backStackEntry.destination.id) { inclusive = true }
                                    }
                                },
                                content = {
                                    Icon(
                                        painterResource(R.drawable.ic_history_analysis),
                                        contentDescription = stringResource(id = R.string.top_bar_analysis_title)
                                    )
                                }
                            )
                        )
                    ),
                    snackbarHostState = viewModel.snackbarHostState,
                    isBottomBarVisible = true,
                    isFabVisible = false
                )
            )
        }
        HistoryScreen(
            navController = navController,
            viewModel = viewModel
        )
    }
    composable(
        route = Destination.Analysis.route,
        arguments = listOf(
            navArgument("transactionType") {
                type = NavType.EnumType(TransactionTypeFilter::class.java)
            },
            navArgument("parentRoute") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val viewModel: AnalysisViewModel = viewModel(factory = viewModelFactory)

        @Suppress("DEPRECATION")
        val transactionType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            backStackEntry.arguments?.getSerializable("transactionType", TransactionTypeFilter::class.java)
        } else {
            backStackEntry.arguments?.getSerializable("transactionType") as? TransactionTypeFilter
        } ?: TransactionTypeFilter.ALL
        val parentRoute = backStackEntry.arguments?.getString("parentRoute") ?: Destination.Expenses.route


        LaunchedEffect(key1 = viewModel) {
            viewModel.initialize(transactionType, parentRoute)
            onScaffoldStateChanged(
                ScaffoldState(
                    topBarState = TopBarState(
                        title = navController.context.getString(R.string.top_bar_analysis_title),
                        navigationAction = TopBarAction(
                            id = "back",
                            onAction = { navController.popBackStack() },
                            content = {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = stringResource(id = R.string.action_back)
                                )
                            }
                        ),
                        actions = listOf(
                            TopBarAction(
                                id = "history",
                                onAction = {
                                    navController.navigate(
                                        Destination.History.createRoute(
                                            filter = transactionType,
                                            parentRoute = parentRoute
                                        )
                                    ) {
                                        popUpTo(backStackEntry.destination.id) { inclusive = true }
                                    }
                                },
                                content = {
                                    Icon(
                                        painterResource(R.drawable.ic_top_bar_history),
                                        contentDescription = stringResource(id = R.string.top_bar_icon_history)
                                    )
                                }
                            )
                        )
                    ),
                    snackbarHostState = viewModel.snackbarHostState,
                    isBottomBarVisible = true,
                    isFabVisible = false
                )
            )
        }
        AnalysisScreen(
            navController = navController,
            viewModel = viewModel
        )
    }
    composable(
        route = Destination.AddEditTransaction.route,
        arguments = listOf(
            navArgument("transactionId") {
                type = NavType.IntType
                defaultValue = -1
            },
            navArgument("transactionType") {
                type = NavType.EnumType(TransactionTypeFilter::class.java)
            },
            navArgument("parentRoute") {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->
        val viewModel: AddEditTransactionViewModel = viewModel(factory = viewModelFactory)
        val topBarState by viewModel.uiState.collectAsStateWithLifecycle()

        val transactionId = backStackEntry.arguments?.getInt("transactionId") ?: -1

        @Suppress("DEPRECATION")
        val transactionType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            backStackEntry.arguments?.getSerializable("transactionType", TransactionTypeFilter::class.java)
        } else {
            backStackEntry.arguments?.getSerializable("transactionType") as? TransactionTypeFilter
        } ?: TransactionTypeFilter.EXPENSE

        LaunchedEffect(key1 = viewModel) {
            viewModel.initialize(transactionId, transactionType)
        }

        LaunchedEffect(topBarState) {
            onScaffoldStateChanged(
                ScaffoldState(
                    topBarState = viewModel.topBarState.value,
                    isBottomBarVisible = true,
                    isFabVisible = false
                )
            )
        }

        AddEditTransactionScreen(
            navController = navController,
            viewModel = viewModel
        )
    }
}