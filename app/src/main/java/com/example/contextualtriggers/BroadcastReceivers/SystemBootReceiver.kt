package com.example.contextualtriggers.BroadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.contextualtriggers.Services.*
import com.example.contextualtriggers.ServicesManager

class SystemBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val services = listOf(
            DummyBackgroundService::class.java,
            SedentaryBackgroundService::class.java,
            WeatherService::class.java,
            FoodIntakeService::class.java,
            GoalService::class.java
            //add services here
        )
        if (context != null) {
            //check service is running, if not run the service
            ServicesManager.startServices(context, services)
        }

    }


}