package com.sagon.piscinasblue.ui.components

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sagon.piscinasblue.R
import com.sagon.piscinasblue.logic.HelpContent

@Composable
fun SettingsDialog(
    poolId: String,
    cityName: String,
    onDeleteAccount: () -> Unit,
    onSyncHelp: () -> Unit,
    onJoinPool: (String) -> Unit,
    onUpdateLocation: () -> Unit,
    onDismiss: () -> Unit
) {
    var showLegalText by remember { mutableStateOf<String?>(null) }
    var showConfirmDelete by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }
    
    val context = androidx.compose.ui.platform.LocalContext.current

    if (showJoinDialog) {
        var newId by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showJoinDialog = false },
            title = { Text("Unirse a Piscina Shared", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Introduce el ID de la piscina de tu familiar para sincronizar los datos.")
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = newId,
                        onValueChange = { newId = it.uppercase() },
                        label = { Text("ID de Piscina (ej: P-A1B2C3)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = { 
                    if (newId.isNotBlank()) {
                        onJoinPool(newId)
                        showJoinDialog = false
                    }
                }) { Text("UNIRSE") }
            },
            dismissButton = { TextButton(onClick = { showJoinDialog = false }) { Text("CANCELAR") } },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

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
                Spacer(Modifier.height(16.dp))

                // Panel de ID de Piscina
                Surface(
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("MI ID DE PISCINA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                        Text(poolId, fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color(0xFF0D47A1))
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(
                                onClick = { 
                                    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                    val clip = android.content.ClipData.newPlainText("Pool ID", poolId)
                                    clipboard.setPrimaryClip(clip)
                                    android.widget.Toast.makeText(context, "Copiado al portapapeles", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(Icons.Rounded.ContentCopy, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("COPIAR", fontSize = 11.sp)
                            }

                            Button(
                                onClick = { 
                                    val sendIntent: Intent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "¡Gestionemos juntos nuestra piscina! 🏊‍♂️🤝\n\nEste es el código para unirte en la App Piscinas Blue: *$poolId*")
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, null)
                                    context.startActivity(shareIntent)
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(Icons.Rounded.Share, null, modifier = Modifier.size(16.dp), tint = Color.White)
                                Spacer(Modifier.width(8.dp))
                                Text("ENVIAR ID", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                
                // Panel de Ubicación
                Surface(
                    color = Color(0xFFF1F8E9),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.LocationOn, null, tint = Color(0xFF388E3C))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("CIUDAD ACTUAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            Text(cityName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                        }
                        TextButton(onClick = onUpdateLocation) {
                            Text("ACTUALIZAR", fontSize = 11.sp, color = Color(0xFF388E3C))
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                
                SettingsItem(Icons.Rounded.GroupAdd, "Unirse a otra piscina") { showJoinDialog = true }
                SettingsItem(Icons.Rounded.CloudSync, "Sincronización en la Nube") { onSyncHelp() }
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
