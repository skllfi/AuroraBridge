package com.aurorabridge.optimizer.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aurorabridge.optimizer.analyzer.AdbAnalyzer
import com.aurorabridge.optimizer.services.BloatwareNotificationHelper

/**
 * A background worker that periodically scans for system limiters and reports them.
 */
class AppMonitorWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "AppMonitorWorker"
        private const val TAG = "AppMonitorWorker"
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "Starting periodic scan...")

        try {
            val foundLimiters = AdbAnalyzer.scanAll(applicationContext)

            if (foundLimiters.isNotEmpty()) {
                Log.w(TAG, "Found system limiters: ${foundLimiters.joinToString()}")
                BloatwareNotificationHelper.showNotification(applicationContext, "System Limiters Found", foundLimiters.joinToString())
            } else {
                Log.i(TAG, "No system limiters found. All clear.")
            }

            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error during periodic scan", e)
            return Result.failure()
        }
    }
}
