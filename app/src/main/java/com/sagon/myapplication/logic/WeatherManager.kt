package com.sagon.myapplication.logic

import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

object WeatherManager {

    /**
     * Obtiene el tiempo real usando Open-Meteo (GRATIS, sin registro).
     */
    suspend fun getWeatherData(lat: Double, lon: Double): WeatherInfo = withContext(Dispatchers.IO) {
        try {
            val response = URL("https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&current_weather=true").readText()
            val json = JSONObject(response).getJSONObject("current_weather")
            WeatherInfo(
                temp = json.getDouble("temperature"),
                windSpeed = json.getDouble("windspeed"),
                isError = false
            )
        } catch (e: Exception) {
            WeatherInfo(temp = 25.0, windSpeed = 5.0, isError = true)
        }
    }
}

data class WeatherInfo(
    val temp: Double,
    val windSpeed: Double,
    val isError: Boolean
)
