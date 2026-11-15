package com.example.pc2.data.repository

import android.content.Context
import com.example.pc2.data.local.AppDatabase
import com.example.pc2.data.local.neows.AsteroidDao
import com.example.pc2.data.local.neows.AsteroidEntity
import com.example.pc2.data.remote.RetrofitClient
import com.example.pc2.data.remote.neows.NeoWsApiService
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NeoWsRepository(
    private val asteroidDao: AsteroidDao,
    private val apiService: NeoWsApiService
) {
    constructor(context: Context) : this(
        AppDatabase.getInstance(context).asteroidDao(),
        RetrofitClient.apiRetrofit.create(NeoWsApiService::class.java)
    )

    val allAsteroids: Flow<List<AsteroidEntity>> = asteroidDao.getAllAsteroids()
    val favoriteAsteroids: Flow<List<AsteroidEntity>> = asteroidDao.getFavoriteAsteroids()

    suspend fun refreshAsteroids(apiKey: String) {
        check(apiKey.isNotBlank()) { "Falta NASA_API_KEY en local.properties." }
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(6)
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE

        val response = apiService.getFeed(
            startDate = startDate.format(formatter),
            endDate = endDate.format(formatter),
            apiKey = apiKey
        )

        val remoteAsteroids = mutableListOf<AsteroidEntity>()

        response.nearEarthObjects?.values?.flatten()?.forEach { dto ->
            val id = dto.id ?: return@forEach
            val name = dto.name ?: "Unknown"
            val isHazardous = dto.isPotentiallyHazardous ?: false
            
            val diameterMin = dto.estimatedDiameter?.meters?.estimatedDiameterMin ?: 0.0
            val diameterMax = dto.estimatedDiameter?.meters?.estimatedDiameterMax ?: 0.0

            val approachData = dto.closeApproachData?.firstOrNull()
            val approachDate = approachData?.closeApproachDate ?: LocalDate.now().toString()
            val velocity = approachData?.relativeVelocity?.kilometersPerHour?.toDoubleOrNull() ?: 0.0
            val distance = approachData?.missDistance?.kilometers?.toDoubleOrNull() ?: 0.0

            remoteAsteroids.add(
                AsteroidEntity(
                    id = id,
                    name = name,
                    closeApproachDate = approachDate,
                    estimatedDiameterMin = diameterMin,
                    estimatedDiameterMax = diameterMax,
                    relativeVelocity = velocity,
                    missDistance = distance,
                    isPotentiallyHazardous = isHazardous
                )
            )
        }

        if (remoteAsteroids.isNotEmpty()) {
            asteroidDao.insertAsteroids(remoteAsteroids)
        }
    }

    fun observeAsteroidById(id: String): Flow<AsteroidEntity?> = asteroidDao.observeAsteroidById(id)

    suspend fun toggleFavorite(id: String) {
        asteroidDao.toggleFavorite(id)
    }
}
