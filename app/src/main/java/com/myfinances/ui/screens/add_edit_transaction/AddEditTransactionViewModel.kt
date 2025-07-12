package com.myfinances.ui.screens.add_edit_transaction

import androidx.lifecycle.SavedStateHandle
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
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.util.Calendar
import java.util.Date

class AddEditTransactionViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val createTransactionUseCase: CreateTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val getTransactionDetailsUseCase: GetTransactionDetailsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val getAccountUseCase: GetAccountUseCase,
    private val accountUpdateManager: AccountUpdateManager,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle): AddEditTransactionViewModel
    }

    private val _uiState =
        MutableStateFlow<AddEditTransactionUiState>(AddEditTransactionUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val transactionId: Int? = savedStateHandle.get<String>("transactionId")?.toIntOrNull()
    private val transactionType: TransactionTypeFilter =
        savedStateHandle.get<TransactionTypeFilter>("transactionType")!!

    init {
        loadInitialData()
    }

    // ... остальной код ViewModel остается без изменений ...
    fun onEvent(event: AddEditTransactionEvent) {
        val currentState = _uiState.value
        if (currentState !is AddEditTransactionUiState.Success) return

        when (event) {
            is AddEditTransactionEvent.AmountChanged -> _uiState.update { currentState.copy(amount = event.amount) }
            is AddEditTransactionEvent.CommentChanged -> _uiState.update { currentState.copy(comment = event.comment) }
            is AddEditTransactionEvent.CategorySelected -> _uiState.update {
                currentState.copy(selectedCategory = event.category, showCategoryPicker = false)
            }

            is AddEditTransactionEvent.DateSelected -> {
                val calendar = Calendar.getInstance().apply { time = currentState.date }
                val newDateCalendar = Calendar.getInstance().apply { time = event.date }
                newDateCalendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
                newDateCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
                _uiState.update {
                    currentState.copy(
                        date = newDateCalendar.time,
                        showDatePicker = false
                    )
                }
            }

            is AddEditTransactionEvent.TimeChanged -> {
                val calendar = Calendar.getInstance().apply { time = currentState.date }
                calendar.set(Calendar.HOUR_OF_DAY, event.hour)
                calendar.set(Calendar.MINUTE, event.minute)
                _uiState.update { currentState.copy(date = calendar.time, showTimePicker = false) }
            }
            AddEditTransactionEvent.SaveTransaction -> saveTransaction()
            AddEditTransactionEvent.DeleteTransaction -> deleteTransaction()
            AddEditTransactionEvent.ToggleDatePicker -> _uiState.update {
                currentState.copy(
                    showDatePicker = !currentState.showDatePicker
                )
            }

            AddEditTransactionEvent.ToggleTimePicker -> _uiState.update {
                currentState.copy(
                    showTimePicker = !currentState.showTimePicker
                )
            }

            AddEditTransactionEvent.ToggleCategoryPicker -> _uiState.update {
                currentState.copy(
                    showCategoryPicker = !currentState.showCategoryPicker
                )
            }
            AddEditTransactionEvent.DismissErrorDialog -> _uiState.update { currentState.copy(error = null) }
            AddEditTransactionEvent.ShowDeleteConfirmation -> _uiState.update {
                currentState.copy(
                    showDeleteConfirmation = true
                )
            }

            AddEditTransactionEvent.DismissDeleteConfirmation -> _uiState.update {
                currentState.copy(
                    showDeleteConfirmation = false
                )
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = AddEditTransactionUiState.Loading

            val accountDeferred = async { getAccountUseCase() }
            val categoriesDeferred = async { getCategoriesUseCase(transactionType) }
            val transactionDeferred =
                transactionId?.let { async { getTransactionDetailsUseCase(it) } }

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

            val title = if (transactionId == null) {
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
                amount = transaction?.amount?.toBigDecimal()?.setScale(2, RoundingMode.UNNECESSARY)
                    ?.toPlainString() ?: "",
                selectedCategory = transaction?.categoryId?.let { catId -> categories.find { it.id == catId } },
                date = transaction?.date ?: Date(),
                comment = transaction?.comment ?: "",
                categories = categories,
                pageTitle = title,
                isEditMode = transactionId != null
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

            val result: Result<Transaction> = if (transactionId == null) {
                createTransactionUseCase(
                    categoryId = currentState.selectedCategory.id,
                    amount = currentState.amount,
                    transactionDate = currentState.date,
                    comment = currentState.comment.takeIf { it.isNotBlank() }
                )
            } else {
                updateTransactionUseCase(
                    transactionId = transactionId,
                    categoryId = currentState.selectedCategory.id,
                    amount = currentState.amount,
                    transactionDate = currentState.date,
                    comment = currentState.comment.takeIf { it.isNotBlank() }
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
        val currentTransactionId = transactionId ?: return

        viewModelScope.launch {
            _uiState.update {
                currentState.copy(
                    isSaving = true,
                    showDeleteConfirmation = false,
                    error = null
                )
            }

            when (val result = deleteTransactionUseCase(currentTransactionId)) {
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
            _uiState.value = AddEditTransactionUiState.Success(
                pageTitle = resourceProvider.getString(R.string.error_dialog_title),
                error = error,
                isEditMode = transactionId != null
            )
        }
    }
}