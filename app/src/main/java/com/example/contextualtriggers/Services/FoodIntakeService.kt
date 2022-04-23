package com.example.contextualtriggers.Services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.contextualtriggers.DataSources.getCalories
import com.example.contextualtriggers.DataSources.getFoodNutrition
import com.example.contextualtriggers.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import java.time.LocalDate
import java.time.ZoneOffset

class FoodIntakeService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val CHANNEL_ID = "Food Intake Service"
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
        startForeground(1003, notification);
        val todayStart = LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000
        val fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_STEP_COUNT_CADENCE, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
            .build();
        val account = GoogleSignIn.getAccountForExtension(this, fitnessOptions)
        val thread = Thread {
            while (true) {
                getCalories(
                    startSeconds = todayStart,
                    endSeconds = System.currentTimeMillis(),
                    context = this,
                    account = account
                ) {
                    generateNotification(
                        "Burn few more calories to Have ${
                            getFoodNutrition(
                                it.toDouble()
                            )
                        }",
                        "Food Intake Trigger",
                        NotificationManager.IMPORTANCE_HIGH,
                        9000,this,intent
                    )
//                        this,
//                        "Food Intake Trigger",
//                        "Few you send few more calories you could Have ${getFoodNutrition(it.toDouble())}"
//                    )
                    Log.d("TAG", "onStartCommand: $it")
                }
                Thread.sleep(1000 * 3600)
            }
        }
        thread.start()
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
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
}