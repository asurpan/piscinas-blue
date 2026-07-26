package com.sagon.myapplication.data

import com.sagon.myapplication.data.local.PoolDao
import com.sagon.myapplication.data.local.PoolEntity
import com.sagon.myapplication.data.local.MaintenanceLogEntity
import kotlinx.coroutines.flow.Flow

class PoolRepository(private val poolDao: PoolDao) {

    val allPools: Flow<List<PoolEntity>> = poolDao.getAllPools()
    val allLogs: Flow<List<MaintenanceLogEntity>> = poolDao.getAllLogs()

    suspend fun updatePool(pool: PoolEntity) {
        poolDao.insertPool(pool)
    }

    suspend fun getPool(id: String): PoolEntity? {
        return poolDao.getPoolById(id)
    }

    suspend fun deletePool(pool: PoolEntity) {
        poolDao.deletePool(pool)
    }

    suspend fun addLog(log: MaintenanceLogEntity) {
        poolDao.insertLog(log)
    }

    suspend fun clearHistory() {
        poolDao.deleteAllLogs()
    }
}
