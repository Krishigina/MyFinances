package com.myfinances.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.myfinances.ui.navigation.Destination

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val destinations = listOf(
        Destination.Expenses,
        Destination.Income,
        Destination.Account,
        Destination.Articles,
        Destination.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        modifier = modifier
    ) {
        destinations.forEach { destination ->
            if (destination.title != null && destination.icon != null) {
                val selected = if (currentDestination?.route?.startsWith("history") == true) {
                    val parentRouteArg = navBackStackEntry?.arguments?.getString("parentRoute")
                    parentRouteArg == destination.route
                } else {
                    currentDestination?.hierarchy?.any { it.route == destination.route } == true
                }

                NavigationBarItem(
                    label = { Text(text = stringResource(id = destination.title)) },
                    icon = {
                        Icon(
                            painterResource(id = destination.icon),
                            contentDescription = stringResource(id = destination.title)
                        )
                    },
                    selected = selected,
                    onClick = {
                        if (currentDestination?.route?.startsWith("history") == true && !selected) {
                            navController.popBackStack()
                        }

                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}