package com.myfinances.ui.screens.add_edit_transaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.R
import com.myfinances.data.manager.AccountUpdateManager
import com.myfinances.domain.entity.TransactionTypeFilter
import com.myfinances.domain.usecase.CreateTransactionUseCase
import com.myfinances.domain.usecase.GetCategoriesUseCase
import com.myfinances.domain.usecase.GetTransactionDetailsUseCase
import com.myfinances.domain.usecase.UpdateTransactionUseCase
import com.myfinances.domain.util.Result
import com.myfinances.ui.util.ResourceProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.util.Date
import javax.inject.Inject

class AddEditTransactionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val createTransactionUseCase: CreateTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val getTransactionDetailsUseCase: GetTransactionDetailsUseCase,
    private val accountUpdateManager: AccountUpdateManager,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<AddEditTransactionUiState>(AddEditTransactionUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val transactionId: Int? = savedStateHandle.get<String>("transactionId")?.toIntOrNull()
    private val transactionType: TransactionTypeFilter =
        savedStateHandle.get<TransactionTypeFilter>("transactionType")!!

    init {
        loadInitialData()
    }

    fun onEvent(event: AddEditTransactionEvent) {
        val currentState = _uiState.value
        if (currentState !is AddEditTransactionUiState.Success) return

        when (event) {
            is AddEditTransactionEvent.AmountChanged -> _uiState.update { currentState.copy(amount = event.amount) }
            is AddEditTransactionEvent.CommentChanged -> _uiState.update { currentState.copy(comment = event.comment) }
            is AddEditTransactionEvent.CategorySelected -> _uiState.update {
                currentState.copy(
                    selectedCategory = event.category,
                    showCategoryPicker = false
                )
            }

            is AddEditTransactionEvent.DateSelected -> _uiState.update {
                currentState.copy(
                    date = event.date,
                    showDatePicker = false
                )
            }

            AddEditTransactionEvent.SaveTransaction -> saveTransaction()
            AddEditTransactionEvent.ToggleDatePicker -> _uiState.update {
                currentState.copy(
                    showDatePicker = !currentState.showDatePicker
                )
            }

            AddEditTransactionEvent.ToggleCategoryPicker -> _uiState.update {
                currentState.copy(
                    showCategoryPicker = !currentState.showCategoryPicker
                )
            }

            AddEditTransactionEvent.DismissErrorDialog -> _uiState.update { currentState.copy(error = null) }
        }
    }

    private fun loadInitialData() = viewModelScope.launch {
        _uiState.value = AddEditTransactionUiState.Loading

        val categoriesDeferred = async { getCategoriesUseCase(transactionType) }
        val transactionDeferred = transactionId?.let { async { getTransactionDetailsUseCase(it) } }

        val categoriesResult = categoriesDeferred.await()
        val transactionResult = transactionDeferred?.await()

        if (categoriesResult is Result.Error) {
            showErrorDialog(
                categoriesResult.exception.message ?: "Ошибка загрузки категорий"
            ) { loadInitialData() }
            return@launch
        }
        if (transactionResult is Result.Error) {
            showErrorDialog(
                transactionResult.exception.message ?: "Ошибка загрузки транзакции"
            ) { loadInitialData() }
            return@launch
        }
        if (categoriesResult is Result.NetworkError || transactionResult is Result.NetworkError) {
            showErrorDialog("Ошибка сети. Проверьте подключение.") { loadInitialData() }
            return@launch
        }

        val categories = (categoriesResult as Result.Success).data
        val transaction = (transactionResult as? Result.Success)?.data

        val title = if (transactionId == null) {
            if (transactionType == TransactionTypeFilter.EXPENSE)
                resourceProvider.getString(R.string.add_expense_title)
            else
                resourceProvider.getString(R.string.add_income_title)
        } else {
            resourceProvider.getString(R.string.edit_transaction_title)
        }

        _uiState.value = AddEditTransactionUiState.Success(
            amount = transaction?.amount?.toBigDecimal()?.setScale(2, RoundingMode.UNNECESSARY)
                ?.toPlainString() ?: "",
            selectedCategory = transaction?.categoryId?.let { catId -> categories.find { it.id == catId } },
            date = transaction?.date ?: Date(),
            comment = transaction?.comment ?: "",
            categories = categories,
            pageTitle = title
        )
    }

    private fun saveTransaction() {
        val currentState = _uiState.value
        if (currentState !is AddEditTransactionUiState.Success) return

        if (currentState.selectedCategory == null) {
            showErrorDialog("Необходимо выбрать категорию", onRetry = {
                _uiState.update { currentState.copy(error = null) }
            })
            return
        }

        viewModelScope.launch {
            _uiState.update { currentState.copy(isSaving = true, error = null) }

            val result = if (transactionId == null) {
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

            _uiState.update { currentState.copy(isSaving = false) }

            when (result) {
                is Result.Success -> {
                    accountUpdateManager.notifyAccountUpdated()
                    // Навигация назад будет обработана в Composable
                }

                is Result.Error -> showErrorDialog(
                    result.exception.message ?: "Ошибка сохранения"
                ) { saveTransaction() }

                is Result.NetworkError -> showErrorDialog("Ошибка сети. Проверьте подключение.") { saveTransaction() }
            }
        }
    }

    private fun showErrorDialog(message: String, onRetry: () -> Unit) {
        val currentState = _uiState.value
        if (currentState is AddEditTransactionUiState.Success) {
            _uiState.update {
                currentState.copy(error = AddEditTransactionUiState.ErrorState(message, onRetry))
            }
        } else {
            // Если экран еще на стадии Loading
            _uiState.value = AddEditTransactionUiState.Success(
                pageTitle = "", // Заглушка
                error = AddEditTransactionUiState.ErrorState(message, onRetry)
            )
        }
    }
}