package com.sagon.piscinasblue.logic

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import java.util.Locale

object LocationHelper {

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(context: Context): Pair<Double, Double>? {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        return try {
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                CancellationTokenSource().token
            ).await()
            
            location?.let { Pair(it.latitude, it.longitude) }
        } catch (e: Exception) {
            null
        }
    }

    fun getCityName(context: Context, lat: Double, lon: Double): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            addresses?.firstOrNull()?.locality ?: "Ubicación desconocida"
        } catch (e: Exception) {
            "Desconocida"
        }
    }
}
