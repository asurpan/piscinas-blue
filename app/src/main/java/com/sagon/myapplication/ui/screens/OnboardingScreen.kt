package com.sagon.myapplication.ui.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sagon.myapplication.R
import com.sagon.myapplication.logic.AuthManager
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var isSigningIn by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { onFinish() }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.p),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Black.copy(0.7f), Color.Transparent, Color.Black.copy(0.8f)))))

        Column(
            modifier = Modifier.fillMaxSize().systemBarsPadding().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Contenido Central
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                HorizontalPager(state = pagerState) { page -> OnboardingPage(page) }
            }

            // Pie de página (Indicadores y Botones)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Row(Modifier.padding(bottom = 24.dp)) {
                    repeat(4) { i ->
                        val isSelected = pagerState.currentPage == i
                        Box(
                            Modifier
                                .padding(4.dp)
                                .background(if (isSelected) Color.White else Color.White.copy(0.3f), CircleShape)
                                .size(if (isSelected) 10.dp else 6.dp)
                        )
                    }
                }

                if (pagerState.currentPage == 3) {
                    Button(
                        onClick = {
                            if (!isSigningIn) {
                                if (!AuthManager.isInternetAvailable(context)) {
                                    Toast.makeText(context, "⚠️ Verifica tu conexión a internet", Toast.LENGTH_LONG).show()
                                    return@Button
                                }
                                isSigningIn = true
                                scope.launch {
                                    try {
                                        if (AuthManager.signInWithGoogle(context)) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                            } else onFinish()
                                        } else {
                                            Toast.makeText(context, "Error al iniciar sesión con Google", Toast.LENGTH_LONG).show()
                                        }
                                    } finally {
                                        isSigningIn = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                        enabled = !isSigningIn
                    ) {
                        if (isSigningIn) {
                            CircularProgressIndicator(Modifier.size(24.dp), Color.Black, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Rounded.AccountCircle, null, tint = Color(0xFF4285F4))
                            Spacer(Modifier.width(10.dp))
                            Text("ENTRAR CON GOOGLE", fontWeight = FontWeight.Black, fontSize = 15.sp)
                        }
                    }
                } else {
                    Button(
                        onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                    ) {
                        Text("CONTINUAR", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingPage(page: Int) {
    val title = stringResource(when (page) {
        0 -> R.string.onboarding_title_1
        1 -> R.string.onboarding_title_2
        2 -> R.string.onboarding_title_3
        else -> R.string.onboarding_title_4
    })
    val desc = stringResource(when (page) {
        0 -> R.string.onboarding_desc_1
        1 -> R.string.onboarding_desc_2
        2 -> R.string.onboarding_desc_3
        else -> R.string.onboarding_desc_4
    })
    val icon = when (page) {
        0 -> Icons.Rounded.Cloud
        1 -> Icons.Rounded.SmartToy
        2 -> Icons.Rounded.Shield
        else -> Icons.Rounded.CloudDone
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape, 
            color = Color.White.copy(alpha = 0.15f), 
            modifier = Modifier.size(80.dp) // Icono más compacto
        ) {
            Icon(
                imageVector = icon, 
                contentDescription = null, 
                tint = Color.White, 
                modifier = Modifier.padding(20.dp).fillMaxSize()
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = title, 
            color = Color.White, 
            fontSize = 24.sp, 
            fontWeight = FontWeight.Black, 
            textAlign = TextAlign.Center, 
            lineHeight = 28.sp
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = desc, 
            color = Color.White.copy(alpha = 0.85f), 
            fontSize = 16.sp, 
            textAlign = TextAlign.Center, 
            lineHeight = 22.sp
        )
    }
}
