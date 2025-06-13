package com.myfinances.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.myfinances.ui.navigation.Destination

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val destinations = listOf(
        Destination.Expenses,
        Destination.Income,
        Destination.Score,
        Destination.Articles,
        Destination.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        destinations.forEach { destination ->
            NavigationBarItem(
                label = { Text(text = stringResource(id = destination.title)) },
                icon = {
                    Icon(
                        painterResource(id = destination.icon),
                        contentDescription = stringResource(id = destination.title)
                    )
                },
                selected = currentRoute == destination.route,
                onClick = {
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