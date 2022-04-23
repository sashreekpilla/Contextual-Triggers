package com.example.contextualtriggers.Services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.Service
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.contextualtriggers.DataSources.getLocation
import com.example.contextualtriggers.MainActivity
import com.example.contextualtriggers.R
import com.example.contextualtriggers.Triggers.PerfectWeatherTrigger
import com.example.contextualtriggers.Triggers.WaterIntakeTrigger
import com.example.contextualtriggers.Utils.getWeather
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception


class WeatherService : Service() {
    lateinit var temp: String
    var isForeGroundService = false

    inner class LocalBinder : Binder() {
        fun getService(): WeatherService = this@WeatherService
    }

    override fun onCreate() {
        super.onCreate()
        isForeGroundService = false
    }

    private val binder = LocalBinder()

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind")
        return binder
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        CoroutineScope(IO).launch {
            Log.e("Weather", "test1")
            onServiceStart(intent)
        }
        showToast("service started")
        createNotificationChannel()
        val flags = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
            else -> FLAG_UPDATE_CURRENT
        }
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, flags)
        isForeGroundService = true
        val CHANNEL_ID = "channel_01"
        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Weather service Running")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notification = builder.build()
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(4, notification)
        }
        startForeground(4, notification)
        return START_STICKY;
    }

    fun generateNotification(
        text: String,
        title: String,
        priority: Int,
        id: Int,
        context: Context,
        intent: Intent?
    ) {
        try {
            val flags = when {
                true -> PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                else -> PendingIntent.FLAG_UPDATE_CURRENT
            }
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, flags)
            val builder = NotificationCompat.Builder(
                context,
                text
            )
            builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(text)
                .setContentTitle(title)
                .setPriority(priority)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
            val notificationManager =
                ContextCompat.getSystemService(context, NotificationManager::class.java)

            val channel = NotificationChannel(
                title,
                text,
                priority
            )
            notificationManager?.createNotificationChannel(channel)
            builder.setChannelId(title)

            notificationManager?.notify(id, builder.build())

        } catch (e: Exception) {
            Log.e("Weather", e.toString())
        }

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val CHANNEL_ID = "channel_01"
            val channel = NotificationChannel(CHANNEL_ID, "CT_Channel", importance).apply {
                description = "CT_Service"
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private suspend fun onServiceStart(intent: Intent?) {
        while (true) {
            val x = FusedLocationProviderClient(this)
            getLocation(
                x,
                { latitude, longitude ->
                    Thread.sleep(3000)
                    Log.e("Weather", latitude.toString())
//                    var weatherData = getWeather(latitude, longitude,this)
                    getWeather(latitude, longitude, this) {
                        if (it != null) {
                            var wTrigger = WaterIntakeTrigger.waterTrigger(it.getDouble("temp_c"), this)
                            if (wTrigger) {
                                generateNotification(
                                    "Its hot out there!!! better to drink water while taking those steps",
                                    "Water Intake",
                                    NotificationManager.IMPORTANCE_HIGH,
                                    1,
                                    this,
                                    intent
                                )
                            }
                            var pTriçgger = PerfectWeatherTrigger.perfectTrigger(it)
                            if (pTriçgger) {
                                generateNotification(
                                    "The weather feels immaculate like i've been told, get out there!",
                                    "Perfect Weather",
                                    NotificationManager.IMPORTANCE_HIGH,
                                    0,
                                    this,
                                    intent
                                )
                            }
                        }
                    }

                }, {
                    println(it)
                })
            delay(300000)
        }
    }
}


