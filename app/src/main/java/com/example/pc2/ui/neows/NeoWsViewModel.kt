package com.example.pc2.ui.neows

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pc2.BuildConfig
import com.example.pc2.data.local.neows.AsteroidEntity
import com.example.pc2.data.repository.NeoWsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.net.SocketTimeoutException
import java.time.LocalDate

sealed interface NeoWsUiState {
    object Loading : NeoWsUiState
    data class Success(
        val asteroids: List<AsteroidEntity>,
        val favorites: List<AsteroidEntity>,
        val showOnlyHazardous: Boolean,
        val isRefreshing: Boolean,
        val errorMessage: String? = null
    ) : NeoWsUiState
    data class Error(val message: String, val cachedAsteroids: List<AsteroidEntity>) : NeoWsUiState
}

class NeoWsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NeoWsRepository(application)

    private val _showOnlyHazardous = MutableStateFlow(false)
    private val _isRefreshing = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<NeoWsUiState> = combine(
        repository.allAsteroids,
        repository.favoriteAsteroids,
        _showOnlyHazardous,
        _isRefreshing,
        _errorMessage
    ) { all, favs, onlyHazardous, refreshing, errorMsg ->
        // Limit main list strictly to the last 7 days (today - 6 days to today),
        // even though Room may retain favorite asteroids from previous dates.
        val today = LocalDate.now()
        val startDate = today.minusDays(6)

        val recentAsteroids = all.filter { asteroid ->
            try {
                val date = LocalDate.parse(asteroid.closeApproachDate)
                !date.isBefore(startDate) && !date.isAfter(today)
            } catch (e: Exception) {
                true
            }
        }

        val filtered = if (onlyHazardous) {
            recentAsteroids.filter { it.isPotentiallyHazardous }
        } else {
            recentAsteroids
        }

        NeoWsUiState.Success(
            asteroids = filtered,
            favorites = favs,
            showOnlyHazardous = onlyHazardous,
            isRefreshing = refreshing,
            errorMessage = errorMsg
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NeoWsUiState.Loading
    )

    init {
        fetchAsteroids()
    }

    fun fetchAsteroids() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _errorMessage.value = null
            try {
                repository.refreshAsteroids(BuildConfig.NASA_API_KEY)
            } catch (e: Exception) {
                Log.e("NeoWsViewModel", "Error al sincronizar datos de NeoWs: ${e.javaClass.simpleName} - ${e.localizedMessage}")
                val message = if (e is IllegalStateException && BuildConfig.NASA_API_KEY.isBlank()) {
                    "Falta NASA_API_KEY en local.properties."
                } else if (e is UnknownHostException || e is SocketTimeoutException) {
                    "Sin conexión a internet. Mostrando datos guardados en caché."
                } else {
                    "Error al sincronizar con el servidor. Mostrando datos guardados."
                }
                _errorMessage.value = message
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun toggleHazardousFilter(showOnly: Boolean) {
        _showOnlyHazardous.value = showOnly
    }

    fun toggleFavorite(asteroid: AsteroidEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(asteroid.id)
        }
    }

    fun observeAsteroidDetail(id: String) = repository.observeAsteroidById(id)
}
