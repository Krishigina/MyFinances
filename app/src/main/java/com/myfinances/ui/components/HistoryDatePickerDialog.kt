// app/src/main/java/com/myfinances/ui/components/HistoryDatePickerDialog.kt

package com.myfinances.ui.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.myfinances.R

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
    val datePickerColors = DatePickerDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        selectedDayContainerColor = MaterialTheme.colorScheme.primary,
        selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
        todayDateBorderColor = MaterialTheme.colorScheme.primary,
        todayContentColor = MaterialTheme.colorScheme.primary,
        dayContentColor = MaterialTheme.colorScheme.onSurface,
        weekdayContentColor = MaterialTheme.colorScheme.onSurface,
        subheadContentColor = MaterialTheme.colorScheme.onSurface,
        yearContentColor = MaterialTheme.colorScheme.onSurface,
        currentYearContentColor = MaterialTheme.colorScheme.primary,
        selectedYearContainerColor = MaterialTheme.colorScheme.primary,
        selectedYearContentColor = MaterialTheme.colorScheme.onPrimary,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        headlineContentColor = MaterialTheme.colorScheme.onSurface
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
        colors = datePickerColors
    ) {
        DatePicker(
            state = datePickerState,
            colors = datePickerColors,
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
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(stringResource(id = R.string.action_clear))
        }
        Spacer(modifier = Modifier.weight(1f))
        Row {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(stringResource(id = R.string.action_cancel))
            }
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(stringResource(id = R.string.action_ok))
            }
        }
    }
}