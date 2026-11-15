package com.example.pc2.data.local.neows

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asteroids")
data class AsteroidEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val closeApproachDate: String,
    val estimatedDiameterMin: Double,
    val estimatedDiameterMax: Double,
    val relativeVelocity: Double,
    val missDistance: Double,
    val isPotentiallyHazardous: Boolean,
    val isFavorite: Boolean = false
)
