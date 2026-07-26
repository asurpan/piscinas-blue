package com.sagon.piscinasblue

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.sagon.piscinasblue.logic.MaintenanceWorker
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sagon.piscinasblue.ui.PoolViewModel
import com.sagon.piscinasblue.ui.components.ExitDialog
import com.sagon.piscinasblue.ui.components.WaterFillAnimation
import com.sagon.piscinasblue.ui.screens.*
import com.sagon.piscinasblue.ui.theme.MyApplicationTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupMaintenanceReminders()

        setContent {
            MyApplicationTheme {
                val viewModel: PoolViewModel = viewModel()
                val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsState()
                val usageCount by viewModel.usageCount.collectAsState()
                val isActivated by viewModel.isActivated.collectAsState()
                val stealthConfig by viewModel.stealthConfig.collectAsState()
                
                var currentScreen by rememberSaveable { mutableStateOf("splash") }
                var showExitDialog by remember { mutableStateOf(false) }
                var initialBotQuery by rememberSaveable { mutableStateOf<String?>(null) }

                val limitReached = usageCount >= 30 && !isActivated && stealthConfig.isEnabled

                BackHandler { 
                    if (currentScreen != "main") currentScreen = "main" else showExitDialog = true 
                }

                if (showExitDialog) {
                    ExitDialog(onConfirm = { finish() }, onDismiss = { showExitDialog = false })
                }

                when (currentScreen) {
                    "splash" -> WaterFillAnimation { 
                        currentScreen = if (isOnboardingCompleted) {
                            if (limitReached) "activation" else "main"
                        } else "onboarding" 
                    }
                    "onboarding" -> OnboardingScreen {
                        viewModel.completeOnboarding()
                        currentScreen = if (limitReached) "activation" else "main"
                    }
                    "activation" -> ActivationScreen(stealthConfig) { code ->
                        if (viewModel.activateApp(code)) currentScreen = "main"
                    }
                    "main" -> DashboardScreen(
                        onOpenAssistant = { query -> 
                            initialBotQuery = query
                            currentScreen = "assistant" 
                        },
                        onOpenHistory = { currentScreen = "history" }
                    )
                    "assistant" -> {
                        AssistantScreen(
                            initialQuery = initialBotQuery,
                            onBack = { 
                                initialBotQuery = null
                                currentScreen = "main" 
                            }
                        )
                    }
                    "history" -> HistoryScreen { currentScreen = "main" }
                }
            }
        }
    }

    private fun setupMaintenanceReminders() {
        val request = PeriodicWorkRequestBuilder<MaintenanceWorker>(7, TimeUnit.DAYS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "pool_maintenance",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
