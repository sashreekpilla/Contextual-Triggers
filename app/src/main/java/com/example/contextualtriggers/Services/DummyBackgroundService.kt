package com.example.contextualtriggers.Services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class DummyBackgroundService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // implement Notification to indicate that the service is running
        val CHANNEL_ID = "Foreground Service ID"
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_ID,
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(
            notificationChannel
        )
        val notification =
            Notification.Builder(this, CHANNEL_ID)
                .setContentText("Service is Running")
                .setContentTitle("Sub-Heading")
                .setContentTitle("Service working").build()
        startForeground(1001, notification)
        Log.d("TAGG", "onStartCommand: System started")
        val t = Thread {
            var x = 0;
            while (true) {
                x++
                Thread.sleep(1000)
                Log.d("TAG", "onStartCommand: Dummy service running")
            }
        }
        t.start()
//        val x = FusedLocationProviderClient(this)
//        getLocation(
//            x,
//            { latitude, longitude ->
//
//            }, {
//                println(it)
//            })
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
//        TODO("Not yet implemented")
        return null
    }
}