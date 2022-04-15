package com.example.contextualtriggers.DataSources

import android.annotation.SuppressLint
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.CancellationTokenSource
import java.lang.Exception

@SuppressLint("MissingPermission")
fun getLocation(
    client: FusedLocationProviderClient,
    onSuccessListener: (latitude: Double, longitude: Double) -> Unit = { _: Double, _: Double -> },
    onFailureListener: (exception:Exception?) -> Unit = {}
) {
    val location = client.getCurrentLocation(100, CancellationTokenSource().token)
    location.addOnCompleteListener {
        if (it.isSuccessful) {
            Log.d("TAG", "getLocation: ${it.result}")
            onSuccessListener(it.result.latitude, it.result.longitude);
        } else {
            val x = it.exception
            onFailureListener(it.exception)
        }
    }
}