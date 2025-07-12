package com.myfinances.ui.screens.add_edit_transaction

import com.myfinances.domain.entity.Category
import java.util.Date

sealed interface AddEditTransactionEvent {
    data class AmountChanged(val amount: String) : AddEditTransactionEvent
    data class CategorySelected(val category: Category) : AddEditTransactionEvent
    data class DateSelected(val date: Date) : AddEditTransactionEvent
    data class TimeChanged(val hour: Int, val minute: Int) : AddEditTransactionEvent
    data class CommentChanged(val comment: String) : AddEditTransactionEvent
    data object SaveTransaction : AddEditTransactionEvent
    data object DeleteTransaction : AddEditTransactionEvent
    data object ToggleDatePicker : AddEditTransactionEvent
    data object ToggleTimePicker : AddEditTransactionEvent
    data object ToggleCategoryPicker : AddEditTransactionEvent
    data object DismissErrorDialog : AddEditTransactionEvent
    data object DismissDeleteConfirmation : AddEditTransactionEvent
    data object ShowDeleteConfirmation : AddEditTransactionEvent
    data object NavigateBack : AddEditTransactionEvent
}