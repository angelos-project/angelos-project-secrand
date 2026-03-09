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

import org.angproj.sec.stat.securityHealthCheck
import org.angproj.sec.util.HealthCheck
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SecureEntropyTest {

    /*@Test
    fun testExportBytesLengthAndRandomness() {
        val size = 34
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
        val size = 10
        val buffer1 = LongArray(size)
        val buffer2 = LongArray(size)

        SecureEntropy.exportLongs(buffer1, 0, size) { idx, value -> this[idx] = value }
        SecureEntropy.exportLongs(buffer2, 0, size) { idx, value -> this[idx] = value }

        assertEquals(size, buffer1.size)
        assertEquals(size, buffer2.size)
        // With high probability, two random arrays should not be equal
        assertNotEquals(buffer1.toList(), buffer2.toList())
    }

    @Test
    fun testTotalBits() {
        SecureEntropy.exportLongs(LongArray(2), 0, 2) { idx, value -> this[idx] = value }
        assertNotEquals(0, SecureEntropy.totalBits)
    }*/

    @Test
    fun testSecurityHealth() {
        assertTrue{
            HealthCheck.doubleHealthCheckWithSample { debug ->
                analyzeSecurity(SecureEntropy, debug)
            }
        }
    }
}