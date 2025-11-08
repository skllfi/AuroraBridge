package com.aurorabridge.optimizer.ui.vm

import androidx.lifecycle.ViewModel
import com.aurorabridge.optimizer.utils.CommandLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CommandLoggerViewModel : ViewModel() {

    private val _logEntries = MutableStateFlow<List<CommandLogger.LogEntry>>(emptyList())
    val logEntries: StateFlow<List<CommandLogger.LogEntry>> = _logEntries.asStateFlow()

    init {
        loadLogs()
    }

    fun loadLogs() {
        _logEntries.value = CommandLogger.getHistory()
    }

    fun clearLogs() {
        CommandLogger.clearHistory()
        loadLogs() // Refresh the list after clearing
    }
}
