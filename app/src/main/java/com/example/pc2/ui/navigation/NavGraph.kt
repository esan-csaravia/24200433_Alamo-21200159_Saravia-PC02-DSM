package com.example.pc2.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pc2.ui.screens.ApodPlaceholderScreen
import com.example.pc2.ui.screens.NeoWsPlaceholderScreen

@Composable
fun NavGraph(navController: NavHostController, innerPadding: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = Screen.Apod.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(Screen.Apod.route) {
            ApodPlaceholderScreen()
        }
        composable(Screen.NeoWs.route) {
            NeoWsPlaceholderScreen()
        }
    }
}
