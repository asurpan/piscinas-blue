package com.sagon.piscinasblue.logic

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sagon.piscinasblue.R
import com.sagon.piscinasblue.data.local.AppDatabase
import com.sagon.piscinasblue.data.PoolData
import com.sagon.piscinasblue.data.PreferenceManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class MaintenanceWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val prefManager = PreferenceManager(applicationContext)
        val pools = database.poolDao().getAllPools().firstOrNull()
        
        if (!pools.isNullOrEmpty()) {
            val poolEntity = pools.first()
            val location = prefManager.location.first()
            val lat = location?.first ?: 37.38
            val lon = location?.second ?: -5.98

            val poolData = PoolData(
                volumeM3 = poolEntity.volumeM3,
                currentPh = poolEntity.lastPh,
                currentChlorine = poolEntity.lastChlorine,
                isWinterMode = poolEntity.isWinterMode,
                lastTabletChange = poolEntity.lastTabletChange,
                userConsumptionFactor = poolEntity.userConsumptionFactor,
                tabletQuantity = poolEntity.tabletQuantity,
                isHolidayMode = poolEntity.isHolidayMode,
                pumpHp = poolEntity.pumpHp,
                lastFilterWash = poolEntity.lastFilterWash
            )

            // Lógica de Previsión Inteligente "Zero Molestias"
            val weather = try {
                WeatherManager.getWeatherData(lat, lon)
            } catch (e: Exception) {
                null
            }

            if (weather != null && !poolData.isWinterMode) {
                val todayMax = weather.temp
                val tomorrowMax = if (weather.maxTemps.size > 1) weather.maxTemps[1] else todayMax
                val dayAfterMax = if (weather.maxTemps.size > 2) weather.maxTemps[2] else todayMax

                // 0. Alerta de Evaporación Extrema (>40°C)
                if (todayMax >= 40.0 || tomorrowMax >= 40.0) {
                    showNotification(
                        "¡PELIGRO: CALOR EXTREMO! ⚠️🔥",
                        "Superamos los 40°C. El agua se evapora rápido. Revisa el nivel y dobla el filtrado."
                    )
                }

                // 1. Alerta de Ola de Calor (>35°C)
                if (tomorrowMax >= 35.0 || dayAfterMax >= 35.0) {
                    val targetTemp = if (tomorrowMax >= 35.0) tomorrowMax else dayAfterMax
                    showNotification(
                        "¡ALERTA EXTREMA! 🌡️🔥",
                        "Se esperan ${targetTemp.toInt()}°C. Sube el filtrado y revisa el cloro para evitar agua verde."
                    )
                    return Result.success()
                }

                // 2. Alerta de Choque Térmico (Subida > 5°C)
                if (tomorrowMax - todayMax >= 5.0) {
                    showNotification(
                        "Aviso de Prevención 🌡️⬆️",
                        "Mañana sube la temperatura drásticamente. Pon una pastilla extra hoy."
                    )
                    return Result.success()
                }

                // 3. Aviso de Ahorro (Bajada > 10°C)
                if (todayMax - tomorrowMax >= 10.0) {
                    showNotification(
                        "Oportunidad de Ahorro 💡",
                        "Mañana refresca mucho. Puedes bajar las horas de depuradora."
                    )
                    return Result.success()
                }
            }

            // Chequeos de mantenimiento existentes
            if (!poolData.isWinterMode) {
                val lifespan = PoolCalculator.calculateIntelligentTabletLifespan(poolData, weather?.temp ?: 25.0, weather?.windSpeed ?: 10.0)
                val now = System.currentTimeMillis()
                val daysPassed = (now - poolData.lastTabletChange) / (1000 * 60 * 60 * 24).toDouble()

                if (daysPassed >= lifespan) {
                    showNotification(
                        "¡PASTILLA CADUCADA! ⚠️",
                        "La pastilla de cloro se ha agotado. Cámbiala ahora."
                    )
                    return Result.success()
                }
                
                val filterDays = (now - poolData.lastFilterWash) / (1000 * 60 * 60 * 24).toDouble()
                if (filterDays >= 15) {
                    showNotification(
                        "Mantenimiento de Filtro 🧼",
                        "Toca lavar el filtro de arena para mantener el agua clara."
                    )
                    return Result.success()
                }
            } else {
                // Mantenimiento en Modo Invierno
                val now = System.currentTimeMillis()
                val daysPassed = (now - poolData.lastWinterProductDate) / (1000 * 60 * 60 * 24).toDouble()
                val limit = if (poolData.winterProductType == "BOYA") 60 else 90

                if (poolData.lastWinterProductDate > 0 && daysPassed >= limit) {
                    showNotification(
                        "Invernación: Reposición ❄️",
                        "El producto invernador (${poolData.winterProductType}) ha caducado. Añade una nueva dosis."
                    )
                    return Result.success()
                }

                // Aviso de Primavera (Marzo/Abril > 16°C)
                val calendar = java.util.Calendar.getInstance()
                val month = calendar.get(java.util.Calendar.MONTH) // 0-11
                if ((month == 2 || month == 3) && (weather?.temp ?: 0.0) > 16.0) {
                    showNotification(
                        "¡Llega la Primavera! ☀️",
                        "El agua sube de 16°C. ¿Empezamos a preparar la piscina para la temporada?"
                    )
                    return Result.success()
                }
            }
        }

        return Result.success()
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "pool_maintenance"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Mantenimiento", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}
