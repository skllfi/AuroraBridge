package com.aurorabridge.optimizer.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorabridge.optimizer.core.adb.AdbAnalyzer
import kotlinx.coroutines.launch

/**
 * ViewModel for the Diagnostics screen.
 *
 * This ViewModel manages the state and logic for running system diagnostics,
 * primarily using the AdbAnalyzer to detect system limiters.
 */
class DiagnosticsViewModel : ViewModel() {

    private val adbAnalyzer = AdbAnalyzer()

    // Holds the list of found limiters. The UI observes this state.
    val analysisResult = mutableStateOf<List<String>>(emptyList())
    // Indicates if the analysis is currently running.
    val isAnalyzing = mutableStateOf(false)

    /**
     * A mock command executor for testing purposes.
     * In a real scenario, this would be replaced with an actual AdbCommandExecutor.
     */
    private val mockCommandExecutor: suspend (String) -> String = {
        // Simulate finding two limiters for demonstration purposes
        when {
            it.contains("com.huawei.powergenie") -> "package:com.huawei.powergenie"
            it.contains("com.miui.powerkeeper") -> "package:com.miui.powerkeeper"
            else -> ""
        }
    }

    /**
     * Starts the analysis of system limiters.
     * The result is stored in [analysisResult].
     */
    fun runAnalysis() {
        viewModelScope.launch {
            isAnalyzing.value = true
            analysisResult.value = adbAnalyzer.analyze(mockCommandExecutor)
            isAnalyzing.value = false
        }
    }
}
