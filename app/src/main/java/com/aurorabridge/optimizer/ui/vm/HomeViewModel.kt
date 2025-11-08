package com.aurorabridge.optimizer.ui.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.aurorabridge.optimizer.worker.AppMonitorWorker
import java.util.concurrent.TimeUnit

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val workManager = WorkManager.getInstance(application)

    fun startMonitoring() {
        val workRequest = PeriodicWorkRequestBuilder<AppMonitorWorker>(
            12, TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            AppMonitorWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    fun stopMonitoring() {
        workManager.cancelUniqueWork(AppMonitorWorker.WORK_NAME)
    }
}
