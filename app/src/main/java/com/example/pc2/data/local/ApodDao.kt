package com.example.pc2.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ApodDao {
    @Query("SELECT * FROM apod_images WHERE date >= :startDate ORDER BY date DESC")
    fun observeSince(startDate: String): Flow<List<ApodEntity>>

    @Query("SELECT * FROM apod_images WHERE isFavorite = 1 ORDER BY date DESC")
    fun observeFavorites(): Flow<List<ApodEntity>>

    @Query("SELECT * FROM apod_images WHERE date = :date")
    fun observeByDate(date: String): Flow<ApodEntity?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfMissing(apod: ApodEntity)

    @Query(
        """
        UPDATE apod_images
        SET title = :title,
            explanation = :explanation,
            mediaType = :mediaType,
            hdUrl = :hdUrl,
            credit = :credit
        WHERE date = :date
        """
    )
    suspend fun updateContent(
        date: String,
        title: String?,
        explanation: String?,
        mediaType: String?,
        hdUrl: String?,
        credit: String?
    )

    @Query("UPDATE apod_images SET isFavorite = NOT isFavorite WHERE date = :date")
    suspend fun toggleFavorite(date: String)
}
