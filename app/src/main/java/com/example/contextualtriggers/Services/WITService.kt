package com.example.contextualtriggers.Services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.Service
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.contextualtriggers.DataSources.getLocation
import com.example.contextualtriggers.MainActivity
import com.example.contextualtriggers.R
import com.example.contextualtriggers.Utils.WeatherMgr
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class WITService : Service() {
    lateinit var temp:String
    var isForeGroundService = false
    inner class LocalBinder : Binder() {
        fun getService(): WITService = this@WITService
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
        Log.d(TAG, "onStartCommand: exec")
                CoroutineScope(IO).launch {
                    onServiceStart(0)
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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

    @RequiresApi(Build.VERSION_CODES.P)
    private suspend fun onServiceStart(increaseT: Int) {
        while (true) {
            var it = increaseT
            if(it == null){
                it = 0
            }
            val x = FusedLocationProviderClient(this)

        getLocation(
            x,
            { latitude, longitude ->
                Thread.sleep(3000)
                Log.d(TAG, "location: exec")

                val preferences = PreferenceManager.getDefaultSharedPreferences(this)
                var wm = WeatherMgr(preferences,object : WeatherMgr.AsyncResponse {
                    override fun processFinish(output: String?) {
                        //Here you will receive the result fired from async class
                        //of onPostExecute(result) method.
                        if (output != null) {
                            temp= output
                        }
                    }
                }, this, latitude, longitude,it)
                wm.execute()
                if (wm.temp != "") {
                    Log.e("Current Temp", wm.temp)
                }

            }, {
                println(it)
            })

            delay(300000)
        }
    }
}


