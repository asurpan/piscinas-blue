package com.sagon.piscinasblue.logic

import com.google.firebase.firestore.FirebaseFirestore
import com.sagon.piscinasblue.data.PoolData
import kotlinx.coroutines.tasks.await

import com.sagon.piscinasblue.data.StealthConfig

object FirebaseSyncManager {
    private val db = FirebaseFirestore.getInstance()
    private const val COLLECTION_NAME = "piscinas"
    private const val LOGS_COLLECTION = "historial"
    private const val CONFIG_COLLECTION = "config"
    private const val APP_CONFIG_DOC = "app_stealth"

    suspend fun fetchStealthConfig(): StealthConfig {
        return try {
            val snapshot = db.collection(CONFIG_COLLECTION).document(APP_CONFIG_DOC).get().await()
            if (snapshot.exists()) {
                StealthConfig(
                    isEnabled = snapshot.getBoolean("isEnabled") ?: false,
                    priceText = snapshot.getString("priceText") ?: "",
                    bizumText = snapshot.getString("bizumText") ?: "",
                    instructions = snapshot.getString("instructions") ?: "",
                    activationCode = snapshot.getString("activationCode") ?: "121212"
                )
            } else StealthConfig()
        } catch (e: Exception) {
            StealthConfig()
        }
    }

    suspend fun syncPoolToCloud(poolId: String, data: PoolData) {
        val poolMap = hashMapOf(
            "volumeM3" to data.volumeM3,
            "currentPh" to data.currentPh,
            "currentChlorine" to data.currentChlorine,
            "isWinterMode" to data.isWinterMode,
            "lastUpdate" to System.currentTimeMillis(),
            "lastTabletChange" to data.lastTabletChange,
            "userConsumptionFactor" to data.userConsumptionFactor,
            "lastSafetyCheck" to data.lastSafetyCheck,
            "tabletQuantity" to data.tabletQuantity,
            "isHolidayMode" to data.isHolidayMode,
            "pumpHp" to data.pumpHp,
            "lastFilterWash" to data.lastFilterWash,
            "lastWinterProductDate" to data.lastWinterProductDate,
            "winterProductType" to data.winterProductType
        )
        try {
            db.collection(COLLECTION_NAME).document(poolId).set(poolMap).await()
        } catch (e: Exception) {
            // Error silencioso
        }
    }

    fun listenToPoolChanges(poolId: String, onUpdate: (PoolData) -> Unit) {
        db.collection(COLLECTION_NAME).document(poolId)
            .addSnapshotListener { snapshot, e ->
                if (e == null && snapshot != null && snapshot.exists()) {
                    val data = PoolData(
                        volumeM3 = snapshot.getDouble("volumeM3") ?: 25.0,
                        currentPh = snapshot.getDouble("currentPh") ?: 7.2,
                        currentChlorine = snapshot.getDouble("currentChlorine") ?: 1.0,
                        isWinterMode = snapshot.getBoolean("isWinterMode") ?: false,
                        lastTabletChange = snapshot.getLong("lastTabletChange") ?: System.currentTimeMillis(),
                        userConsumptionFactor = snapshot.getDouble("userConsumptionFactor") ?: 1.0,
                        lastSafetyCheck = snapshot.getLong("lastSafetyCheck") ?: 0L,
                        tabletQuantity = snapshot.getLong("tabletQuantity")?.toInt() ?: 1,
                        isHolidayMode = snapshot.getBoolean("isHolidayMode") ?: false,
                        pumpHp = snapshot.getDouble("pumpHp") ?: 0.75,
                        lastFilterWash = snapshot.getLong("lastFilterWash") ?: 0L,
                        lastWinterProductDate = snapshot.getLong("lastWinterProductDate") ?: 0L,
                        winterProductType = snapshot.getString("winterProductType") ?: ""
                    )
                    onUpdate(data)
                }
            }
    }

    suspend fun syncLogToCloud(poolId: String, log: com.sagon.piscinasblue.data.local.MaintenanceLogEntity) {
        val logMap = hashMapOf(
            "date" to log.date,
            "actionType" to log.actionType,
            "description" to log.description,
            "value" to log.value
        )
        try {
            db.collection(COLLECTION_NAME).document(poolId)
                .collection(LOGS_COLLECTION).document(log.date.toString()).set(logMap).await()
        } catch (e: Exception) {}
    }

    fun listenToLogs(poolId: String, onUpdate: (List<com.sagon.piscinasblue.data.local.MaintenanceLogEntity>) -> Unit) {
        db.collection(COLLECTION_NAME).document(poolId)
            .collection(LOGS_COLLECTION)
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e == null && snapshot != null) {
                    val logs = snapshot.documents.map { doc ->
                        com.sagon.piscinasblue.data.local.MaintenanceLogEntity(
                            date = doc.getLong("date") ?: 0L,
                            actionType = doc.getString("actionType") ?: "",
                            description = doc.getString("description") ?: "",
                            value = doc.getString("value") ?: ""
                        )
                    }
                    onUpdate(logs)
                }
            }
    }
}
