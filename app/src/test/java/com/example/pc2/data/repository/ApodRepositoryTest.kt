package com.example.pc2.data.repository

import com.example.pc2.data.remote.ApodDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class ApodRepositoryTest {
    @Test
    fun lastSevenDaysIncludesTodayAndSixPreviousDays() {
        assertEquals(
            "2026-11-08" to "2026-11-14",
            ApodDateRange.lastSevenDays(LocalDate.of(2026, 11, 14))
        )
    }

    @Test
    fun dtoWithMissingOptionalFieldsCanBeMapped() {
        val entity = ApodDto(
            date = "2026-11-14",
            title = null,
            explanation = null,
            mediaType = "video",
            hdUrl = null,
            credit = null
        ).toEntityOrNull()

        assertEquals("2026-11-14", entity?.date)
        assertEquals("video", entity?.mediaType)
        assertNull(entity?.hdUrl)
    }

    @Test
    fun dtoWithoutDateIsIgnored() {
        assertNull(
            ApodDto(
                date = null,
                title = "No date",
                explanation = null,
                mediaType = "image",
                hdUrl = null,
                credit = null
            ).toEntityOrNull()
        )
    }
}
