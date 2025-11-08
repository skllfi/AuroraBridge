package com.aurorabridge.optimizer.services

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class AppMonitorWorker(private val appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    private val TAG = "AppMonitorWorker"

    override suspend fun doWork(): Result {
        Log.i(TAG, "Running periodic app monitor to check for bloatware.")

        return try {
            // Create notification channel (important for Android 8.0+)
            BloatwareNotificationHelper.createNotificationChannel(appContext)

            // Get the list of installed apps
            val installedPackages = getInstalledPackages()
            
            // Find which bloatware apps are installed
            val foundBloatware = BloatwareApps.packageNames.intersect(installedPackages)

            if (foundBloatware.isNotEmpty()) {
                Log.i(TAG, "Found ${foundBloatware.size} bloatware apps.")
                
                // Create and show a notification
                val notificationTitle = "Bloatware Detected"
                val notificationContent = "Found ${foundBloatware.size} potential bloatware apps. Check the App List for details."
                BloatwareNotificationHelper.showNotification(appContext, notificationTitle, notificationContent)
            } else {
                Log.i(TAG, "No known bloatware apps found.")
            }
            
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error during bloatware check", e)
            Result.failure()
        }
    }

    private fun getInstalledPackages(): Set<String> {
        val pm = appContext.packageManager
        return pm.getInstalledApplications(PackageManager.GET_META_DATA)
                 .map { it.packageName }
                 .toSet()
    }
}
