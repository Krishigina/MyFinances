package com.myfinances.ui.screens.history

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.myfinances.ui.theme.BrightBlack
import com.myfinances.ui.theme.BrightGreen
import com.myfinances.ui.theme.PastelGreen

/**
 * Кастомизированный диалог для выбора даты с определенной цветовой схемой.
 *
 * @param datePickerState Состояние DatePicker, управляемое извне.
 * @param onDismissRequest Коллбэк, вызываемый при закрытии диалога.
 * @param onConfirm Коллбэк, вызываемый при подтверждении выбора даты.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDatePickerDialog(
    datePickerState: DatePickerState,
    onDismissRequest: () -> Unit,
    onConfirm: (Long?) -> Unit
) {
    val greenDatePickerColors = DatePickerDefaults.colors(
        containerColor = PastelGreen,
        selectedDayContainerColor = BrightGreen,
        selectedDayContentColor = BrightBlack,
        todayDateBorderColor = Color.Transparent,
        todayContentColor = BrightBlack,
        dayContentColor = BrightBlack,
        weekdayContentColor = BrightBlack,
        subheadContentColor = BrightBlack,
        yearContentColor = BrightBlack,
        currentYearContentColor = BrightGreen,
        selectedYearContainerColor = BrightGreen,
        selectedYearContentColor = BrightBlack
    )

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            DatePickerDialogButtons(
                onDismiss = onDismissRequest,
                onClear = { datePickerState.selectedDateMillis = null },
                onConfirm = {
                    onConfirm(datePickerState.selectedDateMillis)
                    onDismissRequest()
                }
            )
        },
        dismissButton = null,
        colors = greenDatePickerColors
    ) {
        DatePicker(
            state = datePickerState,
            colors = greenDatePickerColors,
            title = null,
            headline = null,
            showModeToggle = false
        )
    }
}

/**
 * Вспомогательный компонент для кастомных кнопок в DatePickerDialog.
 */
@Composable
private fun DatePickerDialogButtons(
    onDismiss: () -> Unit,
    onClear: () -> Unit,
    onConfirm: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = onClear,
            colors = ButtonDefaults.textButtonColors(contentColor = BrightBlack)
        ) {
            Text("Clear")
        }
        Spacer(modifier = Modifier.weight(1f))
        Row {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = BrightBlack)
            ) {
                Text("Cancel")
            }
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = BrightBlack)
            ) {
                Text("OK")
            }
        }
    }
}