package com.example.contextualtriggers.Utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.contextualtriggers.R
import com.example.contextualtriggers.Utils.Constants.CHANNEL_ID
import com.example.contextualtriggers.Utils.Constants.NOTIFICATION_ID

object SedentaryTriggerUtils {
    fun sendNotification(context: Context, title: String, content: String) {
        createNotificationChannel(context)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel(context: Context) {
        val name = "Sedentary Trigger Channel"
        val description = "Notifications related to sedentary triggers are sent in this channel"
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            name,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            this.description = description
        }
        val notificationManager =
            ContextCompat.getSystemService(context, NotificationManager::class.java)
        notificationManager?.createNotificationChannel(notificationChannel)
    }
}