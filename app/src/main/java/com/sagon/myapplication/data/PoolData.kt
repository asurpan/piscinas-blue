package com.sagon.myapplication.data

data class PoolData(
    val volumeM3: Double = 25.0,
    val currentPh: Double = 7.2,
    val currentChlorine: Double = 1.0,
    val waterTemperature: Double = 25.0,
    val isWinterMode: Boolean = false,
    val lastSafetyCheck: Long = 0L,
    val lastTabletChange: Long = System.currentTimeMillis(),
    val userConsumptionFactor: Double = 1.0,
    val tabletQuantity: Int = 1,
    val isHolidayMode: Boolean = false,
    val pumpHp: Double = 0.75,
    val lastFilterWash: Long = 0L,
    val lastWinterProductDate: Long = 0L,
    val winterProductType: String = "" // "LIQUIDO" o "BOYA"
)
