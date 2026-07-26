package com.sagon.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun VolumeCalculatorDialog(
    onDismiss: () -> Unit,
    onResult: (Double) -> Unit
) {
    var length by remember { mutableStateOf("") }
    var width by remember { mutableStateOf("") }
    var avgDepth by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Rounded.Calculate, null, tint = Color(0xFF1976D2), modifier = Modifier.size(40.dp))
                Text("Calculadora de Capacidad", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF0D47A1))
                Spacer(Modifier.height(16.dp))

                CalculatorInput("Largo (m)", length) { length = it }
                CalculatorInput("Ancho (m)", width) { width = it }
                CalculatorInput("Fondo Medio (m)", avgDepth) { avgDepth = it }

                val l = length.toDoubleOrNull() ?: 0.0
                val w = width.toDoubleOrNull() ?: 0.0
                val d = avgDepth.toDoubleOrNull() ?: 0.0
                val result = l * w * d

                Spacer(Modifier.height(16.dp))
                Surface(
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "TOTAL: ${String.format("%.1f", result)} m³",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1976D2)
                    )
                }

                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { onResult(result) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                ) {
                    Text("GRABAR VALOR", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CalculatorInput(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}
