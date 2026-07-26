package com.sagon.piscinasblue.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuickAdjustInput(
    label: String,
    value: String,
    icon: ImageVector,
    step: Double = 0.1,
    onValueChange: (String) -> Unit
) {
    val currentVal = value.toDoubleOrNull() ?: 0.0

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(
            text = label,
            color = Color(0xFF0D47A1),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
        )
        
        Surface(
            color = Color.White.copy(alpha = 0.9f),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shadowElevation = 4.dp
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Icon(icon, null, tint = Color(0xFF1976D2), modifier = Modifier.size(24.dp))
                
                Spacer(Modifier.width(12.dp))
                
                Text(
                    text = value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0D47A1),
                    modifier = Modifier.weight(1f)
                )

                Row {
                    IconButton(
                        onClick = { onValueChange((currentVal - step).coerceAtLeast(0.0).format(1)) },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFE3F2FD))
                    ) {
                        Icon(Icons.Rounded.Remove, null, tint = Color(0xFF1976D2))
                    }
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = { onValueChange((currentVal + step).format(1)) },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF1976D2))
                    ) {
                        Icon(Icons.Rounded.Add, null, tint = Color.White)
                    }
                }
            }
        }
    }
}

private fun Double.format(digits: Int) = "%.${digits}f".format(java.util.Locale.US, this)
