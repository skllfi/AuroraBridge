package com.aurorabridge.optimizer.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.aurorabridge.optimizer.optimizer.BrandAutoOptimizer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OptimizationService : Service() {

    @Inject
    lateinit var brandAutoOptimizer: BrandAutoOptimizer

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        GlobalScope.launch {
            brandAutoOptimizer.getProfileForCurrentDevice()?.let {
                brandAutoOptimizer.applyOptimization(it)
            }
            stopSelf()
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}