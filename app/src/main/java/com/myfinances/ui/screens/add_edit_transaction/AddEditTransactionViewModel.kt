package com.myfinances.ui.screens.add_edit_transaction

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.R
import com.myfinances.data.manager.AccountUpdateManager
import com.myfinances.domain.entity.Account
import com.myfinances.domain.entity.Category
import com.myfinances.domain.entity.Transaction
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.domain.usecase.CreateTransactionUseCase
import com.myfinances.domain.usecase.DeleteTransactionUseCase
import com.myfinances.domain.usecase.GetAccountUseCase
import com.myfinances.domain.usecase.GetCategoriesUseCase
import com.myfinances.domain.usecase.GetTransactionDetailsUseCase
import com.myfinances.domain.usecase.UpdateTransactionUseCase
import com.myfinances.domain.util.Result
import com.myfinances.ui.util.ResourceProvider
import com.myfinances.ui.viewmodel.TopBarAction
import com.myfinances.ui.viewmodel.TopBarState
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class AddEditTransactionViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val createTransactionUseCase: CreateTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val getTransactionDetailsUseCase: GetTransactionDetailsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val getAccountUseCase: GetAccountUseCase,
    private val accountUpdateManager: AccountUpdateManager,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<AddEditTransactionUiState>(AddEditTransactionUiState.Loading)
    val uiState = _uiState.asStateFlow()

    val topBarState: MutableStateFlow<TopBarState> = MutableStateFlow(TopBarState())

    private var transactionId: Int = -1
    private var isEditMode: Boolean = false
    private lateinit var transactionType: TransactionTypeFilter

    fun initialize(id: Int, type: TransactionTypeFilter) {
        if (this::transactionType.isInitialized) return

        this.transactionId = id
        this.isEditMode = id != -1
        this.transactionType = type

        Log.d("DEBUG_NAV", "[ViewModel] Initialized with transactionId: $transactionId, isEditMode: $isEditMode")
        loadInitialData()
        observeUiStateForTopBar()
    }

    private fun observeUiStateForTopBar() {
        viewModelScope.launch {
            _uiState.collect { state ->
                val successState = state as? AddEditTransactionUiState.Success
                val newTopBarState = TopBarState(
                    title = successState?.pageTitle ?: "",
                    navigationAction = TopBarAction(
                        id = "back",
                        onAction = { onEvent(AddEditTransactionEvent.NavigateBack) },
                        content = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_top_bar_cancel),
                                contentDescription = resourceProvider.getString(R.string.action_cancel)
                            )
                        }
                    ),
                    actions = listOf(
                        TopBarAction(
                            id = "save",
                            isEnabled = successState?.let { it.selectedCategory != null && it.amount.isNotBlank() } ?: false,
                            onAction = { onEvent(AddEditTransactionEvent.SaveTransaction) },
                            content = {
                                if (successState?.isSaving == true) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_top_bar_confirm),
                                        contentDescription = resourceProvider.getString(R.string.action_save)
                                    )
                                }
                            }
                        )
                    )
                )
                if (topBarState.value != newTopBarState) {
                    topBarState.value = newTopBarState
                }
            }
        }
    }

    fun onEvent(event: AddEditTransactionEvent) {
        val currentState = _uiState.value
        if (currentState !is AddEditTransactionUiState.Success && event !is AddEditTransactionEvent.NavigateBack) return

        when (event) {
            is AddEditTransactionEvent.AmountChanged -> _uiState.update {
                (currentState as AddEditTransactionUiState.Success).copy(
                    amount = event.amount
                )
            }

            is AddEditTransactionEvent.CommentChanged -> _uiState.update {
                (currentState as AddEditTransactionUiState.Success).copy(
                    comment = event.comment
                )
            }
            is AddEditTransactionEvent.CategorySelected -> _uiState.update {
                (currentState as AddEditTransactionUiState.Success).copy(
                    selectedCategory = event.category,
                    showCategoryPicker = false
                )
            }
            is AddEditTransactionEvent.DateSelected -> {
                val calendar = Calendar.getInstance()
                    .apply { time = (currentState as AddEditTransactionUiState.Success).date }
                val newDateCalendar = Calendar.getInstance().apply { time = event.date }
                newDateCalendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
                newDateCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
                _uiState.update {
                    (currentState as AddEditTransactionUiState.Success).copy(
                        date = newDateCalendar.time,
                        showDatePicker = false
                    )
                }
            }

            is AddEditTransactionEvent.TimeChanged -> {
                val calendar = Calendar.getInstance()
                    .apply { time = (currentState as AddEditTransactionUiState.Success).date }
                calendar.set(Calendar.HOUR_OF_DAY, event.hour)
                calendar.set(Calendar.MINUTE, event.minute)
                _uiState.update {
                    (currentState as AddEditTransactionUiState.Success).copy(
                        date = calendar.time,
                        showTimePicker = false
                    )
                }
            }
            AddEditTransactionEvent.SaveTransaction -> saveTransaction()
            AddEditTransactionEvent.DeleteTransaction -> deleteTransaction()
            AddEditTransactionEvent.DismissErrorDialog -> _uiState.update {
                (currentState as AddEditTransactionUiState.Success).copy(error = null)
            }
            AddEditTransactionEvent.ShowDeleteConfirmation -> _uiState.update {
                (currentState as AddEditTransactionUiState.Success).copy(showDeleteConfirmation = true)
            }
            AddEditTransactionEvent.HideDeleteConfirmation -> _uiState.update {
                (currentState as AddEditTransactionUiState.Success).copy(showDeleteConfirmation = false)
            }

            AddEditTransactionEvent.NavigateBack -> {
                val state = _uiState.value
                if (state is AddEditTransactionUiState.Success) {
                    _uiState.update { state.copy(closeScreen = true) }
                }
            }

            AddEditTransactionEvent.ShowDatePicker -> _uiState.update { (currentState as AddEditTransactionUiState.Success).copy(showDatePicker = true) }
            AddEditTransactionEvent.HideDatePicker -> _uiState.update { (currentState as AddEditTransactionUiState.Success).copy(showDatePicker = false) }
            AddEditTransactionEvent.ShowTimePicker -> _uiState.update { (currentState as AddEditTransactionUiState.Success).copy(showTimePicker = true) }
            AddEditTransactionEvent.HideTimePicker -> _uiState.update { (currentState as AddEditTransactionUiState.Success).copy(showTimePicker = false) }
            AddEditTransactionEvent.ShowCategoryPicker -> _uiState.update { (currentState as AddEditTransactionUiState.Success).copy(showCategoryPicker = true) }
            AddEditTransactionEvent.HideCategoryPicker -> _uiState.update { (currentState as AddEditTransactionUiState.Success).copy(showCategoryPicker = false) }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = AddEditTransactionUiState.Loading

            val accountDeferred = async { getAccountUseCase() }
            val categoriesDeferred = async { getCategoriesUseCase(transactionType) }
            val transactionDeferred = if (isEditMode) {
                async { getTransactionDetailsUseCase(transactionId) }
            } else {
                null
            }

            val accountResult: Result<Account> = accountDeferred.await()
            val categoriesResult: Result<List<Category>> = categoriesDeferred.await()
            val transactionResult: Result<Transaction>? = transactionDeferred?.await()

            val results: List<Result<Any>> =
                listOfNotNull(accountResult, categoriesResult, transactionResult)
            val firstError = results.filterIsInstance<Result.Error>().firstOrNull()
            val isNetworkError = results.any { it is Result.NetworkError }

            if (isNetworkError) {
                showErrorDialog("Ошибка сети. Проверьте подключение.") { loadInitialData() }
                return@launch
            }
            if (firstError != null) {
                showErrorDialog(
                    firstError.exception.message ?: "Неизвестная ошибка"
                ) { loadInitialData() }
                return@launch
            }

            val account = (accountResult as Result.Success<Account>).data
            val categories = (categoriesResult as Result.Success<List<Category>>).data
            val transaction = (transactionResult as? Result.Success<Transaction>)?.data

            val title = if (!isEditMode) {
                if (transactionType == TransactionTypeFilter.EXPENSE)
                    resourceProvider.getString(R.string.add_expense_title)
                else
                    resourceProvider.getString(R.string.add_income_title)
            } else {
                if (transactionType == TransactionTypeFilter.EXPENSE)
                    resourceProvider.getString(R.string.edit_expense_title)
                else
                    resourceProvider.getString(R.string.edit_income_title)
            }

            _uiState.value = AddEditTransactionUiState.Success(
                account = account,
                accountId = transaction?.accountId ?: account.id,
                amount = transaction?.amount?.toBigDecimal()?.setScale(2, RoundingMode.UNNECESSARY)
                    ?.toPlainString() ?: "",
                selectedCategory = transaction?.categoryId?.let { catId -> categories.find { it.id == catId } },
                date = transaction?.date ?: Date(),
                comment = transaction?.comment ?: "",
                categories = categories,
                pageTitle = title,
                isEditMode = isEditMode,
                transactionType = transactionType
            )
        }
    }

    private fun saveTransaction() {
        val currentState = _uiState.value as? AddEditTransactionUiState.Success ?: return

        if (currentState.selectedCategory == null) {
            showErrorDialog("Необходимо выбрать категорию", onRetry = {
                _uiState.update { currentState.copy(error = null) }
            })
            return
        }

        viewModelScope.launch {
            _uiState.update { currentState.copy(isSaving = true, error = null) }

            val result: Result<Transaction> = if (!isEditMode) {
                createTransactionUseCase(
                    categoryId = currentState.selectedCategory.id,
                    amount = currentState.amount,
                    transactionDate = currentState.date,
                    comment = currentState.comment
                )
            } else {
                updateTransactionUseCase(
                    transactionId = transactionId,
                    accountId = currentState.accountId,
                    categoryId = currentState.selectedCategory.id,
                    amount = currentState.amount,
                    transactionDate = currentState.date,
                    comment = currentState.comment
                )
            }

            when (result) {
                is Result.Success -> {
                    accountUpdateManager.notifyAccountUpdated()
                    _uiState.update { currentState.copy(isSaving = false, closeScreen = true) }
                }

                is Result.Error -> {
                    _uiState.update { currentState.copy(isSaving = false) }
                    showErrorDialog(
                        result.exception.message ?: "Ошибка сохранения"
                    ) { saveTransaction() }
                }

                is Result.NetworkError -> {
                    _uiState.update { currentState.copy(isSaving = false) }
                    showErrorDialog("Ошибка сети. Проверьте подключение.") { saveTransaction() }
                }
            }
        }
    }

    private fun deleteTransaction() {
        val currentState = _uiState.value as? AddEditTransactionUiState.Success ?: return
        if (!isEditMode) return

        viewModelScope.launch {
            _uiState.update {
                currentState.copy(
                    isSaving = true,
                    showDeleteConfirmation = false,
                    error = null
                )
            }

            when (val result = deleteTransactionUseCase(transactionId)) {
                is Result.Success -> {
                    accountUpdateManager.notifyAccountUpdated()
                    _uiState.update { currentState.copy(isSaving = false, closeScreen = true) }
                }

                is Result.Error -> {
                    _uiState.update { currentState.copy(isSaving = false) }
                    showErrorDialog(
                        result.exception.message ?: "Ошибка удаления"
                    ) { deleteTransaction() }
                }

                is Result.NetworkError -> {
                    _uiState.update { currentState.copy(isSaving = false) }
                    showErrorDialog("Ошибка сети. Проверьте подключение.") { deleteTransaction() }
                }
            }
        }
    }

    private fun showErrorDialog(message: String, onRetry: () -> Unit) {
        val currentState = _uiState.value
        val error = AddEditTransactionUiState.ErrorState(message, onRetry)
        if (currentState is AddEditTransactionUiState.Success) {
            _uiState.update { currentState.copy(error = error) }
        } else {
            val title = if (!isEditMode) {
                if (transactionType == TransactionTypeFilter.EXPENSE)
                    resourceProvider.getString(R.string.add_expense_title)
                else
                    resourceProvider.getString(R.string.add_income_title)
            } else {
                if (transactionType == TransactionTypeFilter.EXPENSE)
                    resourceProvider.getString(R.string.edit_expense_title)
                else
                    resourceProvider.getString(R.string.edit_income_title)
            }
            _uiState.value = AddEditTransactionUiState.Success(
                pageTitle = title,
                error = error,
                isEditMode = isEditMode,
                transactionType = this.transactionType,
                accountId = -1
            )
        }
    }
}