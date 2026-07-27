package com.sagon.piscinasblue.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sagon.piscinasblue.logic.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PoolInputField(
    label: String,
    value: String,
    icon: ImageVector,
    accentColor: Color = Color(0xFF2196F3),
    valueColor: Color = Color(0xFF0D47A1),
    isBlinking: Boolean = false,
    readOnly: Boolean = false,
    blinkColor: Color = Color(0xFF4CAF50), 
    onValueChange: (String) -> Unit,
    onIncrement: (() -> Unit)? = null,
    onDecrement: (() -> Unit)? = null,
    onHelpClick: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val infiniteTransition = rememberInfiniteTransition(label = "blink")
    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp, bottom = 1.dp)
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .shadow(
                    elevation = if (isBlinking) 12.dp else 4.dp,
                    shape = RoundedCornerShape(12.dp),
                    ambientColor = if (isBlinking) blinkColor else accentColor,
                    spotColor = if (isBlinking) blinkColor else accentColor
                )
                .then(if (isBlinking) Modifier.clickable { onHelpClick() } else Modifier), // Táctil total aquí para el ripple
            shape = RoundedCornerShape(12.dp),
            color = if (isBlinking) {
                // Parpadeo más agresivo entre blanco y verde
                Color.White.copy(alpha = 1f - (blinkAlpha * 0.3f))
            } else Color.White.copy(alpha = 0.95f),
            border = if (isBlinking) BorderStroke(2.dp, blinkColor.copy(alpha = blinkAlpha)) else BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
        ) {
            if (isBlinking) {
                // Capa de color verde vibrante que parpadea
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(blinkColor.copy(alpha = blinkAlpha * 0.3f))
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                // BOTÓN MENOS (Solo si se pasa callback)
                if (onDecrement != null) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        SoundManager.playClick()
                                        onDecrement()
                                        val job = scope.launch {
                                            delay(500)
                                            while (true) {
                                                onDecrement()
                                                delay(100)
                                            }
                                        }
                                        try {
                                            awaitRelease()
                                        } finally {
                                            job.cancel()
                                        }
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Remove, null, tint = accentColor, modifier = Modifier.size(20.dp))
                    }
                } else {
                    Spacer(Modifier.width(12.dp))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isBlinking) blinkColor else accentColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                    
                    val focusManager = LocalFocusManager.current
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        readOnly = readOnly,
                        modifier = Modifier
                            .width(85.dp)
                            .padding(horizontal = 2.dp)
                            .pointerInput(readOnly, isBlinking) {
                                // Si está parpadeando, no dejamos que el texto capture el toque, que lo coja el Surface
                                if (!readOnly && !isBlinking) {
                                    detectTapGestures { /* Foco normal solo si NO parpadea */ }
                                }
                            },
                        enabled = !isBlinking, // Desactivar interacción directa si parpadea para forzar el click del Surface
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = androidx.compose.ui.text.input.ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isBlinking) blinkColor else if (readOnly) valueColor.copy(alpha = 0.7f) else valueColor,
                            textAlign = TextAlign.Center
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(if (isBlinking || readOnly) Color.Transparent else accentColor)
                    )
                }

                // BOTÓN MÁS (Solo si se pasa callback)
                if (onIncrement != null) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        SoundManager.playClick()
                                        onIncrement()
                                        val job = scope.launch {
                                            delay(500)
                                            while (true) {
                                                onIncrement()
                                                delay(100)
                                            }
                                        }
                                        try {
                                            awaitRelease()
                                        } finally {
                                            job.cancel()
                                        }
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Add, null, tint = accentColor, modifier = Modifier.size(20.dp))
                    }
                }

                // BOTÓN AYUDA
                IconButton(
                    onClick = onHelpClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = "Ayuda",
                        tint = accentColor.copy(alpha = 0.3f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
