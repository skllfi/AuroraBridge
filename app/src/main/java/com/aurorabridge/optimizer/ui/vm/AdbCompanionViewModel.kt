package com.aurorabridge.optimizer.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorabridge.optimizer.adb.AdbCommander
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdbCompanionViewModel : ViewModel() {

    private val _command = MutableStateFlow("")
    val command = _command.asStateFlow()

    private val _output = MutableStateFlow("")
    val output = _output.asStateFlow()

    private val adbCommander = AdbCommander()

    fun onCommandChanged(newCommand: String) {
        _command.value = newCommand
    }

    fun runCommand() {
        viewModelScope.launch {
            val result = adbCommander.runCommand(_command.value)
            _output.value = result.output
        }
    }
}
