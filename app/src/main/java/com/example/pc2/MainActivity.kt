package com.example.pc2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pc2.data.local.AppDatabase
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
                val context = LocalContext.current.applicationContext
                val repository = remember(context) {
                    ApodRepository(
                        dao = AppDatabase.getDatabase(context).apodDao(),
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
