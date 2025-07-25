package com.myfinances.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.myfinances.R
import com.myfinances.domain.entity.TransactionTypeFilter

sealed class Destination(
    val route: String,
    @StringRes val title: Int? = null,
    @DrawableRes val icon: Int? = null,
) {
    data object Expenses : Destination(
        route = "expenses",
        title = R.string.botton_nav_label_expenses,
        icon = R.drawable.ic_bottom_nav_expenses
    )

    data object ExpensesList : Destination(route = "expenses_list")

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

    data object ColorPaletteSelection : Destination(route = "color_palette_selection")
    data object Haptics : Destination(route = "haptics")
    data object LanguageSelection : Destination(route = "language_selection")
    data object SyncFrequencySelection : Destination(route = "sync_frequency_selection")

    data object PinScreen : Destination(route = "pin/{mode}") {
        fun createRoute(mode: PinMode): String {
            return "pin/${mode.name}"
        }
    }

    data object History : Destination(route = "history/{transactionType}/{parentRoute}") {
        fun createRoute(filter: TransactionTypeFilter, parentRoute: String): String {
            return "history/${filter.name}/$parentRoute"
        }
    }

    data object Analysis : Destination(route = "analysis/{transactionType}/{parentRoute}") {
        fun createRoute(filter: TransactionTypeFilter, parentRoute: String): String {
            return "analysis/${filter.name}/$parentRoute"
        }
    }

    data object AddEditTransaction :
        Destination(route = "add_edit_transaction/{transactionType}/{transactionId}/{parentRoute}") {
        fun createRoute(
            transactionType: TransactionTypeFilter,
            parentRoute: String,
            transactionId: Int? = null
        ): String {
            val id = transactionId ?: -1
            return "add_edit_transaction/${transactionType.name}/$id/$parentRoute"
        }
    }
}