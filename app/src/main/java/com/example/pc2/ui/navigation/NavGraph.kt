package com.example.pc2.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pc2.ui.neows.AsteroidDetailScreen
import com.example.pc2.ui.neows.AsteroidListScreen
import com.example.pc2.ui.neows.FavoritesScreen
import com.example.pc2.ui.neows.NeoWsViewModel
import com.example.pc2.ui.screens.ApodPlaceholderScreen

@Composable
fun NavGraph(navController: NavHostController, innerPadding: PaddingValues) {
    val neoWsViewModel: NeoWsViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Apod.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(Screen.Apod.route) {
            ApodPlaceholderScreen()
        }
        composable(Screen.NeoWs.route) {
            AsteroidListScreen(
                viewModel = neoWsViewModel,
                onNavigateToDetail = { asteroidId ->
                    navController.navigate(Screen.NeoWsDetail.createRoute(asteroidId))
                },
                onNavigateToFavorites = {
                    navController.navigate(Screen.NeoWsFavorites.route)
                }
            )
        }
        composable(
            route = Screen.NeoWsDetail.route,
            arguments = listOf(navArgument("asteroidId") { type = NavType.StringType })
        ) { backStackEntry ->
            val asteroidId = backStackEntry.arguments?.getString("asteroidId") ?: ""
            AsteroidDetailScreen(
                asteroidId = asteroidId,
                viewModel = neoWsViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.NeoWsFavorites.route) {
            FavoritesScreen(
                viewModel = neoWsViewModel,
                onNavigateToDetail = { asteroidId ->
                    navController.navigate(Screen.NeoWsDetail.createRoute(asteroidId))
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
