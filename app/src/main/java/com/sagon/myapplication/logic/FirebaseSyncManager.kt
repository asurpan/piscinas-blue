package com.sagon.myapplication.logic

import com.google.firebase.firestore.FirebaseFirestore
import com.sagon.myapplication.data.PoolData
import kotlinx.coroutines.tasks.await

import com.sagon.myapplication.data.StealthConfig

object FirebaseSyncManager {
    private val db = FirebaseFirestore.getInstance()
    private const val COLLECTION_NAME = "piscinas"
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
            "lastUpdate" to System.currentTimeMillis()
        )
        try {
            db.collection(COLLECTION_NAME).document(poolId).set(poolMap).await()
        } catch (e: Exception) {
            // Error silencioso si no hay internet o falta google-services.json
        }
    }

    fun listenToPoolChanges(poolId: String, onUpdate: (PoolData) -> Unit) {
        db.collection(COLLECTION_NAME).document(poolId)
            .addSnapshotListener { snapshot, e ->
                if (e == null && snapshot != null && snapshot.exists()) {
                    val data = PoolData(
                        volumeM3 = snapshot.getDouble("volumeM3") ?: 0.0,
                        currentPh = snapshot.getDouble("currentPh") ?: 7.4,
                        currentChlorine = snapshot.getDouble("currentChlorine") ?: 1.2,
                        isWinterMode = snapshot.getBoolean("isWinterMode") ?: false
                    )
                    onUpdate(data)
                }
            }
    }
}
