
package com.aurorabridge.optimizer.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorabridge.optimizer.adb.AdbCommander
import com.aurorabridge.optimizer.utils.CommandLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CommandLoggerViewModel : ViewModel() {

    private val _logEntries = MutableStateFlow<List<CommandLogger.LogEntry>>(emptyList())
    val logEntries: StateFlow<List<CommandLogger.LogEntry>> = _logEntries.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _commandText = MutableStateFlow("")
    val commandText: StateFlow<String> = _commandText.asStateFlow()

    init {
        loadLogs()
    }

    fun onCommandTextChanged(text: String) {
        _commandText.value = text
    }

    fun loadLogs() {
        viewModelScope.launch {
            _logEntries.value = CommandLogger.getHistory()
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            CommandLogger.clearHistory()
            loadLogs()
        }
    }

    fun executeCommand() {
        val commandToExecute = _commandText.value
        if (commandToExecute.isBlank()) {
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = AdbCommander.runCommand(commandToExecute)
            CommandLogger.log(
                command = commandToExecute,
                isSuccess = result.isSuccess,
                details = result.output
            )
            _commandText.value = ""
            loadLogs()
            _isLoading.value = false
        }
    }
}
