package com.sagon.piscinasblue.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun HelpDialog(
    title: String,
    content: String,
    onBotClick: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1976D2)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = content,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    color = Color.DarkGray
                )
                
                if (onBotClick != null) {
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onBotClick,
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

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                ) {
                    Text("Entendido", color = Color.White)
                }
            }
        }
    }
}
