package com.example.pc2.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.pc2.ui.navigation.Screen
import com.example.pc2.ui.neows.AsteroidDetailScreen
import com.example.pc2.ui.neows.AsteroidListScreen
import com.example.pc2.ui.neows.FavoritesScreen
import com.example.pc2.ui.neows.NeoWsViewModel

private const val APOD_FAVORITES_ROUTE = "apod/favorites"
private const val APOD_DETAIL_ROUTE = "apod/{date}"

@Composable
fun AppNavGraph(viewModel: ApodViewModel, modifier: Modifier = Modifier) {
    val images by viewModel.images.collectAsState(initial = emptyList())
    val favorites by viewModel.favorites.collectAsState(initial = emptyList())
    val uiState by viewModel.uiState.collectAsState()
    val neoWsViewModel: NeoWsViewModel = viewModel()
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val isAsteroids = currentRoute?.startsWith("neows") == true

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = !isAsteroids,
                    onClick = {
                        if (isAsteroids) {
                            navController.popBackStack(Screen.Apod.route, inclusive = false)
                        }
                    },
                    icon = { Icon(Screen.Apod.icon, contentDescription = null) },
                    label = { Text(Screen.Apod.title) }
                )
                NavigationBarItem(
                    selected = isAsteroids,
                    onClick = {
                        if (!isAsteroids) {
                            navController.navigate(Screen.NeoWs.route) {
                                popUpTo(Screen.Apod.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = { Icon(Screen.NeoWs.icon, contentDescription = null) },
                    label = { Text(Screen.NeoWs.title) }
                )
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Apod.route,
            modifier = Modifier.padding(it)
        ) {
            composable(Screen.Apod.route) {
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
}
