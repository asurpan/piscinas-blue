package com.sagon.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Mic
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
import com.sagon.myapplication.R
import com.sagon.myapplication.logic.BlueBotManager
import com.sagon.myapplication.ui.PoolViewModel

data class Message(val text: String, val isBot: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantScreen(viewModel: PoolViewModel = viewModel(), onBack: () -> Unit) {
    var text by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf(Message("¡Hola! Soy Blue Bot. ¿Qué le pasa a tu piscina?", true)) }

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
                    title = { Text("Blue Bot - Experto", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp) },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = Color.White) } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
                LazyColumn(Modifier.weight(1f)) {
                    items(messages) { ChatBubble(it) }
                }
                
                ChatInput(
                    text = text, 
                    onValueChange = { text = it },
                    onMicClick = {
                        viewModel.startVoiceInput { voiceText ->
                            if (voiceText.isNotBlank()) {
                                messages.add(Message(voiceText, false))
                                messages.add(Message(BlueBotManager.getResponse(voiceText), true))
                            }
                        }
                    },
                    onSend = {
                        if (text.isNotBlank()) {
                            messages.add(Message(text, false))
                            messages.add(Message(BlueBotManager.getResponse(text), true))
                            text = ""
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ChatInput(text: String, onValueChange: (String) -> Unit, onMicClick: () -> Unit, onSend: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = onMicClick,
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White.copy(alpha = 0.2f)),
            modifier = Modifier.size(56.dp)
        ) {
            Icon(Icons.Rounded.Mic, null, tint = Color.White, modifier = Modifier.size(32.dp))
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

    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalAlignment = alignment) {
        Card(
            shape = RoundedCornerShape(
                topStart = 20.dp, 
                topEnd = 20.dp, 
                bottomStart = if (message.isBot) 0.dp else 20.dp, 
                bottomEnd = if (message.isBot) 20.dp else 0.dp
            ),
            colors = CardDefaults.cardColors(containerColor = color), 
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Text(
                text = message.text, 
                modifier = Modifier.padding(16.dp), 
                fontSize = 17.sp, 
                color = textColor, 
                lineHeight = 24.sp
            )
        }
    }
}
