package com.example.contextualtriggers.Triggers

import org.json.JSONObject

object PerfectWeatherTrigger {
    fun perfectTrigger(weatherData: JSONObject): Boolean {
        return (weatherData.getDouble("temp_c") > 7 && weatherData.getDouble("wind_kph")  < 15 && weatherData.getDouble("wind_kph")  > 9 && weatherData.getDouble("pressure_mb")  > 1000)
    }
}