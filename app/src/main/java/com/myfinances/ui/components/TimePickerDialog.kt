package com.myfinances.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.myfinances.R
import com.myfinances.ui.theme.BrightBlack
import com.myfinances.ui.theme.BrightGreen
import com.myfinances.ui.theme.PastelGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    initialHour: Int,
    initialMinute: Int
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    val timePickerColors = TimePickerDefaults.colors(
        clockDialColor = PastelGreen,
        clockDialSelectedContentColor = BrightBlack,
        clockDialUnselectedContentColor = BrightBlack,
        selectorColor = BrightGreen,
        periodSelectorBorderColor = BrightGreen,
        periodSelectorSelectedContainerColor = BrightGreen,
        periodSelectorUnselectedContainerColor = PastelGreen,
        periodSelectorSelectedContentColor = BrightBlack,
        periodSelectorUnselectedContentColor = BrightBlack,
        timeSelectorSelectedContainerColor = BrightGreen,
        timeSelectorUnselectedContainerColor = PastelGreen,
        timeSelectorSelectedContentColor = BrightBlack,
        timeSelectorUnselectedContentColor = BrightBlack
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier
                    .background(PastelGreen)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = "Выберите время",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BrightBlack
                )
                TimePicker(state = timePickerState, colors = timePickerColors)
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(contentColor = BrightBlack)
                    ) {
                        Text(stringResource(id = R.string.action_cancel))
                    }
                    TextButton(
                        onClick = {
                            onConfirm(timePickerState.hour, timePickerState.minute)
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = BrightBlack)
                    ) {
                        Text(stringResource(id = R.string.action_ok))
                    }
                }
            }
        }
    }
}