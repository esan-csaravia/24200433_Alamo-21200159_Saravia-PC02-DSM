package com.example.pc2.ui.neows

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pc2.data.local.neows.AsteroidEntity
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsteroidListScreen(
    viewModel: NeoWsViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToFavorites: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Asteroides (NeoWs)") },
                actions = {
                    IconButton(onClick = onNavigateToFavorites) {
                        Icon(Icons.Default.Star, contentDescription = "Mis asteroides")
                    }
                    IconButton(onClick = { viewModel.fetchAsteroids() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is NeoWsUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is NeoWsUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Solo potencialmente peligrosos", style = MaterialTheme.typography.bodyMedium)
                            Switch(
                                checked = state.showOnlyHazardous,
                                onCheckedChange = { viewModel.toggleHazardousFilter(it) }
                            )
                        }

                        if (state.errorMessage != null) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.errorContainer
                            ) {
                                Text(
                                    text = state.errorMessage,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        if (state.asteroids.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No se encontraron asteroides.",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.asteroids, key = { it.id }) { asteroid ->
                                    AsteroidItemCard(
                                        asteroid = asteroid,
                                        onClick = { onNavigateToDetail(asteroid.id) },
                                        onFavoriteClick = { viewModel.toggleFavorite(asteroid) }
                                    )
                                }
                            }
                        }
                    }
                }
                is NeoWsUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.fetchAsteroids() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AsteroidItemCard(
    asteroid: AsteroidEntity,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    val maxDiameterStr = String.format(Locale.US, "%.2f", asteroid.estimatedDiameterMax)
    val velocityStr = String.format(Locale.US, "%.2f", asteroid.relativeVelocity)
    val distanceStr = String.format(Locale.US, "%.2f", asteroid.missDistance)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(
                        text = asteroid.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (asteroid.isPotentiallyHazardous) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Peligroso",
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (asteroid.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (asteroid.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Fecha aproximación: ${asteroid.closeApproachDate}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Diámetro máx: $maxDiameterStr m",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Velocidad: $velocityStr km/h",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Distancia: $distanceStr km",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
