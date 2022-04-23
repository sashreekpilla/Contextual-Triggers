package com.example.contextualtriggers.Services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentValues
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
import com.example.contextualtriggers.DataSources.getGoal
import com.example.contextualtriggers.DataSources.getLocation
import com.example.contextualtriggers.DataSources.getTodayTotalSteps
import com.example.contextualtriggers.MainActivity
import com.example.contextualtriggers.R
import com.example.contextualtriggers.Triggers.GoalTrigger
import com.example.contextualtriggers.Triggers.PerfectWeatherTrigger
import com.example.contextualtriggers.Triggers.WaterIntakeTrigger
import com.example.contextualtriggers.Utils.getWeather
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

class GoalService: Service() {
    lateinit var temp: String
    var isForeGroundService = false

    inner class LocalBinder : Binder() {
        fun getService(): GoalService = this@GoalService
    }

    override fun onCreate() {
        super.onCreate()
        isForeGroundService = false
    }

    private val binder = LocalBinder()

    override fun onBind(intent: Intent): IBinder {
        Log.d(ContentValues.TAG, "onBind")
        return binder
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("Gaol","test1")
        CoroutineScope(Dispatchers.IO).launch {
            onServiceStart(intent)
        }
        createNotificationChannel()
        val flags = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            else -> PendingIntent.FLAG_UPDATE_CURRENT
        }
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, flags)
        isForeGroundService = true
        val CHANNEL_ID = "20000"
        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Goal service Running")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notification = builder.build()
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(4000, notification)
        }
        try{
            startForeground(456, notification)
        } catch(e: Exception){
            var a =e
        }

        return Service.START_STICKY;
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
            val CHANNEL_ID = "20000"
            val channel = NotificationChannel(CHANNEL_ID, "Goal_Channel", importance).apply {
                description = "Goal_Service"
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private suspend fun onServiceStart(intent: Intent?) {
        Log.e("Gaol","test2")
        while (true) {
            val fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_STEP_COUNT_CADENCE, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
                .build();
            val account = GoogleSignIn.getAccountForExtension(this, fitnessOptions)

            getGoal(
                this,
                 account
            ){ targetGoal ->
                Log.e("Gaol","test3")
                getTodayTotalSteps(
                    this,
                    account
                ){ currentGoal ->
                    Log.e("Gaol","test4")
                    var triggerText = GoalTrigger.goalTrigger(currentGoal,targetGoal)
                    if(triggerText != ""){
                        generateNotification(
                            triggerText,
                            "Goal Trigger",
                            NotificationManager.IMPORTANCE_HIGH,
                            123,
                            this,
                            intent
                        )
                    }
                }
            }
            delay(1000*7200)
        }
    }
}