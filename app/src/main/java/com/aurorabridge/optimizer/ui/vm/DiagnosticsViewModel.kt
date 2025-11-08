package com.aurorabridge.optimizer.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorabridge.optimizer.adb.AdbAnalyzer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DiagnosticsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<DiagnosticsUiState>(DiagnosticsUiState.Idle)
    val uiState: StateFlow<DiagnosticsUiState> = _uiState.asStateFlow()

    fun runDiagnostics() {
        viewModelScope.launch {
            _uiState.value = DiagnosticsUiState.Loading
            // Simulate long-running diagnostics
            kotlinx.coroutines.delay(2000)
            val report = AdbAnalyzer.runFullAnalysis()
            _uiState.value = DiagnosticsUiState.Success(report)
        }
    }

    fun exportReport(report: AdbAnalyzer.AnalyzerReport): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("ADB Diagnostics Report\n")
        stringBuilder.append("=========================\n\n")
        stringBuilder.append("Found Limiters:\n")
        stringBuilder.append(report.foundLimiters.joinToString("\n- ", prefix = "- "))
        stringBuilder.append("\n\nRaw Outputs:\n")
        report.rawOutputs.forEach { (key, value) ->
            stringBuilder.append("\n--- $key ---\n")
            stringBuilder.append(value)
            stringBuilder.append("\n")
        }
        return stringBuilder.toString()
    }
}

sealed interface DiagnosticsUiState {
    object Idle : DiagnosticsUiState
    object Loading : DiagnosticsUiState
    data class Success(val report: AdbAnalyzer.AnalyzerReport) : DiagnosticsUiState
    data class Error(val message: String) : DiagnosticsUiState
}
