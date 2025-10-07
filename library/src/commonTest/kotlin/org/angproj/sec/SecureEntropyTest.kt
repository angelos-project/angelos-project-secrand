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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class SecureEntropyTest {

    @Test
    fun testExportBytesLengthAndRandomness() {
        val size = 34
        val buffer1 = ByteArray(size)
        val buffer2 = ByteArray(size)

        SecureEntropy.readBytes(buffer1, 0, size) { idx, value -> this[idx] = value }
        SecureEntropy.readBytes(buffer2, 0, size) { idx, value -> this[idx] = value }

        assertEquals(size, buffer1.size)
        assertEquals(size, buffer2.size)
        // With high probability, two random arrays should not be equal
        assertNotEquals(buffer1.toList(), buffer2.toList())
    }

    @Test
    fun testExportLongsLengthAndRandomness() {
        val size = 10
        val buffer1 = LongArray(size)
        val buffer2 = LongArray(size)

        SecureEntropy.readLongs(buffer1, 0, size) { idx, value -> this[idx] = value }
        SecureEntropy.readLongs(buffer2, 0, size) { idx, value -> this[idx] = value }

        assertEquals(size, buffer1.size)
        assertEquals(size, buffer2.size)
        // With high probability, two random arrays should not be equal
        assertNotEquals(buffer1.toList(), buffer2.toList())
    }

    @Test
    fun testHealthCheck() {
        try {
            SecureEntropy.securityHealthCheck()
        } catch (_: IllegalStateException) {
            assertFalse(true)
        }
    }
}