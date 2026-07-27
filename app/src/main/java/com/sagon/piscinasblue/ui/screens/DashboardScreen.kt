package com.sagon.piscinasblue.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sagon.piscinasblue.R
import com.sagon.piscinasblue.logic.PoolCalculator
import com.sagon.piscinasblue.ui.PoolViewModel
import com.sagon.piscinasblue.ui.components.*
import com.sagon.piscinasblue.logic.HelpContent

@Composable
fun DashboardScreen(
    viewModel: PoolViewModel = viewModel(), 
    onOpenAssistant: (String?) -> Unit,
    onOpenHistory: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val poolId by viewModel.poolIdState.collectAsState()
    val hasChanges by viewModel.hasChanges.collectAsState()
    val hasSetVolume by viewModel.hasSetVolume.collectAsState()
    val showShareHighlight by viewModel.showShareHighlight.collectAsState()
    val tabletActionCount by viewModel.tabletActionCount.collectAsState()
    val hasSeenTabletInfo by viewModel.hasSeenTabletInfo.collectAsState()
    
    val pool = uiState.poolData
    val weather = uiState.weather
    val context = LocalContext.current

    var helpContent by remember { mutableStateOf<Pair<String, String>?>(null) }
    var showSettings by remember { mutableStateOf(false) }
    var showVolumeCalc by remember { mutableStateOf(false) }
    var showTabletConfirm by remember { mutableStateOf(false) }
    var showPumpInfo by remember { mutableStateOf(false) }
    var showPumpConfirm by remember { mutableStateOf<Double?>(null) }
    var showFilterWash by remember { mutableStateOf(false) }
    var showWinterConfirm by remember { mutableStateOf(false) }
    var showSaveConfirm by remember { mutableStateOf(false) }
    var showSaveSuccess by remember { mutableStateOf(false) } // Nuevo estado
    var tabletResultSummary by remember { mutableStateOf<String?>(null) }
    var volumeToConfirm by remember { mutableStateOf<Double?>(null) }
    var showVolumeSuccess by remember { mutableStateOf(false) }
    var showHistoryHighlight by remember { mutableStateOf(false) }
    
    // Auto-ocultar el aviso del historial
    LaunchedEffect(showHistoryHighlight) {
        if (showHistoryHighlight) {
            kotlinx.coroutines.delay(4000)
            showHistoryHighlight = false
        }
    }

    // Estado temporal para el diálogo de pastillas
    var tempTabletQty by remember { mutableStateOf(1) }
    var tempHolidayMode by remember { mutableStateOf(false) }

    // Diálogos
    helpContent?.let { (title, content) -> 
        val botQuery = when {
            title.contains("pH", ignoreCase = true) -> "explicame como regular el ph de la piscina, niveles ideales y que productos usar"
            title.contains("Cloro", ignoreCase = true) -> "explicame como regular el cloro, niveles ideales, diferencia entre choque y mantenimiento y que comprar"
            else -> null
        }
        HelpDialog(
            title = title, 
            content = content,
            onBotClick = botQuery?.let { q -> { 
                helpContent = null
                onOpenAssistant(q)
            }},
            onDismiss = { helpContent = null }
        ) 
    }

    if (showSaveSuccess) {
        AlertDialog(
            onDismissRequest = { showSaveSuccess = false },
            title = { Text("¡Guardado!", fontWeight = FontWeight.Black, color = Color(0xFF4CAF50)) },
            text = { 
                Column {
                    Text("Los niveles se han registrado correctamente.")
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.History, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Pulsa el icono del reloj arriba para ver tu historial.", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showSaveSuccess = false }) { Text("ENTENDIDO") }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    volumeToConfirm?.let { vol ->
        AlertDialog(
            onDismissRequest = { volumeToConfirm = null },
            title = { Text("Confirmar Capacidad", fontWeight = FontWeight.Black) },
            text = { Text("¿Confirmas que tu piscina tiene una capacidad de $vol m³?\n\nEsto ajustará todos los cálculos de filtrado y productos químicos.") },
            confirmButton = {
                Button(onClick = {
                    viewModel.updatePoolData(vol.toString(), pool.currentPh.toString(), pool.currentChlorine.toString(), pool.isWinterMode)
                    volumeToConfirm = null
                    showVolumeSuccess = true
                }) { Text("CONFIRMAR") }
            },
            dismissButton = { TextButton(onClick = { volumeToConfirm = null }) { Text("CANCELAR") } },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    if (showVolumeSuccess) {
        AlertDialog(
            onDismissRequest = { showVolumeSuccess = false },
            title = { Text("¡Configurado!", fontWeight = FontWeight.Black, color = Color(0xFF4CAF50)) },
            text = { Text("La capacidad de tu piscina se ha guardado correctamente. Ahora los consejos de Blue Bot serán mucho más precisos.") },
            confirmButton = {
                Button(onClick = { showVolumeSuccess = false }) { Text("ENTENDIDO") }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    if (showSaveConfirm) {
        AlertDialog(
            onDismissRequest = { showSaveConfirm = false },
            title = { Text("Grabar Medición", fontWeight = FontWeight.Black) },
            text = { Text("¿Confirmas que quieres registrar estos niveles en el historial?\n\npH: ${pool.currentPh}\nCloro: ${pool.currentChlorine}") },
            confirmButton = {
                Button(onClick = {
                    viewModel.saveMaintenance()
                    showSaveConfirm = false
                    showSaveSuccess = true // Mostrar el aviso de éxito
                    showHistoryHighlight = true // Iluminar el icono de historial
                }) { Text("GRABAR") }
            },
            dismissButton = { TextButton(onClick = { showSaveConfirm = false }) { Text("CANCELAR") } },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }
    
    if (showTabletConfirm) {
        val showEducation = !hasSeenTabletInfo || (tabletActionCount > 0 && tabletActionCount % 30 == 0)

        AlertDialog(
            onDismissRequest = { showTabletConfirm = false },
            title = { Text("Registro de Pastillas", fontWeight = FontWeight.Black) },
            text = {
                Column {
                    Text("Indica cuántas pastillas has añadido:")
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledIconButton(
                            onClick = { if (tempTabletQty > 1) tempTabletQty-- },
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFE3F2FD))
                        ) {
                            Icon(Icons.Rounded.Remove, null, tint = Color(0xFF0D47A1))
                        }
                        Text(
                            text = tempTabletQty.toString(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 24.dp),
                            color = Color(0xFF0D47A1)
                        )
                        FilledIconButton(
                            onClick = { if (tempTabletQty < 6) tempTabletQty++ },
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFE3F2FD))
                        ) {
                            Icon(Icons.Rounded.Add, null, tint = Color(0xFF0D47A1))
                        }
                    }

                    if (tempTabletQty > 1) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = tempHolidayMode, onCheckedChange = { tempHolidayMode = it })
                            Text("¿Modo Vacaciones?\n(Dosificador cerrado)", fontSize = 14.sp)
                        }
                    }

                    if (showEducation && tempTabletQty > 1) {
                        Surface(
                            color = Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text("💡 CONSEJO PRO", fontWeight = FontWeight.Bold, color = Color(0xFFE65100), fontSize = 12.sp)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Poner varias pastillas en el skimmer NO hace que duren más, solo suben el nivel de cloro a niveles peligrosos. Úsalas solo para vacaciones cerrando las rejillas del dosificador.",
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        tabletResultSummary = viewModel.onTabletChanged(context, tempTabletQty, tempHolidayMode)
                        showTabletConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1))
                ) {
                    Text("CONFIRMAR", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTabletConfirm = false }) {
                    Text("CANCELAR", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    tabletResultSummary?.let { summary ->
        AlertDialog(
            onDismissRequest = { tabletResultSummary = null },
            title = { Text("¡Guardado!", fontWeight = FontWeight.Black, color = Color(0xFF4CAF50)) },
            text = { Text(summary) },
            confirmButton = {
                Button(onClick = { tabletResultSummary = null }) {
                    Text("ENTENDIDO")
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }
    
    if (showPumpInfo) {
        val flowRate = when (pool.pumpHp) {
            0.5 -> 10.0
            0.75 -> 13.0
            1.0 -> 16.0
            1.5 -> 22.0
            else -> 12.0
        }
        val cycles = when { 
            pool.isWinterMode -> 0.5 
            weather.temp < 20 -> 1.0 
            weather.temp < 26 -> 1.5 
            weather.temp < 30 -> 2.0 
            weather.temp < 34 -> 2.5 
            weather.temp < 40 -> 3.0
            else -> 3.5 
        }
        val hours = (pool.volumeM3 / flowRate) * cycles

        AlertDialog(
            onDismissRequest = { showPumpInfo = false },
            title = { Text("Lógica de Filtrado", fontWeight = FontWeight.Black) },
            text = {
                Column {
                    Text("Mi IA calcula el tiempo basándose en ingeniería hidráulica:")
                    Spacer(Modifier.height(12.dp))
                    Text("• Volumen: ${pool.volumeM3}m³", fontSize = 14.sp)
                    Text("• Motor (${pool.pumpHp} CV): Mueve aprox. ${flowRate.toInt()}m³/h", fontSize = 14.sp)
                    Text("• Clima (${weather.temp.toInt()}°C): Requiere $cycles vueltas de agua", fontSize = 14.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("Fórmula: (Volumen / Caudal) x Ciclos = ${String.format(java.util.Locale.getDefault(), "%.1f", hours)} horas", fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                }
            },
            confirmButton = { Button(onClick = { showPumpInfo = false }) { Text("ENTENDIDO") } },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    showPumpConfirm?.let { hp ->
        AlertDialog(
            onDismissRequest = { showPumpConfirm = null },
            title = { Text("Cambiar Potencia", fontWeight = FontWeight.Black) },
            text = { Text("¿Confirmas que quieres cambiar el motor a $hp CV?\n\nEsto recalculará inmediatamente las horas de filtrado necesarias para tu piscina.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updatePumpHp(hp)
                        showPumpConfirm = null
                    }
                ) {
                    Text("CONFIRMAR")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPumpConfirm = null }) {
                    Text("CANCELAR", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    if (showFilterWash) {
        FilterWashDialog(
            onDismiss = { showFilterWash = false },
            onComplete = { viewModel.completeFilterWash() }
        )
    }

    if (showWinterConfirm) {
        var selectedType by remember { mutableStateOf("BOYA") }
        AlertDialog(
            onDismissRequest = { showWinterConfirm = false },
            title = { Text("Registrar Invernador", fontWeight = FontWeight.Black) },
            text = {
                Column {
                    Text("¿Qué tipo de invernador has añadido?")
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        FilterChip(
                            selected = selectedType == "BOYA",
                            onClick = { selectedType = "BOYA" },
                            label = { Text("BOYA (2 meses)", fontSize = 11.sp, maxLines = 1) }
                        )
                        FilterChip(
                            selected = selectedType == "LIQUIDO",
                            onClick = { selectedType = "LIQUIDO" },
                            label = { Text("LÍQUIDO (3 m.)", fontSize = 11.sp, maxLines = 1) }
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = if(selectedType == "BOYA") 
                            "Ideal para mantenimiento automático. Recuerda perforar los tetones laterales." 
                            else "Requiere dilución previa. Protege el agua durante 90 días.",
                        fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))
                    
                    // Enlace al Robot promocionando su uso
                    OutlinedButton(
                        onClick = { 
                            showWinterConfirm = false
                            onOpenAssistant("explicame como funciona el producto ivernacion, diferencias entre boyas y liquidos, recomendaciones de uso y sitios para comprar") 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF0D47A1).copy(alpha = 0.3f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0D47A1))
                    ) {
                        Icon(Icons.Rounded.Android, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("¿Dudas? Que me lo explique Blue Bot", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.updateWinterProduct(selectedType)
                    showWinterConfirm = false
                }) { Text("REGISTRAR") }
            },
            dismissButton = { TextButton(onClick = { showWinterConfirm = false }) { Text("CANCELAR") } },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    if (showSettings) {
        SettingsDialog(
            poolId = poolId,
            cityName = viewModel.cityName.collectAsState().value,
            onDeleteAccount = { viewModel.deleteUserAccount { showSettings = false } },
            onSyncHelp = { helpContent = "Sincronización" to HelpContent.CLOUD_HELP },
            onJoinPool = { viewModel.joinPool(it) },
            onUpdateLocation = { viewModel.updateLocation(context) },
            onDismiss = { showSettings = false }
        )
    }

    if (showVolumeCalc) {
        VolumeCalculatorDialog(
            onDismiss = { showVolumeCalc = false },
            onResult = { result ->
                volumeToConfirm = result
                showVolumeCalc = false
            }
        )
    }

    if (viewModel.showSafetyDialog.value) {
        SafetyCheckDialog(
            onConfirm = { viewModel.confirmSafetyCheck(); viewModel.showSafetyDialog.value = false },
            onDismiss = { viewModel.showSafetyDialog.value = false }
        )
    }

    if (viewModel.showSuccessAnimation.value) {
        SuccessEffect { viewModel.showSuccessAnimation.value = false }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.p), 
            contentDescription = null, 
            modifier = Modifier.fillMaxSize(), 
            contentScale = ContentScale.Crop
        )
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.White.copy(0.4f), Color.Transparent, Color.Black.copy(0.4f)))))

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).systemBarsPadding(), 
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderSection(
                weather = weather, 
                viewModel = viewModel, 
                showHistoryHighlight = showHistoryHighlight, // Pasar el estado
                onBot = onOpenAssistant, 
                onHistory = onOpenHistory, 
                onSettings = { showSettings = true }
            )
            
            androidx.compose.animation.AnimatedVisibility(
                visible = hasSetVolume,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ModeSelector(
                        isWinter = pool.isWinterMode, 
                        lastSafety = pool.lastSafetyCheck,
                        onSafety = { viewModel.showSafetyDialog.value = true },
                        onModeChange = { viewModel.updatePoolData(pool.volumeM3.toString(), pool.currentPh.toString(), pool.currentChlorine.toString(), it) }
                    )

                    StatusIndicator(score = PoolCalculator.getPoolScore(pool))
                }
            }

            if (!hasSetVolume) {
                Spacer(Modifier.height(24.dp))
                Surface(
                    color = Color.Black.copy(alpha = 0.6f), // Fondo más oscuro para legibilidad
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Info, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Configura la capacidad para empezar",
                            color = Color.White,
                            fontSize = 15.sp, // Un poco más grande
                            fontWeight = FontWeight.Bold // Negrita para que destaque
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            Box(Modifier.weight(1f).fillMaxWidth(), Alignment.TopCenter) { 
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    InputsSection(
                        pool = pool, 
                        viewModel = viewModel, 
                        showShareHighlight = showShareHighlight,
                        hasChanges = hasChanges,
                        hasSetVolume = hasSetVolume,
                        onHelp = { t, c -> helpContent = t to c }, 
                        onCalcVolume = { showVolumeCalc = true }, 
                        onTabletChangeClick = { 
                            tempTabletQty = 1
                            tempHolidayMode = false
                            showTabletConfirm = true 
                        },
                        onWinterClick = { showWinterConfirm = true },
                        onSaveClick = { showSaveConfirm = true },
                        onShareClick = { viewModel.shareReport(context) },
                        onPumpHpClick = { showPumpConfirm = it }
                    )
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = hasSetVolume,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 }
            ) {
                ResultsSection(
                    pool = pool, 
                    weather = weather, 
                    onPumpInfo = { showPumpInfo = true },
                    onFilterClick = { showFilterWash = true }
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(stringResource(R.string.credits_author), color = Color.White.copy(0.6f), fontSize = 11.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun HeaderSection(
    weather: com.sagon.piscinasblue.logic.WeatherInfo, 
    viewModel: PoolViewModel, 
    showHistoryHighlight: Boolean, // Nuevo parámetro
    onBot: (String?) -> Unit, 
    onHistory: () -> Unit, 
    onSettings: () -> Unit
) {
    val context = LocalContext.current
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text(stringResource(R.string.app_name), fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFF0D47A1), letterSpacing = (-1).sp)
            Surface(color = Color(0xFF0D47A1).copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.CloudDone, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Nube activa", fontSize = 10.sp, color = Color(0xFF0D47A1), fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Rounded.Cloud, null, tint = Color(0xFF0D47A1), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("${weather.temp.toInt()}°C | ${weather.windSpeed.toInt()} km/h", fontSize = 13.sp, color = Color(0xFF0D47A1), fontWeight = FontWeight.Bold)
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            JuicyIconButton(icon = Icons.Rounded.GroupAdd, tint = Color(0xFFE91E63), pulse = true) { onSettings() }
            JuicyIconButton(Icons.Rounded.Android, Color(0xFF0D47A1), pulse = true) { viewModel.triggerHapticFeedback(context); onBot(null) }
            JuicyIconButton(
                icon = Icons.Rounded.History, 
                tint = if (showHistoryHighlight) Color(0xFF4CAF50) else Color(0xFF0D47A1), 
                superPulse = showHistoryHighlight 
            ) { 
                viewModel.triggerHapticFeedback(context); 
                onHistory() 
            }
            JuicyIconButton(Icons.Rounded.Settings, Color(0xFF0D47A1)) { viewModel.triggerHapticFeedback(context); onSettings() }
        }
    }
}

@Composable
fun JuicyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0xFF0D47A1),
    content: @Composable RowScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(
        if (isPressed) 0.92f else 1f,
        spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "press"
    )

    Button(
        onClick = { isPressed = true; onClick() },
        modifier = modifier.scale(animatedScale),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor)
    ) {
        content()
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

@Composable
fun JuicyIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector, 
    tint: Color = Color.White, 
    pulse: Boolean = false, 
    superPulse: Boolean = false,
    iconSize: androidx.compose.ui.unit.Dp = 24.dp, 
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    
    val targetScale = when {
        superPulse -> 1.4f
        pulse -> 1.25f
        else -> 1f
    }
    
    val duration = if (superPulse) 800 else 1200

    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 1f, 
        targetValue = targetScale, 
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = FastOutSlowInEasing), 
            repeatMode = RepeatMode.Reverse
        ), 
        label = "scale"
    )
    
    var isPressed by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(if (isPressed) 0.85f else 1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow), label = "press")

    Box(modifier = Modifier.padding(1.dp).size(38.dp).scale(if (pulse || superPulse) scalePulse else animatedScale).background(tint.copy(alpha = 0.08f), CircleShape).clip(CircleShape).clickable { isPressed = true; onClick() }, contentAlignment = Alignment.Center) {
        if (pulse || superPulse) {
            val auraAlpha = if (superPulse) 0.25f else 0.15f
            Box(modifier = Modifier.fillMaxSize().background(tint.copy(alpha = auraAlpha * (1f - (scalePulse - 1f) * 4f)), CircleShape))
        }
        Icon(icon, null, tint = tint, modifier = Modifier.size(iconSize))
    }
    LaunchedEffect(isPressed) { if (isPressed) { kotlinx.coroutines.delay(150); isPressed = false } }
}

@Composable
private fun ModeSelector(isWinter: Boolean, lastSafety: Long, onSafety: () -> Unit, onModeChange: (Boolean) -> Unit) {
    val summerColor = Color(0xFFFFC107)
    val winterColor = Color(0xFF2196F3)
    val currentColor by animateColorAsState(if (isWinter) winterColor else summerColor, label = "mode")
    
    val isSafe = (System.currentTimeMillis() - lastSafety) < (15L * 24 * 60 * 60 * 1000)

    val infiniteTransition = rememberInfiniteTransition(label = "safety")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blink"
    )

    Surface(color = Color.Black.copy(alpha = 0.05f), shape = RoundedCornerShape(50.dp), modifier = Modifier.padding(vertical = 12.dp)) {
        Row(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.mode_summer), color = if (!isWinter) currentColor else Color.Gray, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            Switch(checked = isWinter, onCheckedChange = onModeChange, Modifier.padding(horizontal = 12.dp), colors = SwitchDefaults.colors(checkedThumbColor = winterColor, uncheckedThumbColor = summerColor))
            Text(stringResource(R.string.mode_winter), color = if (isWinter) currentColor else Color.Gray, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            
            Spacer(Modifier.width(16.dp))
            VerticalDivider(modifier = Modifier.height(20.dp), color = Color.Gray.copy(alpha = 0.3f))
            Spacer(Modifier.width(16.dp))
            
            IconButton(onClick = onSafety, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.Rounded.Shield,
                    contentDescription = "Seguridad",
                    tint = if (isSafe) Color(0xFF0D47A1) else Color(0xFFFF9800).copy(alpha = alpha),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun InputsSection(
    pool: com.sagon.piscinasblue.data.PoolData, 
    viewModel: PoolViewModel, 
    showShareHighlight: Boolean,
    hasChanges: Boolean,
    hasSetVolume: Boolean,
    onHelp: (String, String) -> Unit, 
    onCalcVolume: () -> Unit, 
    onTabletChangeClick: () -> Unit,
    onWinterClick: () -> Unit,
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit,
    onPumpHpClick: (Double) -> Unit
) {
    val phHelpTitle = stringResource(R.string.help_title_ph)
    val clHelpTitle = stringResource(R.string.help_title_chlorine)
    val capLabel = stringResource(R.string.label_volume)

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1.3f)) {
                PoolInputField(
                    label = capLabel, 
                    value = pool.volumeM3.toString(), 
                    icon = Icons.Rounded.WaterDrop, 
                    accentColor = Color(0xFF2196F3),
                    isBlinking = !hasSetVolume,
                    readOnly = hasSetVolume,
                    blinkColor = Color(0xFF4CAF50), 
                    onValueChange = { viewModel.updatePoolData(it, pool.currentPh.toString(), pool.currentChlorine.toString(), pool.isWinterMode) }, 
                    onHelpClick = onCalcVolume
                )
            }
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Motor (CV)", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().height(48.dp).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)).padding(2.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(0.5, 0.75, 1.0, 1.5).forEach { hp ->
                        val isSelected = pool.pumpHp == hp
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color.White else Color.Transparent)
                                .clickable { onPumpHpClick(hp) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = hp.toString(),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                color = if (isSelected) Color(0xFF0D47A1) else Color.White
                            )
                        }
                    }
                }
            }
        }
        
        androidx.compose.animation.AnimatedVisibility(
            visible = hasSetVolume,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column {
                if (!pool.isWinterMode) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.weight(1f)) {
                            val phIdeal = pool.currentPh in 7.2..7.6
                            PoolInputField(
                                label = stringResource(R.string.label_ph), 
                                value = pool.currentPh.toString(), 
                                icon = Icons.Rounded.Science, 
                                accentColor = Color(0xFFFF9800), // Naranja para pH
                                valueColor = if (phIdeal) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                                onValueChange = { viewModel.updatePoolData(pool.volumeM3.toString(), it, pool.currentChlorine.toString(), pool.isWinterMode) }, 
                                onIncrement = { viewModel.incrementPh() },
                                onDecrement = { viewModel.decrementPh() },
                                onHelpClick = { onHelp(phHelpTitle, HelpContent.PH_HELP) }
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            val clIdeal = pool.currentChlorine in 1.0..1.5
                            PoolInputField(
                                label = stringResource(R.string.label_chlorine), 
                                value = pool.currentChlorine.toString(), 
                                icon = Icons.Rounded.Opacity, 
                                accentColor = Color(0xFF00BCD4), // Cian para Cloro
                                valueColor = if (clIdeal) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                                onValueChange = { viewModel.updatePoolData(pool.volumeM3.toString(), pool.currentPh.toString(), it, pool.isWinterMode) }, 
                                onIncrement = { viewModel.incrementCl() },
                                onDecrement = { viewModel.decrementCl() },
                                onHelpClick = { onHelp(clHelpTitle, HelpContent.CHLORINE_HELP) }
                            )
                        }
                    }

                    if (hasChanges) {
                        Spacer(Modifier.height(8.dp))
                        JuicyButton(
                            onClick = onSaveClick,
                            containerColor = Color(0xFF4CAF50), // Verde para grabar
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                        ) {
                            Icon(Icons.Rounded.Save, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("GRABAR MEDICIÓN", fontWeight = FontWeight.Black, fontSize = 13.sp)
                        }
                    }
                    
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        JuicyButton(
                            onClick = { 
                                com.sagon.piscinasblue.logic.SoundManager.playClick()
                                onTabletChangeClick() 
                            }, 
                            modifier = Modifier.weight(1f).height(48.dp), 
                            containerColor = Color(0xFF0D47A1).copy(alpha = 0.8f)
                        ) {
                            Icon(Icons.Rounded.Sync, null, Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.btn_tablet_changed), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(Modifier.width(8.dp))
                        
                        val isIdeal = PoolCalculator.getPoolScore(pool) == 100
                        JuicyIconButton(
                            icon = Icons.Rounded.Share, 
                            tint = Color(0xFF4CAF50), 
                            pulse = showShareHighlight && !isIdeal,
                            superPulse = showShareHighlight && isIdeal
                        ) {
                            onShareClick()
                        }
                    }
                } else {
                    Surface(color = Color.White.copy(alpha = 0.1f), shape = RoundedCornerShape(24.dp), modifier = Modifier.padding(top = 10.dp).fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.AcUnit, null, tint = Color(0xFF0D47A1), modifier = Modifier.size(40.dp))
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text("MODO INVIERNO", color = Color(0xFF0D47A1), fontWeight = FontWeight.Black, fontSize = 18.sp)
                                    Text("Mantenimiento reducido activo", color = Color(0xFF0D47A1).copy(alpha = 0.7f), fontSize = 12.sp)
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                JuicyButton(
                                    onClick = onWinterClick,
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    containerColor = Color(0xFF1976D2)
                                ) {
                                    Icon(Icons.Rounded.AddModerator, null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("REGISTRAR INVERNADOR", fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.width(8.dp))
                                JuicyIconButton(icon = Icons.Rounded.Share, tint = Color(0xFF4CAF50), pulse = showShareHighlight) {
                                    onShareClick()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultsSection(
    pool: com.sagon.piscinasblue.data.PoolData, 
    weather: com.sagon.piscinasblue.logic.WeatherInfo, 
    onPumpInfo: () -> Unit,
    onFilterClick: () -> Unit
) {
    Surface(Modifier.fillMaxWidth(), RoundedCornerShape(20.dp), Color.White.copy(0.97f), shadowElevation = 6.dp) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
            if (!pool.isWinterMode) {
                val phKey = PoolCalculator.getPhStatusKey(pool.currentPh)
                val phStatus = when(phKey) { "low" -> stringResource(R.string.status_low) "high" -> stringResource(R.string.status_high) else -> stringResource(R.string.status_ideal) }
                FinalResultRow(stringResource(R.string.result_ph_status), phStatus, if (phKey == "ideal") Color(0xFF2E7D32) else Color(0xFFD32F2F))
                
                val daysLifespan = PoolCalculator.calculateIntelligentTabletLifespan(pool, weather.temp, weather.windSpeed)
                val now = System.currentTimeMillis()
                val daysPassed = (now - pool.lastTabletChange) / (1000 * 60 * 60 * 24).toDouble()
                val isExpired = daysPassed >= daysLifespan
                
                FinalResultRow(
                    label = stringResource(R.string.result_tablet_duration),
                    value = if (isExpired) "¡CADUCADA!" else stringResource(R.string.unit_days, daysLifespan),
                    color = if (isExpired) Color(0xFFD32F2F) else Color(0xFF1976D2),
                    isBlinking = isExpired
                )
                
                // Estado del Filtro
                val filterDays = (now - pool.lastFilterWash) / (1000 * 60 * 60 * 24).toDouble()
                val filterStatus = if (filterDays >= 15) "TOCA LAVADO" else "Limpio"
                Box(modifier = Modifier.clickable { onFilterClick() }) {
                    FinalResultRow(
                        label = "FILTRO ARENA:",
                        value = filterStatus,
                        color = if (filterDays >= 15) Color(0xFFE91E63) else Color(0xFF2E7D32),
                        isBlinking = filterDays >= 15
                    )
                }
            } else {
                // Resultados Modo Invierno
                val now = System.currentTimeMillis()
                val winterDays = (now - pool.lastWinterProductDate) / (1000 * 60 * 60 * 24).toDouble()
                val limit = if (pool.winterProductType == "BOYA") 60 else 90
                val remaining = (limit - winterDays).toInt().coerceAtLeast(0)
                
                FinalResultRow(
                    label = "PRODUCTO (${pool.winterProductType}):",
                    value = if (pool.lastWinterProductDate == 0L) "No registrado" else "$remaining días restantes",
                    color = if (remaining < 7) Color(0xFFD32F2F) else Color(0xFF1976D2),
                    isBlinking = remaining < 7 && pool.lastWinterProductDate != 0L
                )
                
                FinalResultRow(
                    label = "RECOMENDACIÓN:",
                    value = if (remaining < 7) "Reponer Producto" else "Solo Filtración",
                    color = if (remaining < 7) Color(0xFFD32F2F) else Color(0xFF2E7D32)
                )
            }
            HorizontalDivider(Modifier.padding(vertical = 2.dp), thickness = 1.dp, color = Color.LightGray.copy(0.4f))
            
            // Fila de depuradora
            Box(modifier = Modifier.clickable { onPumpInfo() }) {
                val pumpHours = PoolCalculator.calculateFilteringHours(pool, weather.temp).toInt()
                FinalResultRow(stringResource(R.string.result_pump_hours), stringResource(R.string.unit_hours_per_day, pumpHours), Color(0xFF0D47A1))
            }
        }
    }
}

@Composable
fun FinalResultRow(label: String, value: String, color: Color, isBlinking: Boolean = false) {
    val alpha by if (isBlinking) {
        rememberInfiniteTransition(label = "blink").animateFloat(
            initialValue = 0.4f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha"
        )
    } else {
        remember { mutableStateOf(1f) }
    }

    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Text(
            text = value,
            fontSize = 17.sp,
            fontWeight = FontWeight.Black,
            color = color.copy(alpha = alpha)
        )
    }
}

@Composable
fun SafetyCheckDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text(stringResource(R.string.safety_dialog_title), fontSize = 22.sp, fontWeight = FontWeight.Black) }, text = { Text(stringResource(R.string.safety_dialog_msg), fontSize = 18.sp) }, confirmButton = { Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)), shape = RoundedCornerShape(12.dp)) { Text(stringResource(R.string.btn_yes_ok), color = Color.White, fontSize = 16.sp) } }, dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.btn_no_later), color = Color.Gray, fontSize = 16.sp) } }, shape = RoundedCornerShape(30.dp), containerColor = Color.White)
}
