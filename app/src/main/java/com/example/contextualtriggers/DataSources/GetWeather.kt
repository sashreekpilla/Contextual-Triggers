package com.example.contextualtriggers.Utils

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

fun getWeather(LAT: Double,LON: Double,context: Context,onWeatherFetched:(current:JSONObject) -> Unit): JSONObject? {
    var API = "03b796c7eb174c56a12114006221904"
    val url = "https://api.weatherapi.com/v1/current.json?key=$API&q=$LAT,$LON"
    var jsonObj: JSONObject? = null
    var current: JSONObject? = null
    val queue = Volley.newRequestQueue(context)
    val stringRequest = StringRequest(
        Request.Method.GET, url,
        { response ->
            jsonObj = response.let { JSONObject(it) }
            current = jsonObj!!.getJSONObject("current")
            onWeatherFetched(jsonObj!!.getJSONObject("current"))
            Log.e("Weather1", current.toString());
        },
        {
            Log.d("TAG", "onCreate: gone wrong")
        })
    queue.add(stringRequest)
    return current
}