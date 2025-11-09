package com.aurorabridge.optimizer.optimizer

import android.content.Context
import android.content.res.Resources
import android.os.Build
import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.utils.AdbCommandResult
import com.aurorabridge.optimizer.utils.IAdbCommander
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.io.ByteArrayInputStream
import java.lang.reflect.Field

@RunWith(MockitoJUnitRunner::class)
class BrandAutoOptimizerTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockResources: Resources

    @Mock
    private lateinit var mockAdbCommander: IAdbCommander

    private lateinit var brandAutoOptimizer: BrandAutoOptimizer

    @Before
    fun setUp() {
        `when`(mockContext.resources).thenReturn(mockResources)
        brandAutoOptimizer = BrandAutoOptimizer(mockContext, mockAdbCommander)
    }

    @Test
    fun `getProfileForCurrentDevice should return correct profile for known brand`() {
        // Given
        setManufacturer("xiaomi")
        val packageName = "com.test.app"
        `when`(mockContext.packageName).thenReturn(packageName)
        val jsonContent = """{"XIAOMI": ["cmd1 ${'$'}packageName", "cmd2"]}"""
        val inputStream = ByteArrayInputStream(jsonContent.toByteArray())
        `when`(mockResources.openRawResource(R.raw.brand_optimizations)).thenReturn(inputStream)

        // When
        val profile = brandAutoOptimizer.getProfileForCurrentDevice()

        // Then
        assertNotNull(profile)
        assertEquals("Xiaomi", profile?.brandName)
        assertEquals(listOf("cmd1 com.test.app", "cmd2"), profile?.commands)
        assertEquals(R.string.optimization_desc_xiaomi, profile?.description)
    }

    @Test
    fun `getProfileForCurrentDevice should return null for unknown brand`() {
        // Given
        setManufacturer("unknown_brand")

        // When
        val profile = brandAutoOptimizer.getProfileForCurrentDevice()

        // Then
        assertNull(profile)
    }

    @Test
    fun `getProfileForCurrentDevice should return profile with empty commands if brand not in json`() {
        // Given
        setManufacturer("google") // A known brand, but let's say it's not in our JSON
        val packageName = "com.test.app"
        `when`(mockContext.packageName).thenReturn(packageName)
        val jsonContent = """{"XIAOMI": ["cmd1"]}""" // JSON doesn't contain PIXEL/GOOGLE
        val inputStream = ByteArrayInputStream(jsonContent.toByteArray())
        `when`(mockResources.openRawResource(R.raw.brand_optimizations)).thenReturn(inputStream)

        // When
        val profile = brandAutoOptimizer.getProfileForCurrentDevice()

        // Then
        assertNotNull(profile)
        assertEquals("Pixel", profile?.brandName)
        assertEquals(emptyList<String>(), profile?.commands)
        assertEquals(R.string.optimization_desc_pixel, profile?.description)
    }

    @Test
    fun `applyOptimization should run all commands and return true on success`() = runBlocking {
        // Given
        val commands = listOf("cmd1", "cmd2")
        val profile = OptimizationProfile("Test", R.string.empty, commands)
        `when`(mockAdbCommander.runAdbCommandAsync(anyString())).thenReturn(AdbCommandResult(isSuccess = true))

        // When
        val result = brandAutoOptimizer.applyOptimization(profile)

        // Then
        assertTrue(result)
        verify(mockAdbCommander).runAdbCommandAsync("cmd1")
        verify(mockAdbCommander).runAdbCommandAsync("cmd2")
    }

    @Test
    fun `applyOptimization should stop on first failure and return false`() = runBlocking {
        // Given
        val commands = listOf("cmd1", "cmd2")
        val profile = OptimizationProfile("Test", R.string.empty, commands)
        `when`(mockAdbCommander.runAdbCommandAsync("cmd1")).thenReturn(AdbCommandResult(isSuccess = false, error = "failure"))

        // When
        val result = brandAutoOptimizer.applyOptimization(profile)

        // Then
        assertFalse(result)
        verify(mockAdbCommander, times(1)).runAdbCommandAsync("cmd1")
        verify(mockAdbCommander, times(0)).runAdbCommandAsync("cmd2")
    }

    private fun setManufacturer(manufacturer: String) {
        try {
            val field: Field = Build::class.java.getDeclaredField("MANUFACTURER")
            field.isAccessible = true
            field.set(null, manufacturer)
        } catch (e: Exception) {
            // In a real test, you'd want to handle this more gracefully
            e.printStackTrace()
        }
    }
}
