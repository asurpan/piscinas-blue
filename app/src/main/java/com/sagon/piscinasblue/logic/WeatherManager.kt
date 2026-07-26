package com.sagon.piscinasblue.logic

import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

object WeatherManager {

    suspend fun getWeatherData(lat: Double, lon: Double): WeatherInfo = withContext(Dispatchers.IO) {
        try {
            val url = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current_weather=true&daily=temperature_2m_max&forecast_days=3&timezone=auto"
            val response = URL(url).readText()
            val root = JSONObject(response)
            
            val current = root.getJSONObject("current_weather")
            val daily = root.getJSONObject("daily")
            val maxTempsArray = daily.getJSONArray("temperature_2m_max")
            
            val maxTemps = mutableListOf<Double>()
            for (i in 0 until maxTempsArray.length()) {
                maxTemps.add(maxTempsArray.getDouble(i))
            }

            WeatherInfo(
                temp = current.getDouble("temperature"),
                windSpeed = current.getDouble("windspeed"),
                maxTemps = maxTemps,
                isError = false
            )
        } catch (e: Exception) {
            WeatherInfo(temp = 25.0, windSpeed = 5.0, maxTemps = listOf(25.0, 25.0, 25.0), isError = true)
        }
    }
}

data class WeatherInfo(
    val temp: Double,
    val windSpeed: Double,
    val maxTemps: List<Double> = emptyList(),
    val isError: Boolean
)
