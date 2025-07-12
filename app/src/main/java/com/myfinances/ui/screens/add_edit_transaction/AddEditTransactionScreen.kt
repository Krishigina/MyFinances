package com.myfinances.ui.screens.add_edit_transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.myfinances.MyFinancesApplication
import com.myfinances.R
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.ui.components.CategoryPickerBottomSheet
import com.myfinances.ui.components.ErrorDialog
import com.myfinances.ui.components.HistoryDatePickerDialog
import com.myfinances.ui.components.ItemType
import com.myfinances.ui.components.ListItem
import com.myfinances.ui.components.ListItemModel
import com.myfinances.ui.components.MainTopBar
import com.myfinances.ui.components.TimePickerDialog
import com.myfinances.ui.components.TrailingContent
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
inline fun <reified T : ViewModel> assistedViewModel(
    crossinline viewModelProducer: (SavedStateHandle) -> T
): T {
    return viewModel(
        modelClass = T::class.java,
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                throw IllegalStateException("Factory not intended for direct use")
            }
        },

        factory = object : AbstractSavedStateViewModelFactory() {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                return viewModelProducer(handle) as T
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionScreen(
    navController: NavController,
    savedStateHandle: SavedStateHandle,
    viewModel: AddEditTransactionViewModel = assistedViewModel { handle ->
        val appComponent =
            (LocalContext.current.applicationContext as MyFinancesApplication).appComponent
        appComponent.viewModelComponentFactory().create().getAddEditTransactionViewModelFactory()
            .create(handle)
    }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val transactionType = savedStateHandle.get<TransactionTypeFilter>("transactionType")
        ?: TransactionTypeFilter.EXPENSE

    LaunchedEffect(uiState) {
        val successState = uiState as? AddEditTransactionUiState.Success
        if (successState?.closeScreen == true) {
            navController.popBackStack()
        }
    }

    when (val state = uiState) {
        is AddEditTransactionUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is AddEditTransactionUiState.Success -> {
            val calendar = remember(state.date) {
                Calendar.getInstance().apply { time = state.date }
            }

            if (state.showDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = state.date.time,
                    yearRange = (2020..Calendar.getInstance().get(Calendar.YEAR))
                )
                HistoryDatePickerDialog(
                    datePickerState = datePickerState,
                    onDismissRequest = { viewModel.onEvent(AddEditTransactionEvent.ToggleDatePicker) },
                    onConfirm = { timestamp ->
                        timestamp?.let {
                            viewModel.onEvent(
                                AddEditTransactionEvent.DateSelected(
                                    Date(
                                        it
                                    )
                                )
                            )
                        }
                    }
                )
            }

            if (state.showTimePicker) {
                TimePickerDialog(
                    onDismiss = { viewModel.onEvent(AddEditTransactionEvent.ToggleTimePicker) },
                    onConfirm = { hour, minute ->
                        viewModel.onEvent(AddEditTransactionEvent.TimeChanged(hour, minute))
                    },
                    initialHour = calendar.get(Calendar.HOUR_OF_DAY),
                    initialMinute = calendar.get(Calendar.MINUTE)
                )
            }

            if (state.showCategoryPicker) {
                CategoryPickerBottomSheet(
                    categories = state.categories,
                    onCategorySelected = {
                        viewModel.onEvent(
                            AddEditTransactionEvent.CategorySelected(
                                it
                            )
                        )
                    },
                    onDismiss = { viewModel.onEvent(AddEditTransactionEvent.ToggleCategoryPicker) }
                )
            }

            state.error?.let { errorState ->
                ErrorDialog(
                    title = stringResource(R.string.error_dialog_title),
                    message = errorState.message,
                    onConfirm = {
                        errorState.onRetry()
                        viewModel.onEvent(AddEditTransactionEvent.DismissErrorDialog)
                    },
                    onDismiss = { viewModel.onEvent(AddEditTransactionEvent.DismissErrorDialog) }
                )
            }

            if (state.showDeleteConfirmation) {
                AlertDialog(
                    onDismissRequest = { viewModel.onEvent(AddEditTransactionEvent.DismissDeleteConfirmation) },
                    title = { Text(stringResource(id = R.string.delete_confirmation_title)) },
                    text = { Text(stringResource(id = R.string.delete_confirmation_message)) },
                    confirmButton = {
                        TextButton(
                            onClick = { viewModel.onEvent(AddEditTransactionEvent.DeleteTransaction) },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text(stringResource(id = R.string.action_delete))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.onEvent(AddEditTransactionEvent.DismissDeleteConfirmation) }) {
                            Text(stringResource(id = R.string.action_cancel))
                        }
                    }
                )
            }

            Scaffold(
                topBar = {
                    MainTopBar(
                        title = state.pageTitle,
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = stringResource(R.string.action_back)
                                )
                            }
                        },
                        actions = {
                            if (state.isSaving) {
                                CircularProgressIndicator(modifier = Modifier.padding(end = 16.dp))
                            } else {
                                IconButton(onClick = { viewModel.onEvent(AddEditTransactionEvent.SaveTransaction) }) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = stringResource(R.string.action_save)
                                    )
                                }
                            }
                        }
                    )
                }
            ) { paddingValues ->
                AddEditTransactionContent(
                    state = state,
                    transactionType = transactionType,
                    onEvent = viewModel::onEvent,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun AddEditTransactionContent(
    state: AddEditTransactionUiState.Success,
    transactionType: TransactionTypeFilter,
    onEvent: (AddEditTransactionEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val amountWithCurrency = remember(state.amount, state.account) {
        if (state.amount.isNotBlank() && state.account != null) {
            val amountValue = state.amount.replace('.', ',')
            val currencySymbol = com.myfinances.ui.util.getCurrencySymbol(state.account.currency)
            "$amountValue $currencySymbol"
        } else {
            ""
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ListItem(
                model = ListItemModel(
                    id = "account",
                    title = stringResource(R.string.account),
                    type = ItemType.SETTING,
                    trailingContent = TrailingContent.TextWithArrow(
                        text = state.account?.name ?: "..."
                    ),
                    onClick = { /* Not editable per design */ }
                )
            )
            Divider()
            ListItem(
                model = ListItemModel(
                    id = "category",
                    title = stringResource(R.string.category),
                    type = ItemType.SETTING,
                    trailingContent = TrailingContent.TextWithArrow(
                        text = state.selectedCategory?.name
                            ?: stringResource(R.string.select_category)
                    ),
                    onClick = { onEvent(AddEditTransactionEvent.ToggleCategoryPicker) }
                )
            )
            Divider()

            ListItem(
                model = ListItemModel(
                    id = "amount",
                    title = stringResource(R.string.amount),
                    type = ItemType.SETTING,
                    trailingContent = TrailingContent.TextOnly(amountWithCurrency),
                    showTrailingArrow = false
                ),
            )
            Divider()

            ListItem(
                model = ListItemModel(
                    id = "date",
                    title = stringResource(R.string.date),
                    type = ItemType.SETTING,
                    trailingContent = TrailingContent.TextWithArrow(
                        text = dateFormat.format(state.date)
                    ),
                    onClick = { onEvent(AddEditTransactionEvent.ToggleDatePicker) }
                )
            )
            Divider()
            ListItem(
                model = ListItemModel(
                    id = "time",
                    title = stringResource(R.string.time),
                    type = ItemType.SETTING,
                    trailingContent = TrailingContent.TextWithArrow(
                        text = timeFormat.format(state.date)
                    ),
                    onClick = { onEvent(AddEditTransactionEvent.ToggleTimePicker) }
                )
            )
            Divider()

            BasicTextField(
                value = state.comment,
                onValueChange = { onEvent(AddEditTransactionEvent.CommentChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground),
                keyboardOptions = KeyboardOptions.Default,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    if (state.comment.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.enter_comment),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    innerTextField()
                }
            )
            Divider()
        }

        Spacer(modifier = Modifier.weight(1f))

        if (state.isEditMode) {
            Button(
                onClick = { onEvent(AddEditTransactionEvent.ShowDeleteConfirmation) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE46962),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (transactionType == TransactionTypeFilter.EXPENSE)
                        stringResource(R.string.delete_expense_button)
                    else
                        stringResource(R.string.delete_income_button)
                )
            }
        }
    }
}