package com.example.pc2.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pc2.presentation.apod.ApodDetailScreen
import com.example.pc2.presentation.apod.ApodFavoritesScreen
import com.example.pc2.presentation.apod.ApodListScreen
import com.example.pc2.presentation.apod.ApodViewModel

private const val APOD_LIST_ROUTE = "apod"
private const val APOD_FAVORITES_ROUTE = "apod/favorites"
private const val APOD_DETAIL_ROUTE = "apod/{date}"
private const val ASTEROIDS_ROUTE = "asteroids"

@Composable
fun AppNavGraph(viewModel: ApodViewModel, modifier: Modifier = Modifier) {
    val images by viewModel.images.collectAsState(initial = emptyList())
    val favorites by viewModel.favorites.collectAsState(initial = emptyList())
    val uiState by viewModel.uiState.collectAsState()
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val isAsteroids = backStackEntry?.destination?.route == ASTEROIDS_ROUTE

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = !isAsteroids,
                    onClick = {
                        if (isAsteroids) {
                            navController.popBackStack(ASTEROIDS_ROUTE, inclusive = true)
                        }
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Imágenes") }
                )
                NavigationBarItem(
                    selected = isAsteroids,
                    onClick = {
                        if (!isAsteroids) navController.navigate(ASTEROIDS_ROUTE)
                    },
                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                    label = { Text("Asteroides") }
                )
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = APOD_LIST_ROUTE,
            modifier = Modifier.padding(it)
        ) {
            composable(APOD_LIST_ROUTE) {
                ApodListScreen(
                    images = images,
                    uiState = uiState,
                    onRefresh = viewModel::refresh,
                    onToggleFavorite = viewModel::toggleFavorite,
                    onOpen = { navController.navigate("apod/${it.date}") },
                    onOpenFavorites = { navController.navigate(APOD_FAVORITES_ROUTE) }
                )
            }
            composable(APOD_FAVORITES_ROUTE) {
                ApodFavoritesScreen(
                    favorites = favorites,
                    onBack = { navController.popBackStack() },
                    onToggleFavorite = viewModel::toggleFavorite,
                    onOpen = { navController.navigate("apod/${it.date}") }
                )
            }
            composable(
                route = APOD_DETAIL_ROUTE,
                arguments = listOf(navArgument("date") { type = NavType.StringType })
            ) { entry ->
                val date = entry.arguments?.getString("date")
                val apodFlow = remember(date) { viewModel.observeImage(date.orEmpty()) }
                val apod by apodFlow.collectAsState(initial = null)
                ApodDetailScreen(
                    apod = apod,
                    onBack = { navController.popBackStack() },
                    onToggleFavorite = viewModel::toggleFavorite
                )
            }
            composable(ASTEROIDS_ROUTE) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Sección de asteroides pendiente.")
                }
            }
        }
    }
}
