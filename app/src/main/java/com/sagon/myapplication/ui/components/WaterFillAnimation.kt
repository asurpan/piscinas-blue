package com.sagon.myapplication.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
fun WaterFillAnimation(onAnimationComplete: () -> Unit) {
    val fillLevel = remember { Animatable(0f) }
    val waveOffset = rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    LaunchedEffect(Unit) {
        fillLevel.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3000, easing = FastOutSlowInEasing)
        )
        delay(500)
        onAnimationComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val currentHeight = height * (1f - fillLevel.value)

            val path = Path().apply {
                moveTo(0f, currentHeight)
                // Dibujar onda
                for (x in 0..width.toInt()) {
                    val y = currentHeight + sin(x * 0.02f + waveOffset.value) * 20f
                    lineTo(x.toFloat(), y)
                }
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(path, Color(0xFF2196F3))
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "PISCINAS",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = if (fillLevel.value > 0.6f) Color.White else Color(0xFF2196F3)
            )
            Text(
                text = "BLUE",
                fontSize = 32.sp,
                fontWeight = FontWeight.Light,
                color = if (fillLevel.value > 0.7f) Color.White else Color(0xFF2196F3)
            )
        }
    }
}
