package com.example.pc2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pc2.ui.navigation.NavGraph
import com.example.pc2.ui.navigation.Screen
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pc2.data.local.ApodDatabase
import com.example.pc2.data.remote.ApodApiClient
import com.example.pc2.data.repository.ApodRepository
import com.example.pc2.presentation.apod.ApodViewModel
import com.example.pc2.presentation.navigation.AppNavGraph
import com.example.pc2.ui.theme.PC2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PC2Theme {
                val navController = rememberNavController()
                val items = listOf(
                    Screen.Apod,
                    Screen.NeoWs
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentRoute = navBackStackEntry?.destination?.route

                            items.forEach { screen ->
                                NavigationBarItem(
                                    icon = { Icon(screen.icon, contentDescription = screen.title) },
                                    label = { Text(screen.title) },
                                    selected = currentRoute == screen.route,
                                    onClick = {
                                        if (currentRoute != screen.route) {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavGraph(navController = navController, innerPadding = innerPadding)
                val context = LocalContext.current.applicationContext
                val repository = remember(context) {
                    ApodRepository(
                        dao = ApodDatabase.getDatabase(context).apodDao(),
                        api = ApodApiClient.api,
                        apiKey = BuildConfig.NASA_API_KEY
                    )
                }
                val viewModel: ApodViewModel = viewModel(
                    factory = ApodViewModel.factory(repository)
                )
                AppNavGraph(viewModel = viewModel, modifier = Modifier.fillMaxSize())
            }
        }
    }
}
}
