package com.aurorabridge.optimizer.viewmodel

import androidx.lifecycle.ViewModel
import com.aurorabridge.optimizer.adb.AdbAnalyzer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DiagnosticsViewModel : ViewModel() {
    private val _report = MutableStateFlow<AdbAnalyzer.AnalyzerReport?>(null)
    val report: StateFlow<AdbAnalyzer.AnalyzerReport?> = _report.asStateFlow()

    fun runAnalysis() {
        CoroutineScope(Dispatchers.IO).launch {
            val r = AdbAnalyzer.runFullAnalysis()
            _report.emit(r)
        }
    }
}
