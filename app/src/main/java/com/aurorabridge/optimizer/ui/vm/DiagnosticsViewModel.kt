package com.aurorabridge.optimizer.ui.vm

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.analyzer.AdbAnalyzer
import com.aurorabridge.optimizer.model.Limiter
import com.aurorabridge.optimizer.utils.AdbCommander
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

    private val adbAnalyzer = AdbAnalyzer()
    private val adbCommander = AdbCommander()

    fun onWarningConfirmed() {
        _uiState.value = DiagnosticsUiState.Idle
    }

    fun runDiagnostics() {
        viewModelScope.launch {
            _uiState.value = DiagnosticsUiState.Loading

            // The command executor that runs ADB commands and returns the output
            val commandExecutor: suspend (String) -> String = {
                val result = adbCommander.runAdbCommandAsync(it)
                if (result.isSuccess) {
                    result.output ?: ""
                } else {
                    // If a command fails, we can surface this to the user
                    _uiState.value = DiagnosticsUiState.Error(result.error ?: "Unknown ADB error")
                    ""
                }
            }

            // Run the analysis
            val report = adbAnalyzer.analyze(commandExecutor)

            // Only update to success if we are still in the loading state
            if (_uiState.value is DiagnosticsUiState.Loading) {
                _uiState.value = DiagnosticsUiState.Success(report)
            }
        }
    }

    fun applyFix(limiter: Limiter, context: Context) {
        viewModelScope.launch {
            limiter.fixCommand?.let {
                val result = adbCommander.runAdbCommandAsync(it)
                if (result.isSuccess) {
                    _snackbarMessage.emit(context.getString(R.string.limiter_fix_applied, context.getString(limiter.name)))
                    // Re-run diagnostics to reflect the change
                    runDiagnostics()
                } else {
                    _snackbarMessage.emit(result.error ?: "Failed to apply fix")
                }
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

        if (report.foundLimiters.isNotEmpty()) {
            stringBuilder.append("Found Limiters:\n")
            report.foundLimiters.forEach { limiter ->
                stringBuilder.append("- ${context.getString(limiter.name)}\n")
            }
        } else {
            stringBuilder.append("No known limiters were found.\n")
        }

        // The rest of the report generation can be added back later

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
