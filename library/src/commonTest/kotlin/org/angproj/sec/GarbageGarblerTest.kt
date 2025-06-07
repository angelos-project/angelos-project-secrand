package org.angproj.sec

import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.test.assertEquals


class GarbageGarblerTest {

    @Test
    fun testRandomBytesAreNotConstant() {
        val garbler = GarbageGarbler()
        val buffer1 = ByteArray(32)
        val buffer2 = ByteArray(32)

        // Fill buffer1
        for (i in buffer1.indices) {
            buffer1[i] = garbler.readByte()
        }
        // Fill buffer2
        for (i in buffer2.indices) {
            buffer2[i] = garbler.readByte()
        }
        // With high probability, two random arrays should not be equal
        assertNotEquals(buffer1.toList(), buffer2.toList())
    }

    @Test
    fun testTriggerReseedAndCountReset() {
        val garbler = GarbageGarbler()

        repeat(256) { garbler.readDouble() }
        val count = garbler.count
        assertEquals(2048, count)

        // Use the importBytes to fill entropy and trigger reseed
        val seed = ByteArray(128) { it.toByte() }
        garbler.importBytes(seed, 0, seed.size) { idx -> this[idx] }
        assertTrue { count > garbler.count }
    }

    @Test
    fun testCountIncreases() {
        val garbler = GarbageGarbler()
        val initialCount = garbler.count
        repeat(10) { garbler.readByte() }
        assertTrue(garbler.count > initialCount)
    }

    @Test
    fun testImportBytesRejectsZeroLength() {
        val garbler = GarbageGarbler()
        val data = ByteArray(10)
        try {
            garbler.importBytes(data, 0, 0) { idx -> this[idx] }
            assertTrue(false)
        } catch (e: IllegalArgumentException) {
            assertTrue(true)
        }
    }
}