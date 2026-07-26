package com.sagon.piscinasblue.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun SuccessEffect(onFinished: () -> Unit) {
    val particles = remember { List(30) { Particle() } }
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
        )
        delay(500)
        onFinished()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val progress = animatable.value
            particles.forEach { particle ->
                val x = particle.startX + (particle.vx * progress * 500f)
                val y = particle.startY + (particle.vy * progress * 500f) + (0.5f * 9.8f * (progress * 20f) * (progress * 20f))
                
                translate(x, y) {
                    drawCircle(
                        color = particle.color.copy(alpha = 1f - progress),
                        radius = 10f
                    )
                }
            }
        }
    }
}

private class Particle {
    val startX = Random.nextFloat() * 1000f
    val startY = Random.nextFloat() * 2000f
    val vx = (Random.nextFloat() - 0.5f) * 2f
    val vy = (Random.nextFloat() - 1.0f) * 2f
    val color = listOf(Color.White, Color(0xFFBBDEFB), Color(0xFF2196F3)).random()
}
