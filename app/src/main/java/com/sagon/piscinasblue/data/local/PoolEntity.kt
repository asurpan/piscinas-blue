package com.sagon.piscinasblue.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pools")
data class PoolEntity(
    @PrimaryKey val id: String,
    val name: String,
    val volumeM3: Double,
    val lastPh: Double,
    val lastChlorine: Double,
    val isWinterMode: Boolean,
    val lastUpdate: Long = System.currentTimeMillis(),
    val lastTabletChange: Long = System.currentTimeMillis(),
    val userConsumptionFactor: Double = 1.0,
    val lastSafetyCheck: Long = 0L,
    val tabletQuantity: Int = 1,
    val isHolidayMode: Boolean = false,
    val pumpHp: Double = 0.75,
    val lastFilterWash: Long = 0L,
    val lastWinterProductDate: Long = 0L,
    val winterProductType: String = ""
)
