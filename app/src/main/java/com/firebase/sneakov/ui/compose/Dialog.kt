package com.firebase.sneakov.ui.compose

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun Dialog(
    showDialog: Boolean,
    title: String = "Thông báo",
    confirmLabel: String = "Ok",
    dismissLabel: String = "Hủy",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title) },
            text = { content() },
            confirmButton = {
                TextButton(onClick = onConfirm) { Text(confirmLabel) }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text(dismissLabel) }
            }
        )
    }
}