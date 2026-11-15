package com.example.pc2.data.repository

import com.example.pc2.data.local.ApodDao
import com.example.pc2.data.local.ApodEntity
import com.example.pc2.data.remote.ApodApi
import com.example.pc2.data.remote.ApodDto
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class ApodSummary(
    val total: Int,
    val images: Int,
    val videos: Int
) {
    val other: Int get() = total - images - videos
}

class ApodRepository(
    private val dao: ApodDao,
    private val api: ApodApi,
    private val apiKey: String
) {
    val images: Flow<List<ApodEntity>> =
        dao.observeSince(ApodDateRange.lastSevenDays(LocalDate.now()).first)
    val favorites: Flow<List<ApodEntity>> = dao.observeFavorites()

    fun observeImage(date: String): Flow<ApodEntity?> = dao.observeByDate(date)

    suspend fun refreshLastSevenDays(): ApodSummary {
        check(apiKey.isNotBlank()) {
            "Falta NASA_API_KEY en local.properties."
        }
        val (startDate, endDate) = ApodDateRange.lastSevenDays(LocalDate.now())
        val results = api.getApods(apiKey, startDate, endDate)
        results.mapNotNull(ApodDto::toEntityOrNull).forEach { apod ->
            dao.insertIfMissing(apod)
            dao.updateContent(
                date = apod.date,
                title = apod.title,
                explanation = apod.explanation,
                mediaType = apod.mediaType,
                hdUrl = apod.hdUrl,
                credit = apod.credit
            )
        }
        return ApodSummary(
            total = results.size,
            images = results.count { it.mediaType.equals("image", ignoreCase = true) },
            videos = results.count { it.mediaType.equals("video", ignoreCase = true) }
        )
    }

    suspend fun toggleFavorite(date: String) {
        dao.toggleFavorite(date)
    }
}

internal object ApodDateRange {
    fun lastSevenDays(today: LocalDate): Pair<String, String> {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        return today.minusDays(6).format(formatter) to today.format(formatter)
    }
}

internal fun ApodDto.toEntityOrNull(): ApodEntity? {
    val validDate = date?.takeIf(String::isNotBlank) ?: return null
    return ApodEntity(
        date = validDate,
        title = title,
        explanation = explanation,
        mediaType = mediaType,
        hdUrl = hdUrl,
        credit = credit
    )
}
