package com.example.pc2.presentation.apod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pc2.data.local.ApodEntity
import com.example.pc2.data.repository.ApodRepository
import com.google.gson.JsonParseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class ApodUiState(
    val isRefreshing: Boolean = true,
    val errorMessage: String? = null
)

class ApodViewModel(
    private val repository: ApodRepository
) : ViewModel() {
    val images: Flow<List<ApodEntity>> = repository.images
    val favorites: Flow<List<ApodEntity>> = repository.favorites

    fun observeImage(date: String): Flow<ApodEntity?> = repository.observeImage(date)

    private val _uiState = MutableStateFlow(ApodUiState())
    val uiState: StateFlow<ApodUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = ApodUiState(isRefreshing = true)
            try {
                repository.refreshLastSevenDays()
                _uiState.value = ApodUiState(isRefreshing = false)
            } catch (error: IOException) {
                showError(error.message ?: "No se pudo conectar con NASA.")
            } catch (error: HttpException) {
                showError("NASA respondió con el código HTTP ${error.code()}.")
            } catch (error: IllegalStateException) {
                showError(error.message ?: "No se pudo cargar APOD.")
            } catch (error: JsonParseException) {
                showError("NASA devolvió una respuesta APOD que no se pudo interpretar.")
            }
        }
    }

    fun toggleFavorite(date: String) {
        viewModelScope.launch {
            repository.toggleFavorite(date)
        }
    }

    private fun showError(message: String) {
        _uiState.value = ApodUiState(isRefreshing = false, errorMessage = message)
    }

    companion object {
        fun factory(repository: ApodRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (!modelClass.isAssignableFrom(ApodViewModel::class.java)) {
                        throw IllegalArgumentException("ViewModel no soportado: ${modelClass.name}")
                    }
                    return ApodViewModel(repository) as T
                }
            }
    }
}
