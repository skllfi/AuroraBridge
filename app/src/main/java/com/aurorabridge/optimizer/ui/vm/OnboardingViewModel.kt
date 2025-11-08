package com.aurorabridge.optimizer.ui.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import com.aurorabridge.optimizer.utils.SettingsManager

class OnboardingViewModel(private val context: Context) : ViewModel() {

    private val settingsManager = SettingsManager(context)

    fun setOnboardingComplete() {
        settingsManager.setOnboardingComplete(true)
    }
}
