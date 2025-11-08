package com.aurorabridge.optimizer.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aurorabridge.optimizer.R

@Composable
fun UserWarningDialog(onAccept: () -> Unit, onDecline: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDecline,
        title = { Text(stringResource(R.string.user_warning_title)) },
        text = { Text(stringResource(R.string.user_warning_message)) },
        confirmButton = {
            TextButton(onClick = onAccept) {
                Text(stringResource(R.string.user_warning_accept))
            }
        },
        dismissButton = {
            TextButton(onClick = onDecline) {
                Text(stringResource(R.string.user_warning_decline))
            }
        }
    )
}
