package com.myfinances.ui.navigation

import android.os.Build
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.myfinances.R
import com.myfinances.di.ViewModelFactory
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.ui.screens.account.AccountEvent
import com.myfinances.ui.screens.account.AccountScreen
import com.myfinances.ui.screens.account.AccountUiState
import com.myfinances.ui.screens.account.AccountViewModel
import com.myfinances.ui.screens.add_edit_transaction.AddEditTransactionScreen
import com.myfinances.ui.screens.add_edit_transaction.AddEditTransactionViewModel
import com.myfinances.ui.screens.articles.ArticlesScreen
import com.myfinances.ui.screens.articles.ArticlesViewModel
import com.myfinances.ui.screens.expenses.ExpensesScreen
import com.myfinances.ui.screens.expenses.ExpensesViewModel
import com.myfinances.ui.screens.history.HistoryScreen
import com.myfinances.ui.screens.history.HistoryViewModel
import com.myfinances.ui.screens.income.IncomeScreen
import com.myfinances.ui.screens.income.IncomeViewModel
import com.myfinances.ui.screens.settings.SettingsScreen
import com.myfinances.ui.viewmodel.ScaffoldState
import com.myfinances.ui.viewmodel.TopBarState

/**
 * Определяет навигационный граф для основных экранов приложения,
 * которые переключаются через BottomNavigationBar.
 *
 * @param navController Контроллер навигации, управляющий стеком экранов.
 */
@Composable
fun NavigationGraph(
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
        navigation(
            startDestination = Destination.Expenses.route,
            route = "main_graph"
        ) {
            composable(Destination.Expenses.route) {
                val viewModel: ExpensesViewModel = viewModel(factory = viewModelFactory)
                LaunchedEffect(Unit) {
                    onScaffoldStateChanged(
                        ScaffoldState(
                            topBarState = TopBarState(
                                title = navController.context.getString(R.string.top_bar_expenses_today_title),
                                actions = listOf(
                                    com.myfinances.ui.viewmodel.TopBarAction(
                                        id = "history",
                                        onAction = {
                                            navController.navigate(
                                                Destination.History.createRoute(
                                                    filter = TransactionTypeFilter.EXPENSE,
                                                    parent = Destination.Expenses
                                                )
                                            )
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
                            isFabVisible = true,
                            isBottomBarVisible = true
                        )
                    )
                }
                ExpensesScreen(navController = navController, viewModel = viewModel)
            }
            composable(Destination.Income.route) {
                val viewModel: IncomeViewModel = viewModel(factory = viewModelFactory)

                LaunchedEffect(Unit) {
                    onScaffoldStateChanged(
                        ScaffoldState(
                            topBarState = TopBarState(
                                title = navController.context.getString(R.string.top_bar_income_today_title),
                                actions = listOf(
                                    com.myfinances.ui.viewmodel.TopBarAction(
                                        id = "history",
                                        onAction = {
                                            navController.navigate(
                                                Destination.History.createRoute(
                                                    filter = TransactionTypeFilter.INCOME,
                                                    parent = Destination.Income
                                                )
                                            )
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
                            isFabVisible = true,
                            isBottomBarVisible = true
                        )
                    )
                }
                IncomeScreen(navController = navController, viewModel = viewModel)
            }
            composable(Destination.Account.route) {
                val viewModel: AccountViewModel = viewModel(factory = viewModelFactory)
                val accountState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(accountState) {
                    val successState = accountState as? AccountUiState.Success
                    val isEditMode = successState?.isEditMode == true
                    val isSaving = successState?.isSaving == true

                    onScaffoldStateChanged(
                        ScaffoldState(
                            topBarState = TopBarState(
                                title = navController.context.getString(R.string.top_bar_my_account_title),
                                navigationAction = if (isEditMode) com.myfinances.ui.viewmodel.TopBarAction(
                                    id = "back",
                                    onAction = { viewModel.onEvent(AccountEvent.EditModeToggled) },
                                    content = {
                                        Icon(
                                            Icons.Default.ArrowBack,
                                            contentDescription = stringResource(R.string.action_cancel)
                                        )
                                    }
                                ) else null,
                                actions = listOf(
                                    com.myfinances.ui.viewmodel.TopBarAction(
                                        id = "edit_save",
                                        isEnabled = !isSaving,
                                        onAction = {
                                            val event = if (isEditMode) AccountEvent.SaveChanges else AccountEvent.EditModeToggled
                                            viewModel.onEvent(event)
                                        },
                                        content = {
                                            when {
                                                isSaving -> CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                                isEditMode -> Icon(Icons.Default.Check, contentDescription = stringResource(R.string.action_save))
                                                else -> Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.top_bar_icon_edit))
                                            }
                                        }
                                    )
                                )
                            ),
                            snackbarHostState = viewModel.snackbarHostState,
                            isBottomBarVisible = true
                        )
                    )
                }
                AccountScreen(navController = navController, viewModel = viewModel)
            }
            composable(Destination.Articles.route) {
                val viewModel: ArticlesViewModel = viewModel(factory = viewModelFactory)
                LaunchedEffect(Unit) {
                    onScaffoldStateChanged(
                        ScaffoldState(
                            topBarState = TopBarState(title = navController.context.getString(R.string.top_bar_my_articles_title)),
                            isBottomBarVisible = true
                        )
                    )
                }
                ArticlesScreen(viewModel = viewModel)
            }
            composable(Destination.Settings.route) {
                LaunchedEffect(Unit) {
                    onScaffoldStateChanged(
                        ScaffoldState(
                            topBarState = TopBarState(title = navController.context.getString(R.string.top_bar_settings_title)),
                            isBottomBarVisible = true
                        )
                    )
                }
                SettingsScreen()
            }
        }
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

            // Безопасно получаем аргумент
            @Suppress("DEPRECATION") // Для поддержки старых API
            val transactionType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                backStackEntry.arguments?.getSerializable("transactionType", TransactionTypeFilter::class.java)
            } else {
                backStackEntry.arguments?.getSerializable("transactionType") as? TransactionTypeFilter
            } ?: TransactionTypeFilter.ALL


            LaunchedEffect(key1 = viewModel) {
                viewModel.initialize(transactionType)
                onScaffoldStateChanged(
                    ScaffoldState(
                        topBarState = TopBarState(
                            title = navController.context.getString(R.string.top_bar_history_title),
                            navigationAction = com.myfinances.ui.viewmodel.TopBarAction(
                                id = "back",
                                onAction = { navController.popBackStack() },
                                content = {
                                    Icon(
                                        Icons.Default.ArrowBack,
                                        contentDescription = stringResource(id = R.string.action_back)
                                    )
                                }
                            )
                        ),
                        snackbarHostState = viewModel.snackbarHostState
                    )
                )
            }
            HistoryScreen(
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
                    ScaffoldState(topBarState = viewModel.topBarState.value)
                )
            }

            AddEditTransactionScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}