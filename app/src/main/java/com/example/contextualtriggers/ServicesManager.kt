package com.example.contextualtriggers

import android.annotation.TargetApi
import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.contextualtriggers.Services.DummyBackgroundService

object ServicesManager {
    fun startServices(context: Context,services:List<Class<out Service>> = emptyList()) {
        services.forEach {
            val service = Intent(context, it.javaClass)
            if (!isServiceRunning(
                    context = context,
                    serviceName = it.javaClass.name
                )
            ) {
                context.startForegroundService(service)
            }
        }
    }
    @TargetApi(Build.VERSION_CODES.N)
    fun isServiceRunning(context: Context, serviceName: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getRunningServices(Int.MAX_VALUE).forEach {
            if (serviceName.equals(it.service.className)) {
                return true
            }
        }
        return false
    }
}