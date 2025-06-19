package com.myfinances.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.myfinances.R
import com.myfinances.ui.components.BottomNavigationBar
import com.myfinances.ui.components.MainFloatingActionButton
import com.myfinances.ui.components.MainTopBar
import com.myfinances.ui.navigation.Destination
import com.myfinances.ui.navigation.NavigationGraph

@Composable
fun MainScreen() {
    val mainNavController = rememberNavController()

    val navBackStackEntry by mainNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            when (currentRoute) {
                Destination.Expenses.route -> {
                    MainTopBar(
                        title = stringResource(id = R.string.top_bar_expenses_today_title),
                        actions = {
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    painterResource(R.drawable.ic_top_bar_history),
                                    contentDescription = stringResource(id = R.string.top_bar_icon_history)
                                )
                            }
                        }
                    )
                }

                Destination.Income.route -> {
                    MainTopBar(
                        title = stringResource(id = R.string.top_bar_income_today_title),
                        actions = {
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    painterResource(R.drawable.ic_top_bar_history),
                                    contentDescription = stringResource(id = R.string.top_bar_icon_history)
                                )
                            }
                        }
                    )
                }

                Destination.Account.route -> {
                    MainTopBar(
                        title = stringResource(id = R.string.top_bar_my_account_title),
                        actions = {
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    painterResource(R.drawable.ic_top_bar_edit),
                                    contentDescription = stringResource(id = R.string.top_bar_icon_edit)
                                )
                            }
                        }
                    )
                }

                Destination.Articles.route -> {
                    MainTopBar(
                        title = stringResource(id = R.string.top_bar_my_articles_title)
                    )
                }

                Destination.Settings.route -> {
                    MainTopBar(
                        title = stringResource(id = R.string.top_bar_settings_title)
                    )
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(navController = mainNavController)
        },
        floatingActionButton = {
            when (currentRoute) {
                Destination.Expenses.route,
                Destination.Income.route,
                Destination.Account.route -> {
                    MainFloatingActionButton {
                        // TODO
                    }
                }

                else -> {
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding)
        ) {
            NavigationGraph(navController = mainNavController)
        }
    }
}