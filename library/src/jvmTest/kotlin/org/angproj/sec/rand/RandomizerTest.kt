package org.angproj.sec.rand

import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for the [Randomizer] interface using Mockito.
 *
 * This test class demonstrates how to mock the [Randomizer] interface and verify its behavior.
 * It also provides examples for testing the companion object utility functions.
 */
class RandomizerTest {

    /**
     * Tests the [Randomizer.getNextBits] method using a Mockito mock.
     * Verifies that the mocked method returns the expected value.
     */
    @Test
    fun testGetNextBits() {
        val mockRandomizer = mock<Randomizer>()
        whenever(mockRandomizer.getNextBits(8)).thenReturn(0xAB)
        val result = mockRandomizer.getNextBits(8)
        assertEquals(0xAB, result, "getNextBits should return the mocked value.")
    }

    /**
     * Tests the [Randomizer.Companion.foldBits] utility function.
     * Verifies that folding a known long value produces the expected result.
     */
    @Test
    fun testFoldBits() {
        val value: Long = 0x12345678_9ABCDEF0
        val expected = (0x12345678 xor 0x9ABCDEF0.toInt())
        val result = Randomizer.foldBits<Long>(value)
        assertEquals(expected, result, "foldBits should XOR upper and lower 32 bits.")
    }

    /**
     * Tests the [Randomizer.Companion.reduceBits] utility function.
     * Verifies that reducing an int value to a specified number of bits works as expected.
     */
    @Test
    fun testReduceBits() {
        val value = 0xFFFFFFFF.toInt()
        val bits = 8
        val expected = value ushr (32 - bits)
        val result = Randomizer.reduceBits<Int>(bits, value)
        assertEquals(expected, result, "reduceBits should shift value to keep only the specified bits.")
    }
}