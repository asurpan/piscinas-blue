package com.sagon.myapplication.ui.screens

import androidx.compose.animation.animateColorAsState
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
import com.sagon.myapplication.R
import com.sagon.myapplication.logic.PoolCalculator
import com.sagon.myapplication.ui.PoolViewModel
import com.sagon.myapplication.ui.components.*
import com.sagon.myapplication.logic.HelpContent

@Composable
fun DashboardScreen(
    viewModel: PoolViewModel = viewModel(), 
    onOpenAssistant: () -> Unit,
    onOpenHistory: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val pool = uiState.poolData
    val weather = uiState.weather
    val context = LocalContext.current

    var helpContent by remember { mutableStateOf<Pair<String, String>?>(null) }
    var showSettings by remember { mutableStateOf(false) }
    var showVolumeCalc by remember { mutableStateOf(false) }

    // Diálogos
    helpContent?.let { (title, content) -> HelpDialog(title, content) { helpContent = null } }
    
    if (showSettings) {
        SettingsDialog(
            onDeleteAccount = { viewModel.deleteUserAccount { showSettings = false } },
            onDismiss = { showSettings = false }
        )
    }

    if (showVolumeCalc) {
        VolumeCalculatorDialog(
            onDismiss = { showVolumeCalc = false },
            onResult = { result ->
                viewModel.updatePoolData(result.toString(), pool.currentPh.toString(), pool.currentChlorine.toString(), pool.isWinterMode)
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
                onSafety = { viewModel.showSafetyDialog.value = true }, 
                onShare = { viewModel.shareReport(context) }, 
                onBot = onOpenAssistant, 
                onHistory = onOpenHistory, 
                onSettings = { showSettings = true }, 
                onHelp = { t, c -> helpContent = t to c },
                lastSafety = pool.lastSafetyCheck
            )
            
            ModeSelector(pool.isWinterMode) { viewModel.updatePoolData(pool.volumeM3.toString(), pool.currentPh.toString(), pool.currentChlorine.toString(), it) }

            StatusIndicator(score = PoolCalculator.getPoolScore(pool))

            Box(Modifier.weight(1f).fillMaxWidth(), Alignment.TopCenter) { // Alineado arriba para dejar sitio al teclado
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    InputsSection(pool, viewModel, { t, c -> helpContent = t to c }, { showVolumeCalc = true })
                }
            }

            ResultsSection(pool, weather)
            Spacer(Modifier.height(6.dp))
            Text(stringResource(R.string.credits_author), color = Color.White.copy(0.6f), fontSize = 11.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun HeaderSection(
    weather: com.sagon.myapplication.logic.WeatherInfo, 
    viewModel: PoolViewModel, 
    onSafety: () -> Unit, 
    onShare: () -> Unit, 
    onBot: () -> Unit, 
    onHistory: () -> Unit, 
    onSettings: () -> Unit, 
    onHelp: (String, String) -> Unit,
    lastSafety: Long
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
            val isSafe = (System.currentTimeMillis() - lastSafety) < (30L * 24 * 60 * 60 * 1000)
            
            JuicyIconButton(Icons.Rounded.Android, Color(0xFF0D47A1), pulse = true) { viewModel.triggerHapticFeedback(context); onBot() }
            JuicyIconButton(Icons.Rounded.CloudSync, Color(0xFF4CAF50)) { onHelp("Sincronización", HelpContent.CLOUD_HELP) }
            JuicyIconButton(Icons.Rounded.Shield, if (isSafe) Color(0xFF0D47A1) else Color(0xFFFF9800), !isSafe) { viewModel.triggerHapticFeedback(context); onSafety() }
            JuicyIconButton(Icons.Rounded.History, Color(0xFF0D47A1)) { viewModel.triggerHapticFeedback(context); onHistory() }
            JuicyIconButton(Icons.Rounded.Settings, Color(0xFF0D47A1)) { viewModel.triggerHapticFeedback(context); onSettings() }
        }
    }
}

@Composable
fun JuicyIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color = Color.White, pulse: Boolean = false, iconSize: androidx.compose.ui.unit.Dp = 24.dp, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scalePulse by infiniteTransition.animateFloat(1f, if (pulse) 1.25f else 1f, infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "scale")
    var isPressed by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(if (isPressed) 0.85f else 1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow), label = "press")

    Box(modifier = Modifier.padding(1.dp).size(38.dp).scale(if (pulse) scalePulse else animatedScale).background(tint.copy(alpha = 0.08f), CircleShape).clip(CircleShape).clickable { isPressed = true; onClick() }, contentAlignment = Alignment.Center) {
        if (pulse) Box(modifier = Modifier.fillMaxSize().background(tint.copy(alpha = 0.15f * (1f - (scalePulse - 1f) * 4f)), CircleShape))
        Icon(icon, null, tint = tint, modifier = Modifier.size(iconSize))
    }
    LaunchedEffect(isPressed) { if (isPressed) { kotlinx.coroutines.delay(150); isPressed = false } }
}

@Composable
private fun ModeSelector(isWinter: Boolean, onModeChange: (Boolean) -> Unit) {
    val summerColor = Color(0xFFFFC107)
    val winterColor = Color(0xFF2196F3)
    val currentColor by animateColorAsState(if (isWinter) winterColor else summerColor, label = "mode")

    Surface(color = Color.Black.copy(alpha = 0.05f), shape = RoundedCornerShape(50.dp), modifier = Modifier.padding(vertical = 12.dp)) {
        Row(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.mode_summer), color = if (!isWinter) currentColor else Color.Gray, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            Switch(checked = isWinter, onCheckedChange = onModeChange, Modifier.padding(horizontal = 16.dp), colors = SwitchDefaults.colors(checkedThumbColor = winterColor, uncheckedThumbColor = summerColor))
            Text(stringResource(R.string.mode_winter), color = if (isWinter) currentColor else Color.Gray, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
        }
    }
}

@Composable
private fun InputsSection(pool: com.sagon.myapplication.data.PoolData, viewModel: PoolViewModel, onHelp: (String, String) -> Unit, onCalcVolume: () -> Unit) {
    val context = LocalContext.current
    val phHelpTitle = stringResource(R.string.help_title_ph)
    val clHelpTitle = stringResource(R.string.help_title_chlorine)
    val capLabel = stringResource(R.string.label_volume)

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        PoolInputField(capLabel, pool.volumeM3.toString(), Icons.Rounded.WaterDrop, { viewModel.updatePoolData(it, pool.currentPh.toString(), pool.currentChlorine.toString(), pool.isWinterMode) }, onCalcVolume)
        if (!pool.isWinterMode) {
            PoolInputField(stringResource(R.string.label_ph), pool.currentPh.toString(), Icons.Rounded.Science, { viewModel.updatePoolData(pool.volumeM3.toString(), it, pool.currentChlorine.toString(), pool.isWinterMode) }, { onHelp(phHelpTitle, HelpContent.PH_HELP) })
            PoolInputField(stringResource(R.string.label_chlorine), pool.currentChlorine.toString(), Icons.Rounded.Opacity, { viewModel.updatePoolData(pool.volumeM3.toString(), pool.currentPh.toString(), it, pool.isWinterMode) }, { onHelp(clHelpTitle, HelpContent.CHLORINE_HELP) })
            Button(onClick = { viewModel.onTabletChanged(context) }, Modifier.fillMaxWidth().padding(top = 12.dp).height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1).copy(alpha = 0.8f)), shape = RoundedCornerShape(16.dp)) {
                Icon(Icons.Rounded.Sync, null, Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.btn_tablet_changed), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        } else {
            Surface(color = Color.White.copy(alpha = 0.1f), shape = RoundedCornerShape(24.dp), modifier = Modifier.padding(top = 10.dp).fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Icon(Icons.Rounded.AcUnit, null, tint = Color(0xFF0D47A1), modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("PROTECCIÓN INVERNAL", color = Color(0xFF0D47A1), fontWeight = FontWeight.Black, fontSize = 20.sp, textAlign = TextAlign.Center)
                    Text("Cálculos optimizados para hibernación", color = Color(0xFF0D47A1).copy(alpha = 0.7f), fontSize = 14.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
private fun ResultsSection(pool: com.sagon.myapplication.data.PoolData, weather: com.sagon.myapplication.logic.WeatherInfo) {
    Surface(Modifier.fillMaxWidth(), RoundedCornerShape(20.dp), Color.White.copy(0.97f), shadowElevation = 6.dp) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
            if (!pool.isWinterMode) {
                val phKey = PoolCalculator.getPhStatusKey(pool.currentPh)
                val phStatus = when(phKey) { "low" -> stringResource(R.string.status_low) "high" -> stringResource(R.string.status_high) else -> stringResource(R.string.status_ideal) }
                FinalResultRow(stringResource(R.string.result_ph_status), phStatus, if (phKey == "ideal") Color(0xFF2E7D32) else Color(0xFFD32F2F))
                val days = PoolCalculator.calculateIntelligentTabletLifespan(pool, weather.temp, weather.windSpeed)
                FinalResultRow(stringResource(R.string.result_tablet_duration), stringResource(R.string.unit_days, days), Color(0xFF1976D2))
            } else {
                FinalResultRow(stringResource(R.string.result_winter_product), stringResource(R.string.unit_liters, PoolCalculator.calculateWinterProduct(pool.volumeM3)), Color(0xFF1976D2))
            }
            HorizontalDivider(Modifier.padding(vertical = 2.dp), thickness = 1.dp, color = Color.LightGray.copy(0.4f))
            val pumpHours = PoolCalculator.calculateFilteringHours(pool, weather.temp).toInt()
            FinalResultRow(stringResource(R.string.result_pump_hours), stringResource(R.string.unit_hours_per_day, pumpHours), Color(0xFF0D47A1))
        }
    }
}

@Composable
fun FinalResultRow(label: String, value: String, color: Color) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Text(value, fontSize = 17.sp, fontWeight = FontWeight.Black, color = color)
    }
}

@Composable
fun SafetyCheckDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text(stringResource(R.string.safety_dialog_title), fontSize = 22.sp, fontWeight = FontWeight.Black) }, text = { Text(stringResource(R.string.safety_dialog_msg), fontSize = 18.sp) }, confirmButton = { Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)), shape = RoundedCornerShape(12.dp)) { Text(stringResource(R.string.btn_yes_ok), color = Color.White, fontSize = 16.sp) } }, dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.btn_no_later), color = Color.Gray, fontSize = 16.sp) } }, shape = RoundedCornerShape(30.dp), containerColor = Color.White)
}
