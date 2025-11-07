package com.aurorabridge.optimizer.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class AppNotificationListener : NotificationListenerService() {
    private val TAG = "AppNotificationListener"

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        Log.i(TAG, "Notification from: ${"$"}{sbn.packageName}")
        // store or broadcast as needed
    }
}
