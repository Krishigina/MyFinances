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
    data object DismissErrorDialog : AddEditTransactionEvent
    data object ShowDeleteConfirmation : AddEditTransactionEvent
    data object HideDeleteConfirmation : AddEditTransactionEvent
    data object NavigateBack : AddEditTransactionEvent

    data object ShowDatePicker : AddEditTransactionEvent
    data object HideDatePicker : AddEditTransactionEvent
    data object ShowTimePicker : AddEditTransactionEvent
    data object HideTimePicker : AddEditTransactionEvent
    data object ShowCategoryPicker : AddEditTransactionEvent
    data object HideCategoryPicker : AddEditTransactionEvent
}