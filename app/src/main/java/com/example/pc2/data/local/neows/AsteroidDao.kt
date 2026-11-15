package com.example.pc2.data.local.neows

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface AsteroidDao {
    @Query("SELECT * FROM asteroids ORDER BY closeApproachDate DESC")
    fun getAllAsteroids(): Flow<List<AsteroidEntity>>

    @Query("SELECT * FROM asteroids WHERE isFavorite = 1 ORDER BY closeApproachDate DESC")
    fun getFavoriteAsteroids(): Flow<List<AsteroidEntity>>

    @Query("SELECT * FROM asteroids WHERE id = :id")
    suspend fun getAsteroidById(id: String): AsteroidEntity?

    @Query("SELECT * FROM asteroids WHERE id = :id")
    fun observeAsteroidById(id: String): Flow<AsteroidEntity?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfMissing(asteroids: List<AsteroidEntity>)

    @Query("""UPDATE asteroids SET name = :name, closeApproachDate = :date,
        estimatedDiameterMin = :minDiameter, estimatedDiameterMax = :maxDiameter,
        relativeVelocity = :velocity, missDistance = :distance,
        isPotentiallyHazardous = :hazardous WHERE id = :id""")
    suspend fun updateContent(
        id: String, name: String, date: String, minDiameter: Double, maxDiameter: Double,
        velocity: Double, distance: Double, hazardous: Boolean
    )

    @Transaction
    suspend fun insertAsteroids(asteroids: List<AsteroidEntity>) {
        insertIfMissing(asteroids)
        asteroids.forEach {
            updateContent(it.id, it.name, it.closeApproachDate, it.estimatedDiameterMin,
                it.estimatedDiameterMax, it.relativeVelocity, it.missDistance, it.isPotentiallyHazardous)
        }
    }

    @Query("UPDATE asteroids SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: String)
}
