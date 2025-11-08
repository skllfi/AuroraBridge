package com.aurorabridge.optimizer.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorabridge.optimizer.core.adb.AdbAnalyzer
import com.aurorabridge.optimizer.utils.AdbCommander
import kotlinx.coroutines.launch

/**
 * ViewModel for the Diagnostics screen.
 *
 * This ViewModel manages the state and logic for running system diagnostics,
 * primarily using the AdbAnalyzer to detect system limiters.
 */
class DiagnosticsViewModel : ViewModel() {

    private val adbAnalyzer = AdbAnalyzer()
    private val adbCommander = AdbCommander()

    // Holds the list of found limiters. The UI observes this state.
    val analysisResult = mutableStateOf<List<String>>(emptyList())
    // Indicates if the analysis is currently running.
    val isAnalyzing = mutableStateOf(false)
    // Holds any error message from the analysis.
    val errorMessage = mutableStateOf<String?>(null)

    /**
     * A command executor that uses the real AdbCommander.
     */
    private val commandExecutor: suspend (String) -> String = {
        val result = adbCommander.runAdbCommandAsync(it)
        if (result.isSuccess) {
            result.output ?: ""
        } else {
            // In case of an error, we can log it or display it.
            errorMessage.value = result.error
            ""
        }
    }

    /**
     * Starts the analysis of system limiters.
     * The result is stored in [analysisResult].
     */
    fun runAnalysis() {
        viewModelScope.launch {
            isAnalyzing.value = true
            errorMessage.value = null
            analysisResult.value = adbAnalyzer.analyze(commandExecutor)
            isAnalyzing.value = false
        }
    }
}
