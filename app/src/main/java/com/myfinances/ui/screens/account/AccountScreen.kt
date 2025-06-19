package com.myfinances.ui.screens.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.myfinances.R
import com.myfinances.data.MockData
import com.myfinances.domain.entity.Account
import com.myfinances.ui.components.ItemType
import com.myfinances.ui.components.LeadingIcon
import com.myfinances.ui.components.ListItem
import com.myfinances.ui.components.ListItemModel
import com.myfinances.ui.components.TrailingContent
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AccountScreenContent(
    account: Account
) {
    val balanceItem = ListItemModel(
        id = "balance_item",
        type = ItemType.TOTAL,
        leadingIcon = LeadingIcon.Emoji(account.emoji),
        title = stringResource(id = R.string.balance),
        trailingContent = TrailingContent.TextOnly(formatCurrency(account.balance)),
        useWhiteIconBackground = true,
        showTrailingArrow = true
    )

    val currencyItem = ListItemModel(
        id = "currency_item",
        type = ItemType.TOTAL,
        leadingIcon = null,
        title = stringResource(id = R.string.currency),
        trailingContent = TrailingContent.TextOnly(account.currency),
        showTrailingArrow = true
    )

    Column(modifier = Modifier.fillMaxSize()) {
        ListItem(model = balanceItem)
        Divider()
        ListItem(model = currencyItem)
    }
}

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    format.maximumFractionDigits = 0
    return format.format(amount).replace(" ", "\u00A0")
}

@Composable
fun AccountScreen() {
    val account = MockData.account
    AccountScreenContent(account = account)
}

@Preview(showBackground = true)
@Composable
fun AccountScreenPreview() {
    AccountScreen()
}