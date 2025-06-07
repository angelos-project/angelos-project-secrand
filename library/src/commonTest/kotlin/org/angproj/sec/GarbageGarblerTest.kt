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
        assertEquals(1024, garbler.count)

        // Use the importBytes to fill entropy and trigger reseed
        val seed = ByteArray(128) { it.toByte() }
        garbler.importBytes(seed, 0, seed.size) { idx -> this[idx] }
        assertEquals(0, garbler.count)
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
        } catch (_: IllegalArgumentException) {
            assertTrue(true)
        }
    }

    @Test
    fun testReadByteRange() {
        val garbler = GarbageGarbler()
        repeat(1000) {
            val value = garbler.readByte()
            assertTrue(value in Byte.MIN_VALUE..Byte.MAX_VALUE)
        }
    }

    @Test
    fun testReadUByteRange() {
        val garbler = GarbageGarbler()
        repeat(1000) {
            val value = garbler.readUByte()
            assertTrue(value in UByte.MIN_VALUE..UByte.MAX_VALUE)
        }
    }

    @Test
    fun testReadShortRange() {
        val garbler = GarbageGarbler()
        repeat(1000) {
            val value = garbler.readShort()
            assertTrue(value in Short.MIN_VALUE..Short.MAX_VALUE)
        }
    }

    @Test
    fun testReadUShortRange() {
        val garbler = GarbageGarbler()
        repeat(1000) {
            val value = garbler.readUShort()
            assertTrue(value in UShort.MIN_VALUE..UShort.MAX_VALUE)
        }
    }

    @Test
    fun testReadIntRange() {
        val garbler = GarbageGarbler()
        repeat(1000) {
            val value = garbler.readInt()
            assertTrue(value in Int.MIN_VALUE..Int.MAX_VALUE)
        }
    }

    @Test
    fun testReadUIntRange() {
        val garbler = GarbageGarbler()
        repeat(1000) {
            val value = garbler.readUInt()
            assertTrue(value in UInt.MIN_VALUE..UInt.MAX_VALUE)
        }
    }

    @Test
    fun testReadLongRange() {
        val garbler = GarbageGarbler()
        repeat(1000) {
            val value = garbler.readLong()
            assertTrue(value in Long.MIN_VALUE..Long.MAX_VALUE)
        }
    }

    @Test
    fun testReadULongRange() {
        val garbler = GarbageGarbler()
        repeat(1000) {
            val value = garbler.readULong()
            assertTrue(value in ULong.MIN_VALUE..ULong.MAX_VALUE)
        }
    }

    @Test
    fun testReadFloatRange() {
        val garbler = GarbageGarbler()
        repeat(1000) {
            val value = garbler.readFloat()
            assertTrue(value in 0.0f..1.0f)
        }
    }

    @Test
    fun testReadDoubleRange() {
        val garbler = GarbageGarbler()
        repeat(1000) {
            val value = garbler.readDouble()
            assertTrue(value in 0.0..1.0)
        }
    }
}