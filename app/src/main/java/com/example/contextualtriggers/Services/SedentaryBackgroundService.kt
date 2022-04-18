package com.example.contextualtriggers.Services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.example.contextualtriggers.BroadcastReceivers.ActivityTransitionReceiver
import com.example.contextualtriggers.Utils.ActivityTransitionsUtil
import com.example.contextualtriggers.Utils.Constants
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient

class SedentaryBackgroundService:Service() {
    lateinit var client: ActivityRecognitionClient
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        client = ActivityRecognition.getClient(this)
        requestForUpdates()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        deregisterForUpdates()
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
    private fun requestForUpdates() {
        client
            .requestActivityTransitionUpdates(
                ActivityTransitionsUtil.getActivityTransitionRequest(),
                getPendingIntent()
            )
            .addOnSuccessListener {
                Log.d("TAG", "onReceive: Registered")
                showToast("successful registration")
            }
            .addOnFailureListener { e: Exception ->
                showToast("Unsuccessful registration")
            }
    }

    private fun deregisterForUpdates() {
        client
            .removeActivityTransitionUpdates(getPendingIntent())
            .addOnSuccessListener {
                getPendingIntent().cancel()
                showToast("successful deregistration")
            }
            .addOnFailureListener { e: Exception ->
                showToast("unsuccessful deregistration")
            }
    }
    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(this, ActivityTransitionReceiver::class.java)
        return PendingIntent.getBroadcast(
            this,
            Constants.REQUEST_CODE_INTENT_ACTIVITY_TRANSITION,
            intent,
            PendingIntent.FLAG_MUTABLE
        )
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG)
            .show()
    }
}