package com.sagon.piscinasblue.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maintenance_logs")
data class MaintenanceLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long = System.currentTimeMillis(),
    val actionType: String, // "PH", "CLORO", "PASTILLA", "SEGURIDAD", "LIMPIEZA"
    val description: String,
    val value: String = ""
)
