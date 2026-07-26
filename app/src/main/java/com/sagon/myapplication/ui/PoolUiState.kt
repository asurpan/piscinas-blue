package com.sagon.myapplication.ui

import com.sagon.myapplication.data.PoolData
import com.sagon.myapplication.logic.WeatherInfo

data class PoolUiState(
    val poolData: PoolData = PoolData(),
    val weather: WeatherInfo = WeatherInfo(25.0, 5.0, listOf(25.0, 25.0, 25.0), false),
    val isLoading: Boolean = false,
    val error: String? = null
)
