package com.myfinances.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.myfinances.R

@Composable
fun ErrorDialog(
    title: String,
    message: String,
    onConfirmText: String = stringResource(id = R.string.action_retry),
    onDismissText: String = stringResource(id = R.string.action_cancel),
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    icon: ImageVector = Icons.Default.Warning
) {
    AlertDialog(
        icon = { Icon(icon, contentDescription = "Error Icon") },
        title = { Text(text = title) },
        text = { Text(text = message) },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(onConfirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(onDismissText)
            }
        }
    )
}