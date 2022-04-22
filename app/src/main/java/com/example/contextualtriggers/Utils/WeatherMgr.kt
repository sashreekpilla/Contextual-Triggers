package com.example.contextualtriggers.Utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.contextualtriggers.MainActivity
import com.example.contextualtriggers.R
import org.json.JSONObject
import java.net.URL


@SuppressLint("StaticFieldLeak")
class WeatherMgr(
    var sharedPreferences: SharedPreferences,
    var delegate: AsyncResponse,
    var context: Context,
    latitude: Double,
    longitude: Double,
    var increaseT: Int
) : AsyncTask<String, Void, String>() {
    var LAT = latitude
    var LON = longitude
    var API = "03b796c7eb174c56a12114006221904"
    var temp: String = ""

    interface AsyncResponse {
        fun processFinish(output: String?)
    }

    override fun doInBackground(vararg params: String?): String? {
        var response: String?

        try {
            response =
                URL("https://api.weatherapi.com/v1/current.json?key=$API&q=$LAT,$LON").readText()
        } catch (e: Exception) {
            response = null
        }

        return response
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        try {

            var prefTemp = sharedPreferences.getString("temp", "0")
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            /* Extracting JSON returns from the API */
            val jsonObj = result?.let { JSONObject(it) }
            val current = jsonObj?.getJSONObject("current")
            var currentWind: Double = 0.0
            var currentPressure: Double = 0.0
            var currentHumidity: Double = 0.0
            if (current != null) {
                var currentTemp = current.getDouble("temp_c")
                currentWind = current.getDouble("wind_kph")
                currentPressure = current.getDouble("pressure_mb")
                currentHumidity = current.getDouble("humidity")
                currentTemp += increaseT
                temp = currentTemp.toString()
            }

            if (prefTemp != null) {
                Log.d(ContentValues.TAG, "weather: $temp")
                Log.d(ContentValues.TAG, "weather: $prefTemp")
                if (temp.toDouble() + increaseT >= prefTemp.toDouble()) {
                    generateNotification(
                        "Its Hot out there its better to have a glass of water while you are taking those steps",
                        "Water Intake Trigger",
                        NotificationManager.IMPORTANCE_HIGH,
                        1
                    )
                    editor.putString("temp", temp)
                    editor.apply()
                }
            }
            perfectWeather(temp, currentWind, currentHumidity, currentPressure)
            Log.d(ContentValues.TAG, "weather: exec4")
        } catch (e: Exception) {
            var a = e
        }
    }

    private fun generateNotification(text: String, title: String, priority: Int, id: Int) {
        val flags = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            else -> PendingIntent.FLAG_UPDATE_CURRENT
        }
        var intent = Intent(context, MainActivity::class.java)
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
        val notificationManager = NotificationManagerCompat.from(
            context
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                title,
                text,
                priority
            )
            notificationManager.createNotificationChannel(channel)
            builder.setChannelId(title)
        }

        notificationManager.notify(id, builder.build())
    }

    private fun perfectWeather(
        temp: String,
        currentWind: Double,
        currentHumidity: Double,
        currentPressure: Double
    ) {
        if (temp.toDouble() > 7 && currentWind < 15 && currentWind > 9 && currentPressure > 1000) {
            generateNotification(
                "The weather feels immaculate like i've been told, get out there!",
                "Perfect Weather",
                NotificationManager.IMPORTANCE_HIGH,
                0
            )
        }
    }
}