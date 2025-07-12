package com.myfinances.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.myfinances.R
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.ui.components.AppSnackbar
import com.myfinances.ui.components.BottomNavigationBar
import com.myfinances.ui.components.MainFloatingActionButton
import com.myfinances.ui.components.MainTopBar
import com.myfinances.ui.navigation.Destination
import com.myfinances.ui.navigation.NavigationGraph
import com.myfinances.ui.screens.account.AccountEvent
import com.myfinances.ui.screens.account.AccountUiState
import com.myfinances.ui.screens.account.AccountViewModel
import com.myfinances.ui.screens.expenses.ExpensesViewModel
import com.myfinances.ui.screens.history.HistoryViewModel
import com.myfinances.ui.screens.income.IncomeViewModel
import com.myfinances.ui.viewmodel.provideViewModelFactory

@Composable
fun MainScreen() {
    val mainNavController = rememberNavController()
    val navBackStackEntry by mainNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val factory = provideViewModelFactory()

    // ViewModel'и, которые живут пока жив NavGraph
    val expensesViewModel: ExpensesViewModel = viewModel(factory = factory)
    val incomeViewModel: IncomeViewModel = viewModel(factory = factory)

    // Определяем, какой ViewModel сейчас активен, чтобы получить его SnackbarHostState
    val historyRoutePrefix = Destination.History.route.substringBefore("/{")
    val snackbarHostState: SnackbarHostState? = when {
        currentRoute == Destination.Expenses.route -> expensesViewModel.snackbarHostState
        currentRoute == Destination.Income.route -> incomeViewModel.snackbarHostState
        // Для экранов со своим состоянием (и, возможно, аргументами), получаем ViewModel,
        // привязанную к их собственному backStackEntry. Это гарантирует, что мы
        // получаем правильный экземпляр.
        currentRoute?.startsWith(historyRoutePrefix) == true -> {
            val backStackEntry = mainNavController.currentBackStackEntry
            if (backStackEntry != null) {
                viewModel<HistoryViewModel>(
                    viewModelStoreOwner = backStackEntry,
                    factory = provideViewModelFactory(backStackEntry.savedStateHandle)
                ).snackbarHostState
            } else {
                null
            }
        }

        currentRoute == Destination.Account.route -> {
            val backStackEntry = remember(navBackStackEntry) {
                mainNavController.getBackStackEntry(Destination.Account.route)
            }
            viewModel<AccountViewModel>(
                viewModelStoreOwner = backStackEntry,
                factory = provideViewModelFactory()
            ).snackbarHostState
        }

        else -> null
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                MainScreenTopBar(
                    currentRoute = currentRoute,
                    navController = mainNavController
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    navController = mainNavController,
                    modifier = Modifier.navigationBarsPadding()
                )
            },
            floatingActionButton = {
                if (currentRoute == Destination.Expenses.route || currentRoute == Destination.Income.route) {
                    MainFloatingActionButton {
                        val type = if (currentRoute == Destination.Expenses.route) {
                            TransactionTypeFilter.EXPENSE
                        } else {
                            TransactionTypeFilter.INCOME
                        }
                        mainNavController.navigate(
                            Destination.AddEditTransaction.createRoute(transactionType = type)
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                NavigationGraph(
                    navController = mainNavController,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        if (snackbarHostState != null) {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .imePadding()
                    .padding(bottom = 90.dp, start = 16.dp, end = 16.dp)
            ) { data ->
                AppSnackbar(snackbarData = data)
            }
        }
    }
}

@Composable
private fun MainScreenTopBar(
    currentRoute: String?,
    navController: NavHostController
) {
    val historyRoutePrefix = Destination.History.route.substringBefore("/{")
    val addEditTransactionRoutePrefix = Destination.AddEditTransaction.route.substringBefore("?")

    when {
        currentRoute == Destination.Account.route -> {
            val backStackEntry = remember(navController.currentBackStackEntry) {
                navController.getBackStackEntry(Destination.Account.route)
            }
            val accountViewModel: AccountViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = provideViewModelFactory()
            )
            AccountTopBar(accountViewModel = accountViewModel)
        }
        currentRoute?.startsWith(addEditTransactionRoutePrefix) == true -> {
            // TopBar для этого экрана рендерится внутри самого экрана
        }
        currentRoute == Destination.Expenses.route -> {
            MainTopBar(
                title = stringResource(id = R.string.top_bar_expenses_today_title),
                actions = {
                    TopBarHistoryAction(
                        navController,
                        TransactionTypeFilter.EXPENSE,
                        Destination.Expenses
                    )
                }
            )
        }
        currentRoute == Destination.Income.route -> {
            MainTopBar(
                title = stringResource(id = R.string.top_bar_income_today_title),
                actions = {
                    TopBarHistoryAction(
                        navController,
                        TransactionTypeFilter.INCOME,
                        Destination.Income
                    )
                }
            )
        }
        currentRoute == Destination.Articles.route -> {
            MainTopBar(title = stringResource(id = R.string.top_bar_my_articles_title))
        }
        currentRoute == Destination.Settings.route -> {
            MainTopBar(title = stringResource(id = R.string.top_bar_settings_title))
        }
        currentRoute?.startsWith(historyRoutePrefix) == true -> {
            MainTopBar(
                title = stringResource(id = R.string.top_bar_history_title),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.action_back)
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun AccountTopBar(accountViewModel: AccountViewModel) {
    val state by accountViewModel.uiState.collectAsStateWithLifecycle()
    val isEditMode = (state as? AccountUiState.Success)?.isEditMode == true

    MainTopBar(
        title = stringResource(id = R.string.top_bar_my_account_title),
        navigationIcon = {
            if (isEditMode) {
                IconButton(onClick = { accountViewModel.onEvent(AccountEvent.EditModeToggled) }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.action_cancel)
                    )
                }
            }
        },
        actions = {
            if (state is AccountUiState.Success) {
                val isSaving = (state as AccountUiState.Success).isSaving
                IconButton(
                    onClick = {
                        val event =
                            if (isEditMode) AccountEvent.SaveChanges else AccountEvent.EditModeToggled
                        accountViewModel.onEvent(event)
                    },
                    enabled = !isSaving
                ) {
                    when {
                        isSaving -> CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )

                        isEditMode -> Icon(
                            Icons.Default.Check,
                            contentDescription = stringResource(R.string.action_save)
                        )

                        else -> Icon(
                            Icons.Default.Edit,
                            contentDescription = stringResource(R.string.top_bar_icon_edit)
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun TopBarHistoryAction(
    navController: NavHostController,
    filter: TransactionTypeFilter,
    parent: Destination
) {
    IconButton(onClick = {
        navController.navigate(Destination.History.createRoute(filter = filter, parent = parent))
    }) {
        Icon(
            painterResource(R.drawable.ic_top_bar_history),
            contentDescription = stringResource(id = R.string.top_bar_icon_history)
        )
    }
}