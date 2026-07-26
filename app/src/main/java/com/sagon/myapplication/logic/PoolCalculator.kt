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
     * Calcula las horas de filtrado recomendadas de forma inteligente.
     * Factores: Temperatura, Estado del agua (pH/Cloro) y Modo.
     */
    fun calculateFilteringHours(data: PoolData, temperature: Double): Double {
        var baseHours = if (data.isWinterMode) {
            (temperature / 3.0).coerceIn(1.0, 4.0)
        } else {
            (temperature / 2.0).coerceIn(4.0, 12.0)
        }

        // Inteligencia Adicional: Penalizaciones por desequilibrio
        // Si el pH es muy alto, el agua tiende a enturbiarse -> filtrar más
        if (data.currentPh > 7.8) baseHours += 2.0
        
        // Si el cloro es muy bajo, riesgo de algas -> filtrar más para mover el producto
        if (data.currentChlorine < 0.5) baseHours += 2.0
        
        return baseHours.coerceAtMost(24.0)
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
        var days = 7.0
        if (currentTemp > 30) days -= 1.5
        if (currentTemp > 35) days -= 1.0
        if (windSpeed > 20) days -= 1.0
        days *= data.userConsumptionFactor
        return days.toInt().coerceAtLeast(1)
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
