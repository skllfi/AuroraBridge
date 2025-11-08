package com.aurorabridge.optimizer.ui.vm

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserWarningViewModel : ViewModel() {

    private val _warningAccepted = MutableStateFlow<Boolean?>(null)
    val warningAccepted = _warningAccepted.asStateFlow()

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("user_settings", Context.MODE_PRIVATE)
    }

    fun checkWarningState(context: Context) {
        viewModelScope.launch {
            val prefs = getPreferences(context)
            _warningAccepted.value = prefs.getBoolean("warning_accepted", false)
        }
    }

    fun acceptWarning(context: Context) {
        val prefs = getPreferences(context)
        prefs.edit().putBoolean("warning_accepted", true).apply()
        _warningAccepted.value = true
    }
}
