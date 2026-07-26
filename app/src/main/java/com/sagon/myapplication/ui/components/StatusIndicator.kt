package com.sagon.myapplication.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sagon.myapplication.R

@Composable
fun StatusIndicator(score: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "status")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (score >= 90) 1.15f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (score < 70) 5f else -5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    val emoji = when {
        score >= 90 -> "😍"
        score >= 70 -> "🤨"
        else -> "😵"
    }
    
    val message = when {
        score >= 90 -> stringResource(R.string.pool_perfect)
        score >= 70 -> stringResource(R.string.pool_almost)
        else -> stringResource(R.string.pool_needs_adjustment)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = emoji,
            fontSize = 48.sp,
            style = androidx.compose.ui.text.TextStyle(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.25f),
                    offset = Offset(0f, 8f),
                    blurRadius = 12f
                )
            ),
            modifier = Modifier
                .scale(scale)
                .offset(y = bounce.dp)
        )
        Text(
            text = message,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 2.dp)
        )
        Text(
            text = stringResource(R.string.score_label, score),
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 10.sp
        )
    }
}
