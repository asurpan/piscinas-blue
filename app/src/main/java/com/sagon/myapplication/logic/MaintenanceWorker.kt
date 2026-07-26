package com.sagon.myapplication.logic

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sagon.myapplication.R
import com.sagon.myapplication.data.local.AppDatabase
import com.sagon.myapplication.data.PoolData
import kotlinx.coroutines.flow.firstOrNull

class MaintenanceWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val pools = database.poolDao().getAllPools().firstOrNull()
        
        if (!pools.isNullOrEmpty()) {
            val poolEntity = pools.first()
            val poolData = PoolData(
                volumeM3 = poolEntity.volumeM3,
                currentPh = poolEntity.lastPh,
                currentChlorine = poolEntity.lastChlorine,
                isWinterMode = poolEntity.isWinterMode,
                lastTabletChange = poolEntity.lastTabletChange,
                userConsumptionFactor = poolEntity.userConsumptionFactor,
                tabletQuantity = poolEntity.tabletQuantity,
                isHolidayMode = poolEntity.isHolidayMode,
                pumpHp = poolEntity.pumpHp
            )

            if (!poolData.isWinterMode) {
                // Obtenemos el clima actual para el cálculo inteligente
                val weather = try {
                    WeatherManager.getWeatherData(37.38, -5.98)
                } catch (e: Exception) {
                    null
                }

                val temp = weather?.temp ?: 25.0
                val wind = weather?.windSpeed ?: 10.0
                val lifespan = PoolCalculator.calculateIntelligentTabletLifespan(poolData, temp, wind)
                
                val now = System.currentTimeMillis()
                val daysPassed = (now - poolData.lastTabletChange) / (1000 * 60 * 60 * 24).toDouble()

                if (daysPassed >= lifespan) {
                    showNotification(
                        "¡PASTILLA CADUCADA! ⚠️",
                        "La pastilla de cloro se ha agotado. Cámbiala ahora para mantener el agua cristalina."
                    )
                    return Result.success()
                }

                if (temp > 33.0) {
                    val flowRate = when (poolData.pumpHp) { 0.5 -> 10.0 0.75 -> 13.0 1.0 -> 16.0 1.5 -> 22.0 else -> 12.0 }
                    val hours = (poolData.volumeM3 / flowRate) * 3.0 // 3 vueltas en ola de calor
                    showNotification(
                        "¡ALERTA CALOR! 🌡️",
                        "Hoy hará ${temp.toInt()}°C. Sube el filtrado a ${String.format("%.1f", hours)} horas para evitar que el agua se estropee."
                    )
                    return Result.success()
                }
            }
        }

        showNotification(
            "Mantenimiento PISCINAS BLUE",
            "Es hora de revisar el nivel de cloro y pH de tu piscina. 🏊‍♂️"
        )
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
            .setSmallIcon(R.mipmap.ic_launcher) // Usando el icono de la app
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}
