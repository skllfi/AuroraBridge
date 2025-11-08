package com.aurorabridge.optimizer.ui

import android.app.Application
import com.aurorabridge.optimizer.services.BloatwareNotificationHelper

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        BloatwareNotificationHelper.createNotificationChannel(this)
    }
}
