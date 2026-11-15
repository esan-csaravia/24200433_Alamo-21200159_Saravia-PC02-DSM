package com.example.pc2.presentation.navigation

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.pc2.data.local.AppDatabase
import com.example.pc2.data.remote.ApodApi
import com.example.pc2.data.remote.ApodDto
import com.example.pc2.data.repository.ApodRepository
import com.example.pc2.presentation.apod.ApodViewModel
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppNavGraphTest {
    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java
        ).build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun bottomBarSwitchesBetweenImagesAndAsteroids() {
        val api = object : ApodApi {
            override suspend fun getApods(
                apiKey: String,
                startDate: String,
                endDate: String
            ): List<ApodDto> = emptyList()
        }
        val viewModel = ApodViewModel(ApodRepository(database.apodDao(), api, "test-key"))
        composeRule.setContent { AppNavGraph(viewModel) }

        composeRule.onNodeWithText("Imágenes").assertIsSelected()
        composeRule.onNodeWithText("Asteroides").performClick().assertIsSelected()
        composeRule.onNodeWithText("Asteroides (NeoWs)").assertExists()
        composeRule.onNodeWithText("Imágenes").performClick().assertIsSelected()
        composeRule.onNodeWithText("Imágenes APOD").assertExists()
    }
}
