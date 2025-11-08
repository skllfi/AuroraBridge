package com.aurorabridge.optimizer.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.aurorabridge.optimizer.services.OptimizationService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            val autoOptimizeEnabled = sharedPreferences.getBoolean("auto_optimize_on_startup", false)

            if (autoOptimizeEnabled) {
                Intent(context, OptimizationService::class.java).also { serviceIntent ->
                    context.startService(serviceIntent)
                }
            }
        }
    }
}
