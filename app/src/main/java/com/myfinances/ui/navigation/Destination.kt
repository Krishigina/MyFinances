package com.myfinances.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.myfinances.R

sealed class Destination(
    val route: String,
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
) {
    data object Expenses : Destination(
        route = "expenses",
        title = R.string.botton_nav_label_expenses,
        icon = R.drawable.ic_bottom_nav_expenses
    )

    data object Income : Destination(
        route = "income",
        title = R.string.botton_nav_label_income,
        icon = R.drawable.ic_bottom_nav_income
    )

    data object Account : Destination(
        route = "account",
        title = R.string.botton_nav_label_account,
        icon = R.drawable.ic_bottom_nav_account
    )

    data object Articles : Destination(
        route = "articles",
        title = R.string.botton_nav_label_articles,
        icon = R.drawable.ic_bottom_nav_articles
    )

    data object Settings : Destination(
        route = "settings",
        title = R.string.botton_nav_label_settings,
        icon = R.drawable.ic_bottom_nav_settings
    )
}