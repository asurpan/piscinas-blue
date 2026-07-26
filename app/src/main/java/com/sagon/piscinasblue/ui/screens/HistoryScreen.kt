package com.sagon.piscinasblue.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sagon.piscinasblue.R
import com.sagon.piscinasblue.ui.PoolViewModel
import com.sagon.piscinasblue.data.local.MaintenanceLogEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: PoolViewModel = viewModel(), onBack: () -> Unit) {
    val logs by viewModel.maintenanceLogs.collectAsState(initial = emptyList())
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("¿Borrar Historial?", fontWeight = FontWeight.Black) },
            text = { Text("Se eliminarán todos los registros de mantenimiento de forma permanente.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearHistory()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("BORRAR TODO", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("CANCELAR", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.p), 
            contentDescription = null, 
            modifier = Modifier.fillMaxSize(), 
            contentScale = ContentScale.Crop
        )
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Black.copy(0.5f), Color.Transparent))))

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Historial de Mantenimiento", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = Color.White) } },
                    actions = {
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Rounded.Delete, null, tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            if (logs.isEmpty()) {
                Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay registros aún", color = Color.White.copy(alpha = 0.6f), fontSize = 18.sp)
                }
            } else {
                LazyColumn(Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
                    items(logs) { log ->
                        var showItemDelete by remember { mutableStateOf(false) }
                        
                        if (showItemDelete) {
                            AlertDialog(
                                onDismissRequest = { showItemDelete = false },
                                title = { Text("¿Borrar registro?", fontWeight = FontWeight.Bold) },
                                text = { Text("¿Quieres eliminar este registro del historial?") },
                                confirmButton = {
                                    TextButton(onClick = { 
                                        viewModel.deleteLogEntry(log)
                                        showItemDelete = false 
                                    }) {
                                        Text("BORRAR", color = Color.Red)
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showItemDelete = false }) { Text("CANCELAR") }
                                },
                                shape = RoundedCornerShape(20.dp),
                                containerColor = Color.White
                            )
                        }

                        LogCard(
                            log = log, 
                            onLongClick = { showItemDelete = true }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LogCard(log: MaintenanceLogEntity, onLongClick: () -> Unit) {
    val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(log.date))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .combinedClickable(
                onClick = { /* Opcional: mostrar detalle */ },
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(log.actionType, fontWeight = FontWeight.Black, color = Color(0xFF1976D2), fontSize = 14.sp)
                Text(dateStr, fontSize = 11.sp, color = Color.Gray)
            }
            Spacer(Modifier.height(4.dp))
            Text(log.description, fontSize = 17.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
            if (log.value.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text("Valor: ${log.value}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}
