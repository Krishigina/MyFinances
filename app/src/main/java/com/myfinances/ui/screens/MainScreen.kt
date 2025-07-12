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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
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
import com.myfinances.ui.screens.add_edit_transaction.AddEditTransactionViewModel
import com.myfinances.ui.screens.expenses.ExpensesViewModel
import com.myfinances.ui.screens.history.HistoryViewModel
import com.myfinances.ui.screens.income.IncomeViewModel
import com.myfinances.ui.viewmodel.TopBarStateProvider
import com.myfinances.ui.viewmodel.provideViewModelFactory

@Composable
fun MainScreen() {
    val mainNavController = rememberNavController()
    val navBackStackEntry by mainNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val factory = provideViewModelFactory()

    // --- Создание ViewModel для текущего экрана ---
    val expensesViewModel: ExpensesViewModel = viewModel(factory = factory)
    val incomeViewModel: IncomeViewModel = viewModel(factory = factory)

    val historyRoutePrefix = Destination.History.route.substringBefore("/{")
    val addEditTransactionRoutePrefix = Destination.AddEditTransaction.route.substringBefore("?")

    val currentViewModel: ViewModel? = when {
        currentRoute == Destination.Expenses.route -> expensesViewModel
        currentRoute == Destination.Income.route -> incomeViewModel
        currentRoute?.startsWith(historyRoutePrefix) == true -> navBackStackEntry?.let {
            viewModel<HistoryViewModel>(viewModelStoreOwner = it, factory = factory)
        }

        currentRoute?.startsWith(addEditTransactionRoutePrefix) == true -> navBackStackEntry?.let {
            viewModel<AddEditTransactionViewModel>(viewModelStoreOwner = it, factory = factory)
        }

        currentRoute == Destination.Account.route -> navBackStackEntry?.let {
            remember(it) {
                mainNavController.getBackStackEntry(Destination.Account.route)
            }
        }?.let {
            viewModel<AccountViewModel>(viewModelStoreOwner = it, factory = factory)
        }
        else -> null
    }

    val snackbarHostState: SnackbarHostState? = when (currentViewModel) {
        is ExpensesViewModel -> currentViewModel.snackbarHostState
        is IncomeViewModel -> currentViewModel.snackbarHostState
        is HistoryViewModel -> currentViewModel.snackbarHostState
        is AccountViewModel -> currentViewModel.snackbarHostState
        else -> null
    }

    Scaffold(
        topBar = {
            MainScreenTopBar(
                currentRoute = currentRoute,
                navController = mainNavController,
                viewModelProvider = { currentViewModel }
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
        Box(modifier = Modifier.fillMaxSize()) {
            NavigationGraph(
                navController = mainNavController,
                modifier = Modifier.padding(paddingValues),
                viewModelProvider = { currentViewModel }
            )

            if (snackbarHostState != null) {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .imePadding()
                        .padding(
                            bottom = paddingValues.calculateBottomPadding(),
                            start = 16.dp,
                            end = 16.dp
                        )
                ) { data ->
                    AppSnackbar(snackbarData = data)
                }
            }
        }
    }
}

@Composable
private fun MainScreenTopBar(
    currentRoute: String?,
    navController: NavHostController,
    viewModelProvider: () -> ViewModel?
) {
    val historyRoutePrefix = Destination.History.route.substringBefore("/{")
    val addEditTransactionRoutePrefix = Destination.AddEditTransaction.route.substringBefore("?")

    val viewModel = viewModelProvider()

    when {
        currentRoute?.startsWith(addEditTransactionRoutePrefix) == true && viewModel is TopBarStateProvider -> {
            val topBarState by viewModel.topBarState.collectAsState()
            MainTopBar(
                title = topBarState.title,
                navigationIcon = {
                    topBarState.navigationAction?.let { action ->
                        IconButton(onClick = action.onAction, enabled = action.isEnabled) {
                            action.content()
                        }
                    }
                },
                actions = {
                    topBarState.actions.forEach { action ->
                        IconButton(onClick = action.onAction, enabled = action.isEnabled) {
                            action.content()
                        }
                    }
                }
            )
        }

        currentRoute == Destination.Account.route && viewModel is AccountViewModel -> {
            AccountTopBar(accountViewModel = viewModel)
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