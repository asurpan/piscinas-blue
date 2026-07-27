package com.sagon.piscinasblue.ui

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
import com.sagon.piscinasblue.data.PoolData
import com.sagon.piscinasblue.data.PoolRepository
import com.sagon.piscinasblue.data.local.AppDatabase
import com.sagon.piscinasblue.data.local.PoolEntity
import com.sagon.piscinasblue.data.local.MaintenanceLogEntity
import com.sagon.piscinasblue.logic.*
import com.sagon.piscinasblue.data.PreferenceManager
import com.sagon.piscinasblue.data.StealthConfig
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PoolViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PoolRepository
    private val preferenceManager = PreferenceManager(application)
    private var poolId = "piscina_principal" // Valor inicial que se actualizará
    private val speechManager = SpeechRecognizerManager(application)

    private val _poolIdState = MutableStateFlow("")
    val poolIdState: StateFlow<String> = _poolIdState.asStateFlow()

    private val _uiState = MutableStateFlow(PoolUiState())
    val uiState: StateFlow<PoolUiState> = _uiState.asStateFlow()

    private val _hasChanges = MutableStateFlow(false)
    val hasChanges: StateFlow<Boolean> = _hasChanges.asStateFlow()

    private val _showShareHighlight = MutableStateFlow(false)
    val showShareHighlight: StateFlow<Boolean> = _showShareHighlight.asStateFlow()

    private val _stealthConfig = MutableStateFlow(StealthConfig())
    val stealthConfig: StateFlow<StealthConfig> = _stealthConfig.asStateFlow()

    private val _currentLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val currentLocation: StateFlow<Pair<Double, Double>?> = _currentLocation.asStateFlow()

    private val _cityName = MutableStateFlow("Sevilla (Por defecto)")
    val cityName: StateFlow<String> = _cityName.asStateFlow()

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

    val isGenuine = preferenceManager.isGenuine.stateIn(
        viewModelScope, SharingStarted.Eagerly, false
    )

    val tabletActionCount = preferenceManager.tabletActionCount.stateIn(
        viewModelScope, SharingStarted.Eagerly, 0
    )

    val hasSeenTabletInfo = preferenceManager.hasSeenTabletInfo.stateIn(
        viewModelScope, SharingStarted.Eagerly, false
    )

    val hasSetVolume = preferenceManager.hasSetVolume.stateIn(
        viewModelScope, SharingStarted.Eagerly, true // Default true para no parpadear si hay error
    )

    val showSuccessAnimation = mutableStateOf(false)
    val showSafetyDialog = mutableStateOf(false)

    init {
        val poolDao = AppDatabase.getDatabase(application).poolDao()
        repository = PoolRepository(poolDao)
        maintenanceLogs = repository.allLogs.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        
        setupPoolId()
        observeLocation()
        validateEnvironment()
        startWeatherSync()
        fetchConfig()
        
        viewModelScope.launch { preferenceManager.incrementUsage() }
    }

    private fun setupPoolId() {
        viewModelScope.launch {
            preferenceManager.poolId.collect { id ->
                if (id == null) {
                    val newId = "P-" + (1..6).map { (('A'..'Z') + ('0'..'9')).random() }.joinToString("")
                    preferenceManager.setPoolId(newId)
                } else {
                    poolId = id
                    _poolIdState.value = id
                    loadInitialData()
                    setupCloudListeners()
                }
            }
        }
    }

    private fun setupCloudListeners() {
        FirebaseSyncManager.listenToPoolChanges(poolId) { cloudData ->
            _uiState.update { it.copy(poolData = cloudData) }
        }
        FirebaseSyncManager.listenToLogs(poolId) { cloudLogs ->
            viewModelScope.launch {
                cloudLogs.forEach { log ->
                    repository.addLog(log) // El DAO ya maneja conflictos por fecha
                }
            }
        }
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
                        isWinterMode = entity.isWinterMode,
                        lastTabletChange = entity.lastTabletChange,
                        userConsumptionFactor = entity.userConsumptionFactor,
                        lastSafetyCheck = entity.lastSafetyCheck,
                        tabletQuantity = entity.tabletQuantity,
                        isHolidayMode = entity.isHolidayMode,
                        pumpHp = entity.pumpHp,
                        lastFilterWash = entity.lastFilterWash,
                        lastWinterProductDate = entity.lastWinterProductDate,
                        winterProductType = entity.winterProductType
                    )
                } else PoolData()
                _uiState.update { it.copy(poolData = data) }
            }
        }
    }

    private fun observeLocation() {
        viewModelScope.launch {
            preferenceManager.location.collect { loc ->
                if (loc != null) {
                    _currentLocation.value = loc
                    _cityName.value = LocationHelper.getCityName(getApplication(), loc.first, loc.second)
                    refreshWeather(loc.first, loc.second)
                } else {
                    // Fallback a Sevilla si no hay ubicación guardada
                    refreshWeather(37.38, -5.98)
                }
            }
        }
    }

    private fun validateEnvironment() {
        viewModelScope.launch {
            _stealthConfig.collect { config ->
                // Si el sistema de "truco" está apagado remotamente, marcamos al usuario como VIP
                // Esto sucede mientras la app es de pago en la Store.
                if (!config.isEnabled && !isGenuine.value) {
                    preferenceManager.setGenuine(true)
                }
            }
        }
    }

    private fun startWeatherSync() {
        // La observación de ubicación ya dispara el primer refreshWeather
    }

    private fun refreshWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            val weather = WeatherManager.getWeatherData(lat, lon)
            _uiState.update { it.copy(weather = weather) }
        }
    }

    fun updateLocation(context: Context) {
        viewModelScope.launch {
            val loc = LocationHelper.getCurrentLocation(context)
            if (loc != null) {
                preferenceManager.setLocation(loc.first, loc.second)
            }
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
        val newVol = volume.toDoubleOrNull() ?: current.volumeM3
        
        // Si el volumen cambia y es distinto al de por defecto (25.0) o simplemente el usuario lo toca
        if (newVol != current.volumeM3 && !hasSetVolume.value) {
            viewModelScope.launch { preferenceManager.setHasSetVolume(true) }
        }

        val newData = current.copy(
            volumeM3 = newVol,
            currentPh = ph.toDoubleOrNull() ?: current.currentPh,
            currentChlorine = cl.toDoubleOrNull() ?: current.currentChlorine,
            isWinterMode = isWinter
        )
        _uiState.update { it.copy(poolData = newData) }
        _hasChanges.value = true // Marcar que hay cambios pendientes
    }

    fun incrementPh() {
        val current = _uiState.value.poolData
        val nextVal = (Math.round((current.currentPh + 0.1) * 10.0) / 10.0).coerceAtMost(10.0)
        _uiState.update { it.copy(poolData = it.poolData.copy(currentPh = nextVal)) }
        _hasChanges.value = true
    }

    fun decrementPh() {
        val current = _uiState.value.poolData
        val nextVal = (Math.round((current.currentPh - 0.1) * 10.0) / 10.0).coerceAtLeast(0.0)
        _uiState.update { it.copy(poolData = it.poolData.copy(currentPh = nextVal)) }
        _hasChanges.value = true
    }

    fun incrementCl() {
        val current = _uiState.value.poolData
        val nextVal = (Math.round((current.currentChlorine + 0.1) * 10.0) / 10.0).coerceAtMost(10.0)
        _uiState.update { it.copy(poolData = it.poolData.copy(currentChlorine = nextVal)) }
        _hasChanges.value = true
    }

    fun decrementCl() {
        val current = _uiState.value.poolData
        val nextVal = (Math.round((current.currentChlorine - 0.1) * 10.0) / 10.0).coerceAtLeast(0.0)
        _uiState.update { it.copy(poolData = it.poolData.copy(currentChlorine = nextVal)) }
        _hasChanges.value = true
    }

    fun saveMaintenance() {
        val data = _uiState.value.poolData
        if (PoolCalculator.getPoolScore(data) == 100) showSuccessAnimation.value = true
        
        addLog("MEDICIÓN", "Niveles registrados: pH ${data.currentPh}, Cloro ${data.currentChlorine}")
        saveData(data)
        _hasChanges.value = false // Cambios guardados
        _showShareHighlight.value = true // Incitar a compartir
    }

    fun updatePumpHp(hp: Double) {
        val current = _uiState.value.poolData
        val newData = current.copy(pumpHp = hp)
        _uiState.update { it.copy(poolData = newData) }
        saveData(newData)
    }

    fun completeFilterWash() {
        val current = _uiState.value.poolData
        val newData = current.copy(lastFilterWash = System.currentTimeMillis())
        _uiState.update { it.copy(poolData = newData) }
        addLog("FILTRO", "Lavado y enjuague realizado correctamente")
        saveData(newData)
    }

    fun updateWinterProduct(type: String) {
        val current = _uiState.value.poolData
        val newData = current.copy(
            lastWinterProductDate = System.currentTimeMillis(),
            winterProductType = type
        )
        _uiState.update { it.copy(poolData = newData) }
        addLog("INVIERNO", "Aplicado producto invernador ($type)")
        saveData(newData)
        _showShareHighlight.value = true // Incitar a compartir
    }

    fun deleteLogEntry(log: MaintenanceLogEntity) {
        viewModelScope.launch {
            repository.deleteLog(log)
        }
    }

    fun onTabletChanged(context: Context, quantity: Int, holidayMode: Boolean): String {
        val current = _uiState.value.poolData
        val weather = _uiState.value.weather
        val now = System.currentTimeMillis()
        
        val diffMs = now - current.lastTabletChange
        val daysPassed = (diffMs / (1000 * 60 * 60 * 24).toDouble()).coerceAtLeast(0.1)
        
        // La IA aprende basándose en la última configuración (si eran 4 pastillas, calcula el factor sobre eso)
        val predictedDays = PoolCalculator.calculateIntelligentTabletLifespan(current, weather.temp, weather.windSpeed)
        val newFactor = (current.userConsumptionFactor * (daysPassed / predictedDays.toDouble())).coerceIn(0.5, 2.0)
        
        val newData = current.copy(
            lastTabletChange = now, 
            userConsumptionFactor = newFactor,
            tabletQuantity = quantity,
            isHolidayMode = holidayMode
        )
        _uiState.update { it.copy(poolData = newData) }
        
        val modeText = if (holidayMode) "en Modo Vacaciones (dosificador cerrado)" else "en el skimmer"
        val summary = "¡Cambio registrado!\n\n" +
                "Has puesto $quantity pastilla(s) $modeText.\n" +
                "La carga anterior duró ${String.format("%.1f", daysPassed)} días.\n" +
                "Factor IA ajustado a ${String.format("%.2f", newFactor)}.\n" +
                "Próximo aviso calculado según el clima y tus hábitos."
        
        addLog("PASTILLA", "Cambio realizado ($quantity past.). Modo: ${if(holidayMode) "Vacaciones" else "Normal"}")
        saveData(newData)
        
        viewModelScope.launch {
            preferenceManager.incrementTabletAction()
            preferenceManager.setHasSeenTabletInfo(true)
        }
        
        triggerHapticFeedback(context)
        _showShareHighlight.value = true // Incitar a compartir
        return summary
    }

    fun confirmSafetyCheck() {
        val newData = _uiState.value.poolData.copy(lastSafetyCheck = System.currentTimeMillis())
        _uiState.update { it.copy(poolData = newData) }
        addLog("SEGURIDAD", "Prueba del diferencial realizada")
        saveData(newData)
    }

    private fun addLog(type: String, desc: String) {
        viewModelScope.launch {
            val log = MaintenanceLogEntity(actionType = type, description = desc)
            repository.addLog(log)
            FirebaseSyncManager.syncLogToCloud(poolId, log)
        }
    }

    fun clearHistory() {
        viewModelScope.launch { repository.clearHistory() }
    }

    private fun saveData(data: PoolData) {
        viewModelScope.launch {
            repository.updatePool(
                PoolEntity(
                    id = poolId,
                    name = "Mi Piscina",
                    volumeM3 = data.volumeM3,
                    lastPh = data.currentPh,
                    lastChlorine = data.currentChlorine,
                    isWinterMode = data.isWinterMode,
                    lastTabletChange = data.lastTabletChange,
                    userConsumptionFactor = data.userConsumptionFactor,
                    lastSafetyCheck = data.lastSafetyCheck,
                    tabletQuantity = data.tabletQuantity,
                    isHolidayMode = data.isHolidayMode,
                    pumpHp = data.pumpHp,
                    lastFilterWash = data.lastFilterWash,
                    lastWinterProductDate = data.lastWinterProductDate,
                    winterProductType = data.winterProductType
                )
            )
            FirebaseSyncManager.syncPoolToCloud(poolId, data)
        }
    }

    fun shareReport(context: Context) {
        _showShareHighlight.value = false // Quitar iluminación al compartir
        val uiState = _uiState.value
        val data = uiState.poolData
        val weather = uiState.weather
        val score = PoolCalculator.getPoolScore(data)
        val pumpHours = PoolCalculator.calculateFilteringHours(data, weather.temp).toInt()
        
        val mode = if (data.isWinterMode) "INVERNADOR ❄️" else "VERANO ☀️"
        
        val isPerfect = score == 100
        val header = if (isPerfect) {
            "🌟 *¡ESTADO PERFECTO!* 🌟\n" +
            "¡El agua de mi piscina está hoy de cine! 💎\n\n"
        } else {
            "*INFORME PISCINA* 🏊‍♂️\n"
        }
        
        val text = "*PISCINAS BLUE*\n" +
                header +
                "--------------------------\n" +
                "📊 *PUNTUACIÓN*: $score/100\n" +
                "📅 *MODO*: $mode\n\n" +
                "🌡️ *CLIMA*: ${weather.temp.toInt()}°C | ${weather.windSpeed.toInt()} km/h\n" +
                "💧 *VOLUMEN*: ${data.volumeM3} m³\n" +
                "🧪 *pH*: ${data.currentPh}\n" +
                "🧼 *CLORO*: ${data.currentChlorine} ppm\n\n" +
                "⚙️ *RECOMENDACIÓN*:\n" +
                "Filtrado: $pumpHours h/día\n" +
                "--------------------------\n" +
                (if (isPerfect) "¡Venid a daros un chapuzón! 🔥" else "_Generado por Piscinas Blue_")

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

    fun joinPool(newId: String) {
        viewModelScope.launch {
            repository.clearHistory()
            preferenceManager.setPoolId(newId)
            // El observador de poolId en setupPoolId se encargará de recargar todo
        }
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
