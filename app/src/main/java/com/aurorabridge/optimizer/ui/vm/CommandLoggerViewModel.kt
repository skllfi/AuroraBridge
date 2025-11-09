package com.aurorabridge.optimizer.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorabridge.optimizer.utils.CommandLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CommandLoggerViewModel : ViewModel() {

    private val _logEntries = MutableStateFlow<List<CommandLogger.LogEntry>>(emptyList())
    val logEntries: StateFlow<List<CommandLogger.LogEntry>> = _logEntries.asStateFlow()

    init {
        loadLogs()
    }

    fun loadLogs() {
        viewModelScope.launch {
            _logEntries.value = CommandLogger.getHistory()
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            CommandLogger.clearHistory()
            loadLogs() // Refresh the list after clearing
        }
    }
}
