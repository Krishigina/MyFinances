package com.myfinances.ui.screens.add_edit_transaction

import com.myfinances.domain.entity.Account
import com.myfinances.domain.entity.Category
import java.util.Date

sealed interface AddEditTransactionUiState {
    data object Loading : AddEditTransactionUiState
    data class Success(
        val account: Account? = null,
        val amount: String = "",
        val selectedCategory: Category? = null,
        val date: Date = Date(),
        val comment: String = "",
        val categories: List<Category> = emptyList(),
        val isEditMode: Boolean,
        val isSaving: Boolean = false,
        val pageTitle: String,
        val showDatePicker: Boolean = false,
        val showTimePicker: Boolean = false,
        val showCategoryPicker: Boolean = false,
        val showDeleteConfirmation: Boolean = false,
        val error: ErrorState? = null,
        val closeScreen: Boolean = false
    ) : AddEditTransactionUiState

    data class ErrorState(
        val message: String,
        val onRetry: () -> Unit
    )
}