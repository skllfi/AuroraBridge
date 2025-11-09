package com.aurorabridge.optimizer.ui.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.aurorabridge.optimizer.utils.SettingsManager

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsManager = SettingsManager(application)

    fun setOnboardingComplete() {
        settingsManager.setOnboardingComplete(true)
    }
}
