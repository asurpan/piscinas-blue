package com.sagon.piscinasblue.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sagon.piscinasblue.R
import com.sagon.piscinasblue.logic.BlueBotManager
import com.sagon.piscinasblue.ui.PoolViewModel
import kotlinx.coroutines.launch

data class Message(
    val text: String, 
    val isBot: Boolean,
    val productToSearch: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantScreen(
    viewModel: PoolViewModel = viewModel(), 
    initialQuery: String? = null,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var text by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf(Message("¡Hola! Soy Blue Bot. ¿Qué le pasa a tu piscina?", true)) }
    var isListening by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    // Estado para el auto-scroll
    val listState = rememberLazyListState()

    // Auto-scroll al final cuando la lista de mensajes cambia
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LaunchedEffect(initialQuery) {
        if (initialQuery != null) {
            messages.add(Message(initialQuery, false))
            val response = BlueBotManager.getResponse(initialQuery, uiState.weather)
            messages.add(Message(response.text, true, response.productToSearch))
        }
    }

    // Función para iniciar la escucha de voz
    fun startVoice() {
        isListening = true
        viewModel.startVoiceInput { voiceText ->
            isListening = false
            if (voiceText.isNotBlank()) {
                val response = BlueBotManager.getResponse(voiceText, uiState.weather)
                messages.add(Message(voiceText, false))
                messages.add(Message(response.text, true, response.productToSearch))
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) startVoice()
        else {
            Toast.makeText(context, "Permiso de audio denegado", Toast.LENGTH_SHORT).show()
        }
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
                    title = { 
                        Column {
                            Text("Blue Bot - Experto", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            if (isListening) {
                                Text("Escuchando...", color = Color(0xFF4CAF50), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = Color.White) } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    state = listState // Asignar el estado para el scroll
                ) {
                    items(messages) { ChatBubble(it) }
                }
                
                ChatInput(
                    text = text, 
                    onValueChange = { text = it },
                    isListening = isListening,
                    onMicClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                            startVoice()
                        } else {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    onSend = {
                        if (text.isNotBlank()) {
                            val response = BlueBotManager.getResponse(text, uiState.weather)
                            messages.add(Message(text, false))
                            messages.add(Message(response.text, true, response.productToSearch))
                            text = ""
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ChatInput(text: String, onValueChange: (String) -> Unit, isListening: Boolean, onMicClick: () -> Unit, onSend: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        val micColor by animateColorAsState(
            if (isListening) Color(0xFFF44336) else Color.White.copy(alpha = 0.2f),
            label = "micColor"
        )
        
        IconButton(
            onClick = onMicClick,
            colors = IconButtonDefaults.iconButtonColors(containerColor = micColor),
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Mic, 
                contentDescription = null, 
                tint = Color.White, 
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        TextField(
            value = text, onValueChange = onValueChange, placeholder = { Text("Escribe aquí...", color = Color.Gray, fontSize = 18.sp) },
            modifier = Modifier.weight(1f), shape = RoundedCornerShape(28.dp),
            textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
            colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White.copy(0.9f), focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
        )
        Spacer(Modifier.width(12.dp))
        FloatingActionButton(onClick = onSend, containerColor = Color(0xFF2196F3), contentColor = Color.White, shape = RoundedCornerShape(28.dp), modifier = Modifier.size(56.dp)) {
            Icon(Icons.AutoMirrored.Rounded.Send, null, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
private fun ChatBubble(message: Message) {
    val alignment = if (message.isBot) Alignment.Start else Alignment.End
    val color = if (message.isBot) Color.White.copy(0.9f) else Color(0xFFBBDEFB)
    val textColor = if (message.isBot) Color.DarkGray else Color(0xFF0D47A1)
    val context = LocalContext.current

    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalAlignment = alignment) {
        Card(
            shape = RoundedCornerShape(
                topStart = 20.dp, 
                topEnd = 20.dp, 
                bottomStart = if (message.isBot) 0.dp else 20.dp, 
                bottomEnd = if (message.isBot) 20.dp else 0.dp
            ),
            colors = CardDefaults.cardColors(containerColor = color), 
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = message.text, 
                    fontSize = 17.sp, 
                    color = textColor, 
                    lineHeight = 24.sp
                )
                
                if (message.productToSearch != null) {
                    Spacer(Modifier.height(16.dp))
                    Text("COMPARAR PRECIOS EN DIRECTO:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(Modifier.height(8.dp))
                    
                    val encodedSearch = Uri.encode(message.productToSearch)
                    
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StoreButton(
                                label = "AMAZON",
                                color = Color(0xFFFF9800),
                                modifier = Modifier.weight(1f),
                                onClick = { openUrl(context, "https://www.amazon.es/s?k=$encodedSearch") }
                            )
                            StoreButton(
                                label = "LEROY",
                                color = Color(0xFF4CAF50),
                                modifier = Modifier.weight(1f),
                                onClick = { openUrl(context, "https://www.leroymerlin.es/buscar?q=$encodedSearch") }
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StoreButton(
                                label = "BRICO",
                                color = Color(0xFF2196F3),
                                modifier = Modifier.weight(1f),
                                onClick = { openUrl(context, "https://www.bricodepot.es/catalogsearch/result/?q=$encodedSearch") }
                            )
                            StoreButton(
                                label = "CARREFOUR",
                                color = Color(0xFFE91E63),
                                modifier = Modifier.weight(1f),
                                onClick = { openUrl(context, "https://www.carrefour.es/?q=$encodedSearch") }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StoreButton(label: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
        modifier = modifier.height(36.dp)
    ) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.White, textAlign = TextAlign.Center)
    }
}

private fun openUrl(context: android.content.Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No se pudo abrir el enlace", Toast.LENGTH_SHORT).show()
    }
}
