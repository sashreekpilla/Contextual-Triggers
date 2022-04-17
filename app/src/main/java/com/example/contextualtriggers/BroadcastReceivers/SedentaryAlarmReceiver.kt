package com.example.contextualtriggers.BroadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.contextualtriggers.Utils.SedentaryTriggerUtils

class SedentaryAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        SedentaryTriggerUtils.sendNotification(
            context,
            "Sedentary Trigger",
            "You have been sitting for a long period. Take a hike"
        )
    }
}