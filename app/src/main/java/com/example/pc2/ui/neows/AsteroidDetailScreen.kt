package com.example.pc2.ui.neows

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pc2.data.local.neows.AsteroidEntity
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsteroidDetailScreen(
    asteroidId: String,
    viewModel: NeoWsViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var asteroid by remember { mutableStateOf<AsteroidEntity?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(asteroidId) {
        scope.launch {
            asteroid = viewModel.getAsteroidDetail(asteroidId)
            isLoading = false
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Asteroide") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (asteroid == null) {
                Text(
                    text = "No se encontró información del asteroide.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                val item = asteroid!!
                val minDiameterStr = String.format(Locale.US, "%.2f", item.estimatedDiameterMin)
                val maxDiameterStr = String.format(Locale.US, "%.2f", item.estimatedDiameterMax)
                val velocityStr = String.format(Locale.US, "%.2f", item.relativeVelocity)
                val distanceStr = String.format(Locale.US, "%.2f", item.missDistance)

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            viewModel.toggleFavorite(item)
                            asteroid = item.copy(isFavorite = !item.isFavorite)
                        }) {
                            Icon(
                                imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorito",
                                tint = if (item.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Divider()

                    DetailRow(label = "ID NASA:", value = item.id)
                    DetailRow(label = "Fecha de aproximación:", value = item.closeApproachDate)
                    DetailRow(label = "Diámetro estimado:", value = "$minDiameterStr m - $maxDiameterStr m")
                    DetailRow(label = "Velocidad relativa:", value = "$velocityStr km/h")
                    DetailRow(label = "Distancia de paso:", value = "$distanceStr km")

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "Peligroso potencialmente:", fontWeight = FontWeight.SemiBold)
                        if (item.isPotentiallyHazardous) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Peligroso",
                                tint = Color.Red
                            )
                            Text(text = "SÍ", color = Color.Red, fontWeight = FontWeight.Bold)
                        } else {
                            Text(text = "NO", color = Color.Green, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontWeight = FontWeight.Bold)
    }
}
