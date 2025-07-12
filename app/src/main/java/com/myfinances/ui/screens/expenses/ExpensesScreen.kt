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
import androidx.navigation.NavController
import com.myfinances.R
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.ui.components.ItemType
import com.myfinances.ui.components.LeadingIcon
import com.myfinances.ui.components.ListItem
import com.myfinances.ui.components.ListItemModel
import com.myfinances.ui.components.TrailingContent
import com.myfinances.ui.model.TransactionItemUiModel
import com.myfinances.ui.navigation.Destination

@Composable
fun ExpensesScreen(
    navController: NavController,
    viewModel: ExpensesViewModel
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
                    navController = navController,
                    transactionItems = state.transactionItems,
                    totalAmountFormatted = state.totalAmountFormatted
                )
            }
        }
    }
}

@Composable
private fun ExpensesScreenContent(
    navController: NavController,
    transactionItems: List<TransactionItemUiModel>,
    totalAmountFormatted: String
) {
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

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(items = transactionItems, key = { it.id }) { model ->
                ListItem(
                    model = ListItemModel(
                        id = model.id,
                        title = model.title,
                        subtitle = model.subtitle,
                        type = ItemType.TRANSACTION,
                        leadingIcon = LeadingIcon.Emoji(model.emoji),
                        trailingContent = TrailingContent.TextWithArrow(text = model.amountFormatted),
                        showTrailingArrow = true,
                        onClick = {
                            navController.navigate(
                                Destination.AddEditTransaction.createRoute(
                                    transactionType = TransactionTypeFilter.EXPENSE,
                                    transactionId = model.id.toInt()
                                )
                            )
                        }
                    )
                )
                Divider()
            }
        }
    }
}