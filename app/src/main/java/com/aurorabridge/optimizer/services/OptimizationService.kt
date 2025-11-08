package com.aurorabridge.optimizer.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.aurorabridge.optimizer.optimizer.BrandAutoOptimizer

class OptimizationService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        BrandAutoOptimizer.getProfileForCurrentDevice()?.let {
            BrandAutoOptimizer.applyOptimization(it)
        }
        stopSelf()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}