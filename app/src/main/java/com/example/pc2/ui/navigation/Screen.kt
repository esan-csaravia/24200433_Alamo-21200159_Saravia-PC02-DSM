package com.example.pc2.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Apod : Screen("apod", "Imágenes (APOD)", Icons.Filled.Star)
    object NeoWs : Screen("neows", "Asteroides (NeoWs)", Icons.Filled.DateRange)
}
