package com.example.contextualtriggers.BroadcastReceivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import com.example.contextualtriggers.Triggers.SedentaryTrigger
import com.example.contextualtriggers.Utils.ActivityTransitionsUtil
import com.example.contextualtriggers.Utils.SedentaryTriggerUtils
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import java.text.SimpleDateFormat
import java.util.*

class ActivityTransitionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent)
            result?.let {
                result.transitionEvents.forEach { event ->
                    val eventType = ActivityTransitionsUtil.toActivityString(event.activityType)
                    val transitionType =
                        ActivityTransitionsUtil.toTransitionType(event.transitionType)
                    val info =
                        "Transition: " + ActivityTransitionsUtil.toActivityString(event.activityType) +
                                " (" + ActivityTransitionsUtil.toTransitionType(event.transitionType) + ")" + "   " +
                                SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
                    SedentaryTrigger.trigger(
                        eventType = eventType,
                        transitionType = transitionType,
                        context = context
                    )
                    Toast.makeText(context, info, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}