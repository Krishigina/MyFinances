package com.myfinances.ui.screens.account

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.myfinances.R
import com.myfinances.domain.entity.Account
import com.myfinances.ui.components.ItemType
import com.myfinances.ui.components.LeadingIcon
import com.myfinances.ui.components.ListItem
import com.myfinances.ui.components.ListItemModel
import com.myfinances.ui.components.TrailingContent
import com.myfinances.ui.util.formatCurrency

@Composable
fun AccountScreen(
    viewModel: AccountViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is AccountUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is AccountUiState.Success -> {
                AccountScreenContent(account = state.account)
            }

            is AccountUiState.Error -> {
                Text(
                    text = state.message,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }

            is AccountUiState.NoInternet -> {
                Text(
                    text = stringResource(id = R.string.no_internet_connection),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

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
        showTrailingArrow = false
    )

    val currencyItem = ListItemModel(
        id = "currency_item",
        type = ItemType.TOTAL,
        leadingIcon = null,
        title = stringResource(id = R.string.currency),
        trailingContent = TrailingContent.TextOnly(account.currency),
        showTrailingArrow = false
    )

    Column(modifier = Modifier.fillMaxSize()) {
        ListItem(model = balanceItem)
        Divider()
        ListItem(model = currencyItem)
    }
}

@Preview(showBackground = true)
@Composable
fun AccountScreenPreview() {
    val previewAccount = Account(
        id = 1,
        name = "Тестовый счет",
        balance = 12345.67,
        currency = "RUB",
        emoji = "💰"
    )
    AccountScreenContent(account = previewAccount)
}