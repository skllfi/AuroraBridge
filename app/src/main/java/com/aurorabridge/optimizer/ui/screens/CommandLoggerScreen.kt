package com.aurorabridge.optimizer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.ui.vm.CommandLoggerViewModel

@Composable
fun CommandLoggerScreen() {
    val viewModel: CommandLoggerViewModel = viewModel()
    val logEntries by viewModel.logEntries.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.command_logger_title),
                style = MaterialTheme.typography.headlineMedium
            )
            Button(onClick = { viewModel.clearLogs() }) {
                Text(stringResource(id = R.string.command_logger_clear))
            }
        }

        if (logEntries.isEmpty()) {
            Text(
                text = stringResource(id = R.string.command_logger_empty),
                modifier = Modifier.padding(top = 16.dp)
            )
        } else {
            LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                items(logEntries) { entry ->
                    LogEntryCard(entry)
                }
            }
        }
    }
}

@Composable
fun LogEntryCard(entry: com.aurorabridge.optimizer.utils.CommandLogger.LogEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(
                        if (entry.success) R.drawable.ic_check_circle else R.drawable.ic_error
                    ),
                    contentDescription = if (entry.success) "Success" else "Error",
                    tint = if (entry.success) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                )
                Text(
                    text = entry.timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Text(
                text = entry.command,
                style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace),
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = entry.output,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
                    .fillMaxWidth()
            )
        }
    }
}
