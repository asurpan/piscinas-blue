package com.sagon.myapplication.logic

import com.sagon.myapplication.data.PoolData

object PoolCalculator {

    const val IDEAL_PH = 7.4
    const val IDEAL_CHLORINE = 1.2

    fun calculateChlorineAdjustment(data: PoolData): Double {
        val diff = IDEAL_CHLORINE - data.currentChlorine
        if (diff <= 0) return 0.0
        return diff * data.volumeM3 * 10
    }

    fun calculatePhAdjustment(data: PoolData): Double {
        val diff = data.currentPh - IDEAL_PH
        if (diff <= 0) return 0.0
        return (diff / 0.1) * (data.volumeM3 / 10.0) * 100.0
    }

    /**
     * Calcula las horas de filtrado recomendadas usando ingeniería hidráulica.
     * Basado en: Volumen, Caudal del motor (CV) y Factor Climático (Vueltas de agua).
     */
    fun calculateFilteringHours(data: PoolData, temperature: Double): Double {
        // Mapeo de Potencia (CV) a Caudal aproximado (m³/h)
        val flowRate = when (data.pumpHp) {
            0.5 -> 10.0
            0.75 -> 13.0
            1.0 -> 16.0
            1.5 -> 22.0
            else -> 12.0
        }

        // Factor de Recirculación (cuántas veces debe pasar toda el agua por el filtro)
        val cycles = when {
            data.isWinterMode -> 0.5 // Hibernación
            temperature < 20 -> 1.0
            temperature < 26 -> 1.5
            temperature < 30 -> 2.0
            temperature < 34 -> 2.5
            else -> 3.0 // Ola de calor
        }

        // Fórmula: (Volumen / Caudal) * Ciclos
        var hours = (data.volumeM3 / flowRate) * cycles

        // Ajustes extra por calidad del agua
        if (data.currentPh > 7.8) hours += 1.0
        if (data.currentChlorine < 0.5) hours += 1.5
        
        return hours.coerceIn(1.0, 24.0)
    }

    fun calculateWinterProduct(volume: Double): Double {
        return (volume / 10.0) * 0.75
    }

    fun calculateShockChlorine(volume: Double): Double {
        return volume * 15.0
    }

    fun getPoolScore(data: PoolData): Int {
        var score = 100
        val phDiff = Math.abs(data.currentPh - IDEAL_PH)
        score -= (phDiff * 50).toInt()
        val clDiff = Math.abs(data.currentChlorine - IDEAL_CHLORINE)
        score -= (clDiff * 40).toInt()
        return score.coerceIn(0, 100)
    }

    fun calculateIntelligentTabletLifespan(data: PoolData, currentTemp: Double, windSpeed: Double): Int {
        var baseDays = 7.0
        if (currentTemp > 30) baseDays -= 1.5
        if (currentTemp > 35) baseDays -= 1.0
        if (windSpeed > 20) baseDays -= 1.0
        
        val consumptionFactor = data.userConsumptionFactor
        
        val finalDays = if (data.tabletQuantity > 1 && data.isHolidayMode) {
            // Modo vacaciones: dosificador cerrado, las pastillas duran mucho más
            baseDays * (1.0 + (data.tabletQuantity - 1) * 0.75) * consumptionFactor
        } else {
            // Modo normal: más pastillas aumentan poco la duración, solo el pico de cloro
            baseDays * (1.0 + (data.tabletQuantity - 1) * 0.15) * consumptionFactor
        }
        
        return finalDays.toInt().coerceAtLeast(1)
    }

    fun getPhStatusKey(ph: Double): String {
        return when {
            ph < 7.2 -> "low"
            ph > 7.6 -> "high"
            else -> "ideal"
        }
    }

    fun getChlorineStatusKey(cl: Double): String {
        return when {
            cl < 1.0 -> "low"
            cl > 1.5 -> "high"
            else -> "ideal"
        }
    }
}
