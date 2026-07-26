package com.sagon.piscinasblue.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PoolDao {
    @Query("SELECT * FROM pools")
    fun getAllPools(): Flow<List<PoolEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPool(pool: PoolEntity)

    @Delete
    suspend fun deletePool(pool: PoolEntity)

    @Query("SELECT * FROM pools WHERE id = :poolId")
    suspend fun getPoolById(poolId: String): PoolEntity?

    // Historial de Mantenimiento
    @Query("SELECT * FROM maintenance_logs ORDER BY date DESC")
    fun getAllLogs(): Flow<List<MaintenanceLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: MaintenanceLogEntity)

    @Delete
    suspend fun deleteLog(log: MaintenanceLogEntity)

    @Query("DELETE FROM maintenance_logs")
    suspend fun deleteAllLogs()
}
