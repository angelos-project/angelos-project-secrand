package org.angproj.sec

import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertTrue

class GarbageGarblerMockitoTest {

    @Test
    fun testDepletedFlagWithMockito() {
        val garbler = spy(GarbageGarbler())

        // Simulate count just below the threshold
        whenever(garbler.count).thenReturn(Int.MAX_VALUE / 2 - 1)
        assertTrue(!garbler.depleted)

        // Simulate count at the threshold
        whenever(garbler.count).thenReturn(Int.MAX_VALUE / 2)
        assertTrue(garbler.depleted)
    }
}