package com.example.contextualtriggers.BroadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.contextualtriggers.MainActivity
import com.example.contextualtriggers.Services.DummyBackgroundService
import com.example.contextualtriggers.Services.WITService
import com.example.contextualtriggers.Services.SedentaryBackgroundService
import com.example.contextualtriggers.ServicesManager

class SystemBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val services = listOf(
            DummyBackgroundService::class.java,
            SedentaryBackgroundService::class.java,
            WITService::class.java
            //add services here
        )
        if (context != null) {
            //check service is running, if not run the service
            ServicesManager.startServices(context,services)
        }

    }


}