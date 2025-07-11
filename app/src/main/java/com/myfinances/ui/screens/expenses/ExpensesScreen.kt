package com.myfinances.ui.screens.expenses

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.myfinances.R
import com.myfinances.ui.components.ItemType
import com.myfinances.ui.components.ListItem
import com.myfinances.ui.components.ListItemModel
import com.myfinances.ui.components.TrailingContent
import com.myfinances.ui.viewmodel.provideViewModelFactory

@Composable
fun ExpensesScreen(
    viewModel: ExpensesViewModel = viewModel(factory = provideViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is ExpensesUiState.Loading -> {
                CircularProgressIndicator()
            }

            is ExpensesUiState.Content -> {
                ExpensesScreenContent(
                    transactionItems = state.transactionItems,
                    totalAmountFormatted = state.totalAmountFormatted
                )
            }
        }
    }
}

@Composable
private fun ExpensesScreenContent(
    transactionItems: List<ListItemModel>,
    totalAmountFormatted: String
) {
    // Используем Column, чтобы прижать карточку к верху
    Column(modifier = Modifier.fillMaxSize()) {
        ListItem(
            model = ListItemModel(
                id = "total_amount_card_expenses",
                title = stringResource(id = R.string.total_amount_card),
                type = ItemType.TOTAL,
                trailingContent = TrailingContent.TextOnly(totalAmountFormatted),
                showTrailingArrow = false
            )
        )
        Divider()

        // LazyColumn занимает все оставшееся место
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(items = transactionItems, key = { it.id }) { model ->
                ListItem(model = model)
                Divider()
            }
        }
    }
}