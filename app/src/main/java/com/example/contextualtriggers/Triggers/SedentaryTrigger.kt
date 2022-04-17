package com.example.contextualtriggers.Triggers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.contextualtriggers.BroadcastReceivers.SedentaryAlarmReceiver

object SedentaryTrigger {
    fun trigger(eventType:String,transitionType:String,context:Context) {
        when (eventType) {
            "STILL" -> {
                if (transitionType == "ENTER") {
                    setAlarm(context)
                }
            }
            "WALKING" -> {
                if (transitionType == "ENTER") {
                    clearAlarm(context)
                }
            }
            "IN VEHICLE" -> {
                if (transitionType == "ENTER") {
                    setAlarm(context)
                }
            }
            "RUNNING" -> {
                if (transitionType == "ENTER") {
                    clearAlarm(context)
                }
            }
            "ON_BICYCLE" -> {
                if (transitionType == "ENTER") {
                    clearAlarm(context)
                }
            }
            else -> {

            }
        }
    }
    private fun getPendingIndent(context: Context): PendingIntent? {
        val intent =  Intent(context, SedentaryAlarmReceiver::class.java);
        return PendingIntent.getBroadcast (context, 0, intent, 0)
    }
    private fun setAlarm(context: Context) {
        val alarmManager =  context.getSystemService(Context.ALARM_SERVICE) as AlarmManager;
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000 * 3600), getPendingIndent(context));
    }
    private fun clearAlarm(context: Context) {
        getPendingIndent(context)?.cancel()
    }
}