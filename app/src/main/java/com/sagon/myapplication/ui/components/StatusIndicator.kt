package com.sagon.myapplication.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sagon.myapplication.R

@Composable
fun StatusIndicator(score: Int) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
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
        modifier = Modifier.padding(12.dp)
    ) {
        Text(
            text = emoji,
            fontSize = 70.sp,
            modifier = Modifier.scale(if (score >= 90) scale else 1f)
        )
        Text(
            text = message,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = stringResource(R.string.score_label, score),
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp
        )
    }
}
