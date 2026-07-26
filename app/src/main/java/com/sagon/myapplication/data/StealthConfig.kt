package com.sagon.myapplication.data

data class StealthConfig(
    val isEnabled: Boolean = false,
    val priceText: String = "",
    val bizumText: String = "",
    val instructions: String = "",
    val activationCode: String = "121212"
)
