package com.sagon.myapplication.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.os.Build
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sagon.myapplication.data.PoolData
import com.sagon.myapplication.data.PoolRepository
import com.sagon.myapplication.data.local.AppDatabase
import com.sagon.myapplication.data.local.PoolEntity
import com.sagon.myapplication.data.local.MaintenanceLogEntity
import com.sagon.myapplication.logic.*
import com.sagon.myapplication.data.PreferenceManager
import com.sagon.myapplication.data.StealthConfig
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PoolViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PoolRepository
    private val preferenceManager = PreferenceManager(application)
    private val poolId = "piscina_principal"
    private val speechManager = SpeechRecognizerManager(application)

    private val _uiState = MutableStateFlow(PoolUiState())
    val uiState: StateFlow<PoolUiState> = _uiState.asStateFlow()

    private val _stealthConfig = MutableStateFlow(StealthConfig())
    val stealthConfig: StateFlow<StealthConfig> = _stealthConfig.asStateFlow()

    val maintenanceLogs: StateFlow<List<MaintenanceLogEntity>>

    val isOnboardingCompleted = preferenceManager.isOnboardingCompleted.stateIn(
        viewModelScope, SharingStarted.Eagerly, false
    )

    val usageCount = preferenceManager.usageCount.stateIn(
        viewModelScope, SharingStarted.Eagerly, 0
    )

    val isActivated = preferenceManager.isActivated.stateIn(
        viewModelScope, SharingStarted.Eagerly, false
    )

    val showSuccessAnimation = mutableStateOf(false)
    val showSafetyDialog = mutableStateOf(false)

    init {
        val poolDao = AppDatabase.getDatabase(application).poolDao()
        repository = PoolRepository(poolDao)
        maintenanceLogs = repository.allLogs.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        
        loadInitialData()
        startWeatherSync()
        fetchConfig()
        
        viewModelScope.launch { preferenceManager.incrementUsage() }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            repository.allPools.collect { pools ->
                val data = if (pools.isNotEmpty()) {
                    val entity = pools.first()
                    PoolData(
                        volumeM3 = entity.volumeM3,
                        currentPh = entity.lastPh,
                        currentChlorine = entity.lastChlorine,
                        isWinterMode = entity.isWinterMode
                    )
                } else PoolData()
                _uiState.update { it.copy(poolData = data) }
            }
        }
        FirebaseSyncManager.listenToPoolChanges(poolId) { cloudData ->
            _uiState.update { it.copy(poolData = cloudData) }
        }
    }

    private fun startWeatherSync() {
        viewModelScope.launch {
            val weather = WeatherManager.getWeatherData(37.38, -5.98)
            _uiState.update { it.copy(weather = weather) }
        }
    }

    private fun fetchConfig() {
        viewModelScope.launch {
            _stealthConfig.value = FirebaseSyncManager.fetchStealthConfig()
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch { preferenceManager.setOnboardingCompleted(true) }
    }

    fun activateApp(code: String): Boolean {
        return if (code == _stealthConfig.value.activationCode) {
            viewModelScope.launch { preferenceManager.setActivated(true) }
            true
        } else false
    }

    fun updatePoolData(volume: String, ph: String, cl: String, isWinter: Boolean) {
        val current = _uiState.value.poolData
        val newData = current.copy(
            volumeM3 = volume.toDoubleOrNull() ?: current.volumeM3,
            currentPh = ph.toDoubleOrNull() ?: current.currentPh,
            currentChlorine = cl.toDoubleOrNull() ?: current.currentChlorine,
            isWinterMode = isWinter
        )
        _uiState.update { it.copy(poolData = newData) }
        if (PoolCalculator.getPoolScore(newData) == 100) showSuccessAnimation.value = true
        
        if (volume != current.volumeM3.toString()) addLog("CAPACIDAD", "Actualizado volumen a $volume m³")
        if (ph != current.currentPh.toString()) addLog("pH", "Ajustado nivel de pH a $ph")
        if (cl != current.currentChlorine.toString()) addLog("CLORO", "Ajustado nivel de cloro a $cl")
        
        saveData(newData)
    }

    fun onTabletChanged(context: Context) {
        val current = _uiState.value.poolData
        val weather = _uiState.value.weather
        val now = System.currentTimeMillis()
        val daysPassed = (now - current.lastTabletChange) / (1000 * 60 * 60 * 24).toDouble()
        
        val predictedDays = PoolCalculator.calculateIntelligentTabletLifespan(current, weather.temp, weather.windSpeed)
        val newFactor = (current.userConsumptionFactor * (daysPassed / predictedDays.toDouble())).coerceIn(0.5, 2.0)
        
        val newData = current.copy(lastTabletChange = now, userConsumptionFactor = newFactor)
        _uiState.update { it.copy(poolData = newData) }
        addLog("PASTILLA", "Cambio de pastilla realizado")
        saveData(newData)
        triggerHapticFeedback(context)
    }

    fun confirmSafetyCheck() {
        val newData = _uiState.value.poolData.copy(lastSafetyCheck = System.currentTimeMillis())
        _uiState.update { it.copy(poolData = newData) }
        addLog("SEGURIDAD", "Prueba del diferencial realizada")
        saveData(newData)
    }

    private fun addLog(type: String, desc: String) {
        viewModelScope.launch {
            repository.addLog(MaintenanceLogEntity(actionType = type, description = desc))
        }
    }

    fun clearHistory() {
        viewModelScope.launch { repository.clearHistory() }
    }

    private fun saveData(data: PoolData) {
        viewModelScope.launch {
            repository.updatePool(PoolEntity(poolId, "Mi Piscina", data.volumeM3, data.currentPh, data.currentChlorine, data.isWinterMode))
            FirebaseSyncManager.syncPoolToCloud(poolId, data)
        }
    }

    fun shareReport(context: Context) {
        val data = _uiState.value.poolData
        val score = PoolCalculator.getPoolScore(data)
        val text = "*PISCINAS BLUE* 🏊‍♂️\nNota: $score/100\nAcción: ${if (data.isWinterMode) "Invernador" else "Cloro"}"
        context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }, "Compartir Informe"))
    }

    fun startVoiceInput(onResult: (String) -> Unit) {
        speechManager.startListening(resultCallback = onResult)
    }

    fun triggerHapticFeedback(context: Context) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION") context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    fun deleteUserAccount(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deletePool(PoolEntity(poolId, "", 0.0, 0.0, 0.0, false))
            repository.clearHistory()
            preferenceManager.setOnboardingCompleted(false)
            preferenceManager.setActivated(false)
            onComplete()
        }
    }

    override fun onCleared() {
        speechManager.destroy()
    }
}
