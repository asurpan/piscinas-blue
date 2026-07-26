package com.sagon.myapplication.data.local

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
    val lastUpdate: Long = System.currentTimeMillis()
)
