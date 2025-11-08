package com.aurorabridge.optimizer.utils

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdbCommanderTest {

    @Test
    fun `runAdbCommandAsync should return success`() = runBlocking {
        // Given
        val adbCommander: IAdbCommander = AdbCommanderTestDouble(isSuccess = true, output = "Success")
        val command = "test command"

        // When
        val result = adbCommander.runAdbCommandAsync(command)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Success", result.output)
    }

    @Test
    fun `runAdbCommandAsync should return error`() = runBlocking {
        // Given
        val adbCommander: IAdbCommander = AdbCommanderTestDouble(isSuccess = false, error = "Error")
        val command = "test command"

        // When
        val result = adbCommander.runAdbCommandAsync(command)

        // Then
        assertFalse(result.isSuccess)
        assertEquals("Error", result.error)
    }
}

class AdbCommanderTestDouble(private val isSuccess: Boolean, private val output: String? = null, private val error: String? = null) : IAdbCommander {
    override suspend fun runAdbCommandAsync(command: String): AdbCommandResult {
        return AdbCommandResult(isSuccess, output, error)
    }
}
