package com.example.pc2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "apod_images")
data class ApodEntity(
    @PrimaryKey val date: String,
    val title: String?,
    val explanation: String?,
    val mediaType: String?,
    val hdUrl: String?,
    val credit: String?,
    val isFavorite: Boolean = false
)
