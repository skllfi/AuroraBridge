package com.aurorabridge.optimizer.ui.vm

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.adb.AdbAnalyzer
import com.aurorabridge.optimizer.model.Limiter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DiagnosticsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<DiagnosticsUiState>(DiagnosticsUiState.RequiresConfirmation)
    val uiState: StateFlow<DiagnosticsUiState> = _uiState.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    fun onWarningConfirmed() {
        _uiState.value = DiagnosticsUiState.Idle
    }

    fun runDiagnostics() {
        viewModelScope.launch {
            _uiState.value = DiagnosticsUiState.Loading
            kotlinx.coroutines.delay(2000) // Simulate analysis time
            val report = AdbAnalyzer.runFullAnalysis()
            _uiState.value = DiagnosticsUiState.Success(report)
        }
    }

    fun applyFix(limiter: Limiter, context: Context) {
        viewModelScope.launch {
            limiter.fixCommand?.let {
                AdbAnalyzer.applyFix(it)
                _snackbarMessage.emit(context.getString(R.string.limiter_fix_applied, context.getString(limiter.name)))
                // Re-run diagnostics to reflect the change
                runDiagnostics()
            }
        }
    }

    fun shareReport(context: Context, report: AdbAnalyzer.AnalyzerReport) {
        val reportText = exportReport(context, report)
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, reportText)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, context.getString(R.string.diagnostics_share_title))
        context.startActivity(shareIntent)
    }

    private fun exportReport(context: Context, report: AdbAnalyzer.AnalyzerReport): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("ADB Diagnostics Report\n")
        stringBuilder.append("=========================\n\n")

        stringBuilder.append("Found Limiters:\n")
        report.foundLimiters.forEach { limiter ->
            stringBuilder.append("- ${context.getString(limiter.name)}\n")
        }

        stringBuilder.append("\nLogcat Errors:\n")
        stringBuilder.append(report.logcatErrors.joinToString("\n"))

        stringBuilder.append("\n\nTop 5 Battery Hogs:\n")
        stringBuilder.append(report.batteryHogs.joinToString("\n") { "- ${it.first}: ${it.second}%" })

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
    object RequiresConfirmation : DiagnosticsUiState
    object Idle : DiagnosticsUiState
    object Loading : DiagnosticsUiState
    data class Success(val report: AdbAnalyzer.AnalyzerReport) : DiagnosticsUiState
    data class Error(val message: String) : DiagnosticsUiState
}
