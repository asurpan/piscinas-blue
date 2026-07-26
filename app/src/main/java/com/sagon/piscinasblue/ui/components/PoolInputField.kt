package com.sagon.piscinasblue.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
    valueColor: Color = Color(0xFF0D47A1), // Color por defecto
    onValueChange: (String) -> Unit,
    onIncrement: (() -> Unit)? = null,
    onDecrement: (() -> Unit)? = null,
    onHelpClick: () -> Unit
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.padding(vertical = 2.dp)
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
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp),
                    ambientColor = accentColor,
                    spotColor = accentColor
                ),
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.95f)
        ) {
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
                    modifier = Modifier.weight(1f).combinedClickable(
                        onClick = { /* Permite abrir teclado */ },
                        onLongClick = onHelpClick
                    ),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                    
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier.width(60.dp).padding(horizontal = 4.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = valueColor,
                            textAlign = TextAlign.Center
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(accentColor)
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
