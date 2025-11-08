package com.aurorabridge.optimizer.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aurorabridge.optimizer.analyzer.AdbAnalyzer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DiagnosticsViewModel(application: Application) : AndroidViewModel(application) {

    private val _scanResult = MutableLiveData<List<String>>()
    val scanResult: LiveData<List<String>> = _scanResult

    fun runAnalysis() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = AdbAnalyzer.scanAll(getApplication())
            _scanResult.postValue(result)
        }
    }
}
