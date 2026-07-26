package com.sagon.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sagon.myapplication.R
import com.sagon.myapplication.logic.HelpContent

@Composable
fun SettingsDialog(
    onDeleteAccount: () -> Unit,
    onDismiss: () -> Unit
) {
    var showLegalText by remember { mutableStateOf<String?>(null) }
    var showConfirmDelete by remember { mutableStateOf(false) }

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
                Text("AJUSTES Y LEGAL", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF0D47A1))
                Spacer(Modifier.height(24.dp))

                SettingsItem(Icons.Rounded.Gavel, "Aviso Legal") { showLegalText = HelpContent.LEGAL_NOTICE }
                SettingsItem(Icons.Rounded.PrivacyTip, "Privacidad") { showLegalText = HelpContent.LEGAL_NOTICE } // Usamos el mismo bloque o uno específico
                
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { showConfirmDelete = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Rounded.DeleteForever, null)
                    Spacer(Modifier.width(8.dp))
                    Text("BORRAR CUENTA Y DATOS", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                TextButton(onClick = onDismiss, modifier = Modifier.padding(top = 16.dp)) {
                    Text("CERRAR", color = Color.Gray)
                }
            }
        }
    }

    if (showLegalText != null) {
        HelpDialog(title = "Información Legal", content = showLegalText!!, onDismiss = { showLegalText = null })
    }

    if (showConfirmDelete) {
        AlertDialog(
            onDismissRequest = { showConfirmDelete = false },
            title = { Text("¿Estás seguro?") },
            text = { Text("Esta acción borrará permanentemente todos tus datos de la piscina tanto en el móvil como en la nube. No se puede deshacer.") },
            confirmButton = {
                Button(onClick = onDeleteAccount, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))) {
                    Text("SÍ, BORRAR TODO", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDelete = false }) { Text("CANCELAR") }
            }
        )
    }
}

@Composable
private fun SettingsItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Icon(icon, null, tint = Color(0xFF1976D2), modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Text(label, color = Color.DarkGray, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}
