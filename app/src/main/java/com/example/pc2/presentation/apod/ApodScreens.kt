package com.example.pc2.presentation.apod

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pc2.data.local.ApodEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApodListScreen(
    images: List<ApodEntity>,
    uiState: ApodUiState,
    onRefresh: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onOpen: (ApodEntity) -> Unit,
    onOpenFavorites: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Imágenes APOD") },
                actions = {
                    TextButton(onClick = onOpenFavorites) { Text("Favoritos") }
                    TextButton(onClick = onRefresh) { Text("Actualizar") }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            if (uiState.isRefreshing) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }
            uiState.errorMessage?.let { ErrorMessage(it, onRefresh) }
            if (images.isEmpty()) {
                EmptyContent(
                    message = if (uiState.isRefreshing) {
                        "Consultando las imágenes de NASA..."
                    } else {
                        "No hay imágenes APOD para mostrar."
                    }
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(images, key = ApodEntity::date) { apod ->
                        ApodCard(apod, onToggleFavorite, onOpen)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApodFavoritesScreen(
    favorites: List<ApodEntity>,
    onBack: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onOpen: (ApodEntity) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis imágenes favoritas") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Volver") } }
            )
        }
    ) { padding ->
        if (favorites.isEmpty()) {
            EmptyContent(
                message = "Todavía no tienes imágenes favoritas.",
                modifier = Modifier.fillMaxSize().padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favorites, key = ApodEntity::date) { apod ->
                    ApodCard(apod, onToggleFavorite, onOpen)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApodDetailScreen(
    apod: ApodEntity?,
    onBack: () -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle APOD") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Volver") } }
            )
        }
    ) { padding ->
        if (apod == null) {
            EmptyContent(
                message = "No se encontró esta imagen.",
                modifier = Modifier.fillMaxSize().padding(padding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                if (apod.isVideo()) {
                    VideoPlaceholder(Modifier.fillMaxWidth().height(240.dp))
                } else if (!apod.hdUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = apod.hdUrl,
                        contentDescription = apod.title ?: "Imagen astronómica",
                        modifier = Modifier.fillMaxWidth().height(280.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    VideoPlaceholder(
                        Modifier.fillMaxWidth().height(180.dp),
                        label = "Imagen no disponible"
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(apod.title ?: "Sin título", style = MaterialTheme.typography.headlineSmall)
                Text(apod.date, style = MaterialTheme.typography.titleSmall)
                FavoriteButton(apod, onToggleFavorite)
                Spacer(Modifier.height(12.dp))
                ApodExplanation(apod.explanation)
                apod.credit?.takeIf(String::isNotBlank)?.let { credit ->
                    Spacer(Modifier.height(12.dp))
                    Text("Crédito: $credit", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
internal fun ApodExplanation(explanation: String?) {
    val html = explanation?.takeIf(String::isNotBlank)
    if (html == null) {
        Text("No hay una explicación disponible.")
        return
    }

    val linkColor = MaterialTheme.colorScheme.primary
    val text = remember(html, linkColor) {
        AnnotatedString.fromHtml(
            htmlString = html,
            linkStyles = TextLinkStyles(
                style = SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline)
            )
        )
    }
    Text(text)
}

@Composable
private fun ApodCard(
    apod: ApodEntity,
    onToggleFavorite: (String) -> Unit,
    onOpen: (ApodEntity) -> Unit
) {
    Card(onClick = { onOpen(apod) }, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            if (apod.isVideo()) {
                VideoPlaceholder(Modifier.fillMaxWidth().height(170.dp))
            } else if (!apod.hdUrl.isNullOrBlank()) {
                AsyncImage(
                    model = apod.hdUrl,
                    contentDescription = apod.title ?: "Imagen astronómica",
                    modifier = Modifier.fillMaxWidth().height(190.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                VideoPlaceholder(
                    Modifier.fillMaxWidth().height(120.dp),
                    label = "Imagen no disponible"
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        apod.title ?: "Sin título",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(apod.date, style = MaterialTheme.typography.bodySmall)
                    if (apod.isVideo()) {
                        Text("Contenido en video", style = MaterialTheme.typography.bodySmall)
                    }
                }
                FavoriteButton(apod, onToggleFavorite)
            }
        }
    }
}

@Composable
private fun FavoriteButton(apod: ApodEntity, onToggleFavorite: (String) -> Unit) {
    TextButton(
        onClick = { onToggleFavorite(apod.date) },
        modifier = Modifier.semantics {
            contentDescription = if (apod.isFavorite) {
                "Quitar de favoritos"
            } else {
                "Agregar a favoritos"
            }
        }
    ) {
        Text(if (apod.isFavorite) "♥" else "♡")
    }
}

@Composable
private fun VideoPlaceholder(
    modifier: Modifier = Modifier,
    label: String = "Contenido en video"
) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Text(label, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun EmptyContent(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(message)
    }
}

@Composable
private fun ErrorMessage(message: String, onRetry: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(message, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.error)
        TextButton(onClick = onRetry) { Text("Reintentar") }
    }
}

private fun ApodEntity.isVideo(): Boolean = mediaType.equals("video", ignoreCase = true)
