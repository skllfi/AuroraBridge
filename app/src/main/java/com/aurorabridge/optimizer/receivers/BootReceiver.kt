package com.aurorabridge.optimizer.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.aurorabridge.optimizer.MainActivity

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            val isAutoStartEnabled = prefs.getBoolean("auto_start_enabled", false)

            if (isAutoStartEnabled) {
                val i = Intent(context, MainActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(i)
            }
        }
    }
}
