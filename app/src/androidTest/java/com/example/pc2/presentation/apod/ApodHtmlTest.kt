package com.example.pc2.presentation.apod

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ApodHtmlTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun explanationScreenShowsHtmlAsReadableText() {
        composeRule.setContent {
            ApodExplanation(
                """<strong>Explanation:</strong> A &amp; B <a href="https://science.nasa.gov/">NASA</a>"""
            )
        }

        composeRule.onNodeWithText("Explanation: A & B NASA").assertExists()
    }

    @Test
    fun missingExplanationShowsFallback() {
        composeRule.setContent { ApodExplanation(null) }

        composeRule.onNodeWithText("No hay una explicación disponible.").assertExists()
    }

    @Test
    fun htmlExplanationRendersTextFormattingAndLinks() {
        val text = AnnotatedString.fromHtml(
            """<strong>Explanation:</strong> A &amp; B <a href="https://science.nasa.gov/">NASA</a>"""
        )

        assertEquals("Explanation: A & B NASA", text.text)
        assertTrue(text.spanStyles.any { it.item.fontWeight == FontWeight.Bold })
        val links = text.getLinkAnnotations(0, text.length)
        assertEquals(1, links.size)
        assertEquals("https://science.nasa.gov/", (links.single().item as LinkAnnotation.Url).url)
    }

    @Test
    fun plainTextRemainsReadable() {
        val text = AnnotatedString.fromHtml("A plain explanation.")
        assertEquals("A plain explanation.", text.text)
    }
}
