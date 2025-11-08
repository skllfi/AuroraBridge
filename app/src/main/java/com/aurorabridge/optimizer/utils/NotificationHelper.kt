
package com.aurorabridge.optimizer.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.aurorabridge.optimizer.R

class NotificationHelper(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createNotificationChannel(channelId: String, channelName: String, channelDescription: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(channelId: String, notificationId: Int, title: String, content: String, smallIcon: Int) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(notificationId, builder.build())
    }

    companion object {
        const val BLOATWARE_CHANNEL_ID = "bloatware_channel"
        const val OPTIMIZATION_CHANNEL_ID = "auto_optimization_channel"

        const val BLOATWARE_NOTIFICATION_ID = 1
        const val OPTIMIZATION_NOTIFICATION_ID = 2
    }
}
