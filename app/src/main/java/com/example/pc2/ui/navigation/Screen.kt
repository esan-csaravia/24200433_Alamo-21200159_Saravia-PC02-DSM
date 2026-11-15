package com.example.pc2.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Apod : Screen("apod", "Imágenes", Icons.Filled.Home)
    object NeoWs : Screen("neows_list", "Asteroides", Icons.Filled.Star)
    object NeoWsDetail : Screen("neows_detail/{asteroidId}", "Detalle Asteroide", Icons.Filled.Star) {
        fun createRoute(asteroidId: String) = "neows_detail/$asteroidId"
    }
    object NeoWsFavorites : Screen("neows_favorites", "Mis Asteroides", Icons.Filled.Star)
}
