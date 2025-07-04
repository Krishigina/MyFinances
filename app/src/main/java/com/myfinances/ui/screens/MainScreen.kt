package com.myfinances.ui.screens

import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.myfinances.R
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.ui.components.BottomNavigationBar
import com.myfinances.ui.components.MainFloatingActionButton
import com.myfinances.ui.components.MainTopBar
import com.myfinances.ui.navigation.Destination
import com.myfinances.ui.navigation.NavigationGraph
import com.myfinances.ui.screens.account.AccountEvent
import com.myfinances.ui.screens.account.AccountUiState
import com.myfinances.ui.screens.account.AccountViewModel

/**
 * Главный экран приложения, который служит контейнером для всех основных экранов.
 *
 * Содержит `Scaffold`, который управляет `TopBar`, `BottomNavigationBar`
 * и `FloatingActionButton`, а также вложенный `NavigationGraph`.
 * Он отслеживает текущий маршрут навигации для корректного отображения
 * заголовков и кнопок в TopBar.
 */
@Composable
fun MainScreen() {
    val mainNavController = rememberNavController()
    val navBackStackEntry by mainNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

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
                MainFloatingActionButton { /* TODO: Navigate to Add Transaction */ }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavigationGraph(navController = mainNavController)
        }
    }
}


/**
 * Composable-функция, которая динамически создает TopBar в зависимости от текущего экрана.
 * @param currentRoute Текущий маршрут в графе навигации.
 * @param navController Контроллер навигации для выполнения переходов.
 */
@Composable
private fun MainScreenTopBar(
    currentRoute: String?,
    navController: NavHostController
) {
    when (currentRoute) {
        Destination.Expenses.route -> {
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

        Destination.Income.route -> {
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

        Destination.Account.route -> {
            AccountTopBar(navController = navController)
        }

        Destination.Articles.route -> {
            MainTopBar(title = stringResource(id = R.string.top_bar_my_articles_title))
        }

        Destination.Settings.route -> {
            MainTopBar(title = stringResource(id = R.string.top_bar_settings_title))
        }
        Destination.History.route -> {
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

/**
 * Специализированный TopBar для экрана "Счет".
 * Он получает доступ к [AccountViewModel] для отображения правильных кнопок
 * (Редактировать/Сохранить/Отмена) и отправки событий при нажатии на них.
 */
@Composable
private fun AccountTopBar(navController: NavHostController) {
    // Получаем экземпляр ViewModel, привязанный к графу навигации.
    // Это позволяет TopBar'у и самому экрану использовать одну и ту же ViewModel.
    val backStackEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry(Destination.Account.route)
    }
    val accountViewModel: AccountViewModel = hiltViewModel(backStackEntry)
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

/**
 * Вспомогательный компонент для кнопки "История" в TopBar.
 */
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