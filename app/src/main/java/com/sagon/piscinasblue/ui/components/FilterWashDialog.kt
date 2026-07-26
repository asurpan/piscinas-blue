package com.sagon.piscinasblue.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay

@Composable
fun FilterWashDialog(
    onDismiss: () -> Unit,
    onComplete: () -> Unit
) {
    var step by remember { mutableStateOf(1) } // 1: Intro, 2: Lavado, 3: Enjuague, 4: Fin
    var timerSeconds by remember { mutableStateOf(0) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var showCancelConfirm by remember { mutableStateOf(false) }

    if (showCancelConfirm) {
        AlertDialog(
            onDismissRequest = { showCancelConfirm = false },
            title = { Text("¿Deseas cancelar?", fontWeight = FontWeight.Black) },
            text = { Text("Si sales ahora, no se registrará la limpieza del filtro en el historial.") },
            confirmButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("SÍ, SALIR", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelConfirm = false }) {
                    Text("CONTINUAR", color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (timerSeconds > 0) {
                delay(1000)
                timerSeconds--
            }
            isTimerRunning = false
            com.sagon.piscinasblue.logic.SoundManager.playClick()
        }
    }

    Dialog(onDismissRequest = if (isTimerRunning) ({}) else onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = when(step) {
                        1 -> Icons.Rounded.Info
                        2 -> Icons.Rounded.Waves
                        3 -> Icons.Rounded.DoneAll
                        else -> Icons.Rounded.CheckCircle
                    },
                    contentDescription = null,
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(48.dp)
                )
                
                Text(
                    text = when(step) {
                        1 -> "Asistente de Lavado"
                        2 -> "PASO 1: Lavado"
                        3 -> "PASO 2: Enjuague"
                        else -> "¡Filtro Limpio!"
                    },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0D47A1)
                )

                Spacer(Modifier.height(16.dp))

                Box(modifier = Modifier.minifyHeight(120.dp), contentAlignment = Alignment.Center) {
                    when (step) {
                        1 -> Text(
                            "Este proceso eliminará la suciedad acumulada en la arena del filtro.\n\n¿Empezamos?",
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp
                        )
                        2 -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Mueve la palanca a 'LAVADO' (Backwash).\n\nAquí saldrá la suciedad acumulada. Observa el visor hasta que el agua salga clara.",
                                textAlign = TextAlign.Center,
                                fontSize = 15.sp
                            )
                            if (timerSeconds > 0) {
                                Text(
                                    text = "${timerSeconds}s",
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF1976D2),
                                    modifier = Modifier.padding(top = 12.dp)
                                )
                            }
                        }
                        3 -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Mueve la palanca a 'ENJUAGUE' (Rinse).\n\nAunque el agua esté clara, este paso es vital para asentar la arena antes de filtrar.",
                                textAlign = TextAlign.Center,
                                fontSize = 15.sp
                            )
                            if (timerSeconds > 0) {
                                Text(
                                    text = "${timerSeconds}s",
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF4CAF50),
                                    modifier = Modifier.padding(top = 12.dp)
                                )
                            }
                        }
                        else -> Text(
                            "¡Buen trabajo! Tu filtro está listo para otra jornada.\n\nRecuerda volver a poner la palanca en 'FILTRACIÓN'.",
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                if (!isTimerRunning) {
                    Button(
                        onClick = {
                            when (step) {
                                1 -> { step = 2; timerSeconds = 120 } // Ir a lavado
                                2 -> {
                                    if (timerSeconds > 0) isTimerRunning = true
                                    else { step = 3; timerSeconds = 30 } // Ir a enjuague
                                }
                                3 -> {
                                    if (timerSeconds > 0) isTimerRunning = true
                                    else step = 4 // Ir a fin
                                }
                                4 -> { onComplete(); onDismiss() }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = when(step) {
                                1 -> "COMENZAR"
                                2 -> if (timerSeconds > 0) "INICIAR CRONÓMETRO" else "IR A ENJUAGUE"
                                3 -> if (timerSeconds > 0) "INICIAR CRONÓMETRO" else "FINALIZAR"
                                else -> "CERRAR"
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    if (step < 4) {
                        TextButton(
                            onClick = { showCancelConfirm = true },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("CANCELAR", color = Color.Gray)
                        }
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFF1976D2))
                        Text("Esperando...", modifier = Modifier.padding(top = 8.dp), color = Color.Gray)
                        
                        Spacer(Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { 
                                    timerSeconds = 0
                                    isTimerRunning = false 
                                },
                                modifier = Modifier.weight(1.2f),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Text(
                                    text = "SALTAR TIEMPO", 
                                    color = Color(0xFF1976D2), 
                                    fontWeight = FontWeight.Bold, 
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                            Spacer(Modifier.width(4.dp))
                            TextButton(
                                onClick = { showCancelConfirm = true },
                                modifier = Modifier.weight(0.8f),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Text(
                                    text = "ABORTAR", 
                                    color = Color.Red.copy(alpha = 0.7f), 
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun Modifier.minifyHeight(dp: androidx.compose.ui.unit.Dp) = this.defaultMinSize(minHeight = dp)
