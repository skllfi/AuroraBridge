package com.aurorabridge.optimizer.ui.vm

import android.content.Context
import com.aurorabridge.optimizer.utils.LocaleManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LanguageViewModelTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockLocaleManager: LocaleManager

    private lateinit var viewModel: LanguageViewModel

    private val englishLanguage = Language("English", "en")
    private val russianLanguage = Language("Русский", "ru")
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LanguageViewModel(mockLocaleManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadInitialLanguage should update selectedLanguage`() = runTest {
        // Given
        `when`(mockLocaleManager.getLanguage(mockContext)).thenReturn("ru")

        // When
        viewModel.loadInitialLanguage(mockContext)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(russianLanguage, viewModel.selectedLanguage.value)
    }

    @Test
    fun `setLanguage should call LocaleManager and update state`() {
        // When
        viewModel.setLanguage(mockContext, englishLanguage)

        // Then
        verify(mockLocaleManager).setLocale(mockContext, "en")
        assertEquals(englishLanguage, viewModel.selectedLanguage.value)
    }

    @Test
    fun `availableLanguages should contain predefined languages`() {
        // Given
        val expectedLanguages = listOf(englishLanguage, russianLanguage)

        // When
        val actualLanguages = viewModel.availableLanguages.value

        // Then
        assertEquals(expectedLanguages, actualLanguages)
    }
}
