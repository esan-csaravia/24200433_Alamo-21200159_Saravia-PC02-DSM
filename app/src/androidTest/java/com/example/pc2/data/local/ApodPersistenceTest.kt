package com.example.pc2.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.pc2.data.remote.ApodApi
import com.example.pc2.data.remote.ApodDto
import com.example.pc2.data.repository.ApodRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class ApodPersistenceTest {
    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private val databaseName = "apod-test-${UUID.randomUUID()}.db"

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        database = openDatabase()
    }

    @After
    fun tearDown() {
        database.close()
        context.deleteDatabase(databaseName)
    }

    @Test
    fun refreshAndFavoritesPersistAcrossReopenAndRefresh() = runBlocking {
        val today = LocalDate.now().toString()
        val api = FakeApodApi(today)
        val repository = ApodRepository(database.apodDao(), api, "test-key")

        repository.refreshLastSevenDays()
        assertEquals(LocalDate.now().minusDays(6).toString(), api.startDate)
        assertEquals(today, api.endDate)
        assertEquals("Primera versión", repository.images.first().single().title)
        repository.toggleFavorite(today)
        assertTrue(repository.favorites.first().single().isFavorite)

        database.close()
        database = openDatabase()
        val reopened = ApodRepository(database.apodDao(), api, "test-key")
        assertEquals("Primera versión", reopened.images.first().single().title)
        assertTrue(reopened.favorites.first().single().isFavorite)

        api.title = "Versión actualizada"
        reopened.refreshLastSevenDays()
        assertEquals("Versión actualizada", reopened.images.first().single().title)
        assertTrue(reopened.favorites.first().single().isFavorite)
        reopened.toggleFavorite(today)
        assertTrue(reopened.favorites.first().isEmpty())
        assertFalse(reopened.observeImage(today).first()!!.isFavorite)
    }

    private fun openDatabase(): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, databaseName).build()

    private class FakeApodApi(private val date: String) : ApodApi {
        var title = "Primera versión"
        var startDate: String? = null
        var endDate: String? = null

        override suspend fun getApods(
            apiKey: String,
            startDate: String,
            endDate: String
        ): List<ApodDto> {
            this.startDate = startDate
            this.endDate = endDate
            return listOf(
                ApodDto(date, title, "<strong>Explicación</strong>", "image", "https://example.com/image.jpg", null)
            )
        }
    }
}
