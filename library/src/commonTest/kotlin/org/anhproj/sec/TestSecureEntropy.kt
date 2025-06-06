package org.anhproj.sec

import org.angproj.sec.SecureEntropy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TestSecureEntropy {

    @Test
    fun testExportBytesLengthAndRandomness() {
        val size = 32
        val buffer1 = ByteArray(size)
        val buffer2 = ByteArray(size)

        SecureEntropy.exportBytes(buffer1, 0, size) { idx, value -> this[idx] = value }
        SecureEntropy.exportBytes(buffer2, 0, size) { idx, value -> this[idx] = value }

        assertEquals(size, buffer1.size)
        assertEquals(size, buffer2.size)
        // With high probability, two random arrays should not be equal
        assertNotEquals(buffer1.toList(), buffer2.toList())
    }

    @Test
    fun testExportLongsLengthAndRandomness() {
        val size = 8
        val buffer1 = LongArray(size)
        val buffer2 = LongArray(size)

        SecureEntropy.exportLongs(buffer1, 0, size) { idx, value -> this[idx] = value }
        SecureEntropy.exportLongs(buffer2, 0, size) { idx, value -> this[idx] = value }

        assertEquals(size, buffer1.size)
        assertEquals(size, buffer2.size)
        // With high probability, two random arrays should not be equal
        assertNotEquals(buffer1.toList(), buffer2.toList())
    }
}