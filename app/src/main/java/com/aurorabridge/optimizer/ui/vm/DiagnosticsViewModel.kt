package com.aurorabridge.optimizer.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorabridge.optimizer.repository.AdbRepository
import kotlinx.coroutines.launch

class DiagnosticsViewModel : ViewModel() {

    private val adbRepository = AdbRepository()

    private val _scanResult = MutableLiveData<List<Pair<String, Boolean>>>()
    val scanResult: LiveData<List<Pair<String, Boolean>>> = _scanResult

    fun runAnalysis() {
        viewModelScope.launch {
            _scanResult.value = adbRepository.runDiagnostics()
        }
    }
}
