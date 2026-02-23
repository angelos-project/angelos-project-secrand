/**
 * Copyright (c) 2025 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
 *
 * This software is available under the terms of the MIT license. Parts are licensed
 * under different terms if stated. The legal terms are attached to the LICENSE file
 * and are made available on:
 *
 *      https://opensource.org/licenses/MIT
 *
 * SPDX-License-Identifier: MIT
 *
 * Contributors:
 *      Kristoffer Paulsson - initial implementation
 */
package org.angproj.sec.rand

import org.angproj.sec.stat.doubleHealthCheck
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class JitterEntropyTest {

    @Test
    fun testExportBytesLengthAndRandomness() {
        val size = 32
        val buffer1 = ByteArray(size)
        val buffer2 = ByteArray(size)

        JitterEntropy.readBytes(buffer1, 0, size) { idx, value -> this[idx] = value }
        JitterEntropy.readBytes(buffer2, 0, size) { idx, value -> this[idx] = value }

        assertEquals(size, buffer1.size)
        assertEquals(size, buffer2.size)
        // With high probability, two random arrays should not be equal
        assertNotEquals(buffer1, buffer2)
    }

    @Test
    fun testExportLongsLengthAndRandomness() {
        val size = 8
        val buffer1 = LongArray(size)
        val buffer2 = LongArray(size)

        JitterEntropy.readLongs(buffer1, 0, size) { idx, value -> this[idx] = value }
        JitterEntropy.readLongs(buffer2, 0, size) { idx, value -> this[idx] = value }

        assertEquals(size, buffer1.size)
        assertEquals(size, buffer2.size)
        // With high probability, two random arrays should not be equal
        assertNotEquals(buffer1, buffer2)
    }

    @Test
    fun testSecurityHealth() {
        /*val jitter = ByteArray(1024)
        repeat(2) {
            JitterEntropy.readBytes(jitter, 0, jitter.size) { idx, value -> this[idx] = value }
            println(jitter.asHexSymbols())
        }*/
        assertTrue{ doubleHealthCheck(JitterEntropy.JitterEntropyState()) }
    }
}