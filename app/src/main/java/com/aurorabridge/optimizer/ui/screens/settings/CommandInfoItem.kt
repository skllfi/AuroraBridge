package com.aurorabridge.optimizer.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.utils.ParsedCommand

@Composable
fun CommandInfoItem(command: ParsedCommand) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            when (command) {
                is ParsedCommand.UninstallPackage -> {
                    Icon(Icons.Default.Delete, contentDescription = "Uninstall", tint = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.padding(start = 12.dp))
                    Column {
                        Text(text = stringResource(R.string.settings_command_info_uninstall), fontWeight = FontWeight.Bold)
                        Text(text = command.packageName, fontFamily = FontFamily.Monospace)
                    }
                }
                is ParsedCommand.PutSetting -> {
                    Icon(Icons.Default.Settings, contentDescription = "Setting", tint = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.padding(start = 12.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.settings_command_info_modify_setting, command.key),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.settings_command_info_new_value, command.value),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                is ParsedCommand.Unknown -> {
                    Icon(Icons.Default.Code, contentDescription = "Command", tint = MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier.padding(start = 12.dp))
                    Column {
                        Text(text = stringResource(R.string.settings_command_info_other_command), fontWeight = FontWeight.Bold)
                        Text(text = command.command, fontFamily = FontFamily.Monospace, maxLines = 1)
                    }
                }
            }
        }
    }
}
