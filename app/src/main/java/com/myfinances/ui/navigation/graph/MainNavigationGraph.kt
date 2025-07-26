package com.myfinances.ui.navigation.graph

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.myfinances.R
import com.myfinances.di.ViewModelFactory
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.ui.navigation.Destination
import com.myfinances.ui.navigation.PinMode
import com.myfinances.ui.screens.about.AboutScreen
import com.myfinances.ui.screens.about.AboutViewModel
import com.myfinances.ui.screens.account.AccountEvent
import com.myfinances.ui.screens.account.AccountScreen
import com.myfinances.ui.screens.account.AccountUiState
import com.myfinances.ui.screens.account.AccountViewModel
import com.myfinances.ui.screens.articles.ArticlesScreen
import com.myfinances.ui.screens.articles.ArticlesViewModel
import com.myfinances.ui.screens.color_palette.ColorPaletteScreen
import com.myfinances.ui.screens.color_palette.ColorPaletteViewModel
import com.myfinances.ui.screens.expenses.ExpensesScreen
import com.myfinances.ui.screens.expenses.ExpensesViewModel
import com.myfinances.ui.screens.haptics.HapticsScreen
import com.myfinances.ui.screens.haptics.HapticsScreenViewModel
import com.myfinances.ui.screens.income.IncomeScreen
import com.myfinances.ui.screens.income.IncomeViewModel
import com.myfinances.ui.screens.language.LanguageScreen
import com.myfinances.ui.screens.language.LanguageScreenViewModel
import com.myfinances.ui.screens.pin.PinScreen
import com.myfinances.ui.screens.pin.PinScreenViewModel
import com.myfinances.ui.screens.settings.SettingsScreen
import com.myfinances.ui.screens.settings.SettingsViewModel
import com.myfinances.ui.screens.sync_frequency.SyncFrequencyScreen
import com.myfinances.ui.screens.sync_frequency.SyncFrequencyViewModel
import com.myfinances.ui.viewmodel.ScaffoldState
import com.myfinances.ui.viewmodel.TopBarAction
import com.myfinances.ui.viewmodel.TopBarState

fun NavGraphBuilder.mainGraph(
    navController: NavHostController,
    viewModelFactory: ViewModelFactory,
    onScaffoldStateChanged: (ScaffoldState) -> Unit
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
                                TopBarAction(
                                    id = "history",
                                    onAction = {
                                        navController.navigate(
                                            Destination.History.createRoute(
                                                filter = TransactionTypeFilter.EXPENSE,
                                                parentRoute = Destination.Expenses.route
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
                                TopBarAction(
                                    id = "history",
                                    onAction = {
                                        navController.navigate(
                                            Destination.History.createRoute(
                                                filter = TransactionTypeFilter.INCOME,
                                                parentRoute = Destination.Income.route
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
                            navigationAction = if (isEditMode) TopBarAction(
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
                                TopBarAction(
                                    id = "edit_save",
                                    isEnabled = !isSaving,
                                    onAction = {
                                        val event =
                                            if (isEditMode) AccountEvent.SaveChanges else AccountEvent.EditModeToggled
                                        viewModel.onEvent(event)
                                    },
                                    content = {
                                        when {
                                            isSaving -> CircularProgressIndicator(
                                                modifier = Modifier.size(
                                                    24.dp
                                                )
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
                                )
                            )
                        ),
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
            val viewModel: SettingsViewModel = viewModel(factory = viewModelFactory)
            val lifecycleOwner = LocalLifecycleOwner.current

            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        viewModel.onResume()
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }
            LaunchedEffect(Unit) {
                onScaffoldStateChanged(
                    ScaffoldState(
                        topBarState = TopBarState(title = navController.context.getString(R.string.top_bar_settings_title)),
                        isBottomBarVisible = true
                    )
                )
            }
            SettingsScreen(
                viewModel = viewModel,
                onNavigateToColorPalette = {
                    navController.navigate(Destination.ColorPaletteSelection.route)
                },
                onNavigateToHaptics = {
                    navController.navigate(Destination.Haptics.route)
                },
                onNavigateToLanguage = {
                    navController.navigate(Destination.LanguageSelection.route)
                },
                onNavigateToPin = { mode ->
                    navController.navigate(Destination.PinScreen.createRoute(mode))
                },
                onNavigateToSyncFrequency = {
                    navController.navigate(Destination.SyncFrequencySelection.route)
                },
                onNavigateToAbout = {
                    navController.navigate(Destination.About.route)
                }
            )
        }

        composable(Destination.ColorPaletteSelection.route) {
            val viewModel: ColorPaletteViewModel = viewModel(factory = viewModelFactory)
            LaunchedEffect(Unit) {
                onScaffoldStateChanged(
                    ScaffoldState(
                        topBarState = TopBarState(
                            title = navController.context.getString(R.string.top_bar_color_palette_title),
                            navigationAction = TopBarAction(
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
                        isBottomBarVisible = false
                    )
                )
            }
            ColorPaletteScreen(viewModel = viewModel)
        }
        composable(Destination.Haptics.route) {
            val viewModel: HapticsScreenViewModel = viewModel(factory = viewModelFactory)
            LaunchedEffect(Unit) {
                onScaffoldStateChanged(
                    ScaffoldState(
                        topBarState = TopBarState(
                            title = navController.context.getString(R.string.top_bar_haptics_title),
                            navigationAction = TopBarAction(
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
                        isBottomBarVisible = false
                    )
                )
            }
            HapticsScreen(viewModel = viewModel)
        }
        composable(Destination.LanguageSelection.route) {
            val viewModel: LanguageScreenViewModel = viewModel(factory = viewModelFactory)
            val lifecycleOwner = LocalLifecycleOwner.current

            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        viewModel.onResume()
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            LaunchedEffect(Unit) {
                onScaffoldStateChanged(
                    ScaffoldState(
                        topBarState = TopBarState(
                            title = navController.context.getString(R.string.top_bar_language_title),
                            navigationAction = TopBarAction(
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
                        isBottomBarVisible = false
                    )
                )
            }
            LanguageScreen(viewModel = viewModel)
        }

        composable(Destination.SyncFrequencySelection.route) {
            val viewModel: SyncFrequencyViewModel = viewModel(factory = viewModelFactory)
            LaunchedEffect(Unit) {
                onScaffoldStateChanged(
                    ScaffoldState(
                        topBarState = TopBarState(
                            title = navController.context.getString(R.string.top_bar_sync_frequency_title),
                            navigationAction = TopBarAction(
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
                        isBottomBarVisible = false
                    )
                )
            }
            SyncFrequencyScreen(viewModel = viewModel)
        }

        composable(Destination.About.route) {
            val viewModel: AboutViewModel = viewModel(factory = viewModelFactory)
            LaunchedEffect(Unit) {
                onScaffoldStateChanged(
                    ScaffoldState(
                        topBarState = TopBarState(
                            title = navController.context.getString(R.string.top_bar_about_title),
                            navigationAction = TopBarAction(
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
                        isBottomBarVisible = false
                    )
                )
            }
            AboutScreen(viewModel = viewModel)
        }

        composable(
            route = Destination.PinScreen.route,
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val viewModel: PinScreenViewModel = viewModel(factory = viewModelFactory)
            val mode = PinMode.valueOf(backStackEntry.arguments?.getString("mode") ?: PinMode.VERIFY.name)

            LaunchedEffect(viewModel) {
                viewModel.initialize(mode)
            }

            LaunchedEffect(Unit) {
                onScaffoldStateChanged(ScaffoldState())
            }

            PinScreen(
                navController = navController,
                viewModel = viewModel,
                onAuthSuccess = {
                    navController.popBackStack()
                }
            )
        }
    }
}