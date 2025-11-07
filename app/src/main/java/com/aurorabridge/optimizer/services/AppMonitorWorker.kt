package com.aurorabridge.optimizer.services

import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import android.content.Context

class AppMonitorWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    private val TAG = "AppMonitorWorker"

    override suspend fun doWork(): Result {
        Log.i(TAG, "Running periodic app monitor")
        // TODO: perform checks (notifications, autostart) per app
        return Result.success()
    }
}
