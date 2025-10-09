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
package org.angproj.sec

import org.angproj.sec.rand.RandomBits
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertFalse


class SecureFeedTest {

    @Test
    fun testExportBytesLengthAndRandomness() {
        val size = 34
        val buffer1 = ByteArray(size)
        val buffer2 = ByteArray(size)

        SecureFeed.readBytes(buffer1, 0, size) { idx, value -> this[idx] = value }
        SecureFeed.readBytes(buffer2, 0, size) { idx, value -> this[idx] = value }

        assertEquals(size, buffer1.size)
        assertEquals(size, buffer2.size)
        // With high probability, two random arrays should not be equal
        assertNotEquals(buffer1, buffer2)
    }

    @Test
    fun testExportLongsLengthAndRandomness() {
        val size = 10
        val buffer1 = LongArray(size)
        val buffer2 = LongArray(size)

        SecureFeed.readLongs(buffer1, 0, size) { idx, value -> this[idx] = value }
        SecureFeed.readLongs(buffer2, 0, size) { idx, value -> this[idx] = value }

        assertEquals(size, buffer1.size)
        assertEquals(size, buffer2.size)
        // With high probability, two random arrays should not be equal
        assertNotEquals(buffer1, buffer2)
    }

    @Test
    fun testHealthCheck() {
        try {
            SecureFeed.securityHealthCheck()
            RandomBits.securityHealthCheck(SecureFeed)
        } catch (_: IllegalStateException) {
            assertFalse(true)
        }
    }

    @Test
    fun testTotalBits() {
        SecureFeed.nextBits(21)
        assertNotEquals(0, SecureFeed.totalBits)
    }
}