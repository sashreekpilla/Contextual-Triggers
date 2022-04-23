package com.example.contextualtriggers.Triggers

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager

object WaterIntakeTrigger {
    fun waterTrigger(temp: Double, context: Context): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        var prefTemp = preferences.getString("temp", "0")
        val editor: SharedPreferences.Editor = preferences.edit()
        if (prefTemp != null) {
            if (temp >= prefTemp.toDouble()) {
                editor.putString("temp", temp.toString())
                editor.apply()
                return true
            } else {
                return false
            }
        } else {
            return false
        }

    }
}