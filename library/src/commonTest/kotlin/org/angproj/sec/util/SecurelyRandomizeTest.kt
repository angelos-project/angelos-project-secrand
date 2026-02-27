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
package org.angproj.sec.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SecurelyRandomizeTest {

    @Test
    fun testSecurelyRandomizeByteArray() {
        val size = 53
        val buffer1 = ByteArray(size)
        val buffer2 = ByteArray(size)

        buffer1.securelyRandomize()
        buffer2.securelyRandomize()

        assertEquals(size, buffer1.size)
        assertEquals(size, buffer2.size)
        // With high probability, two random arrays should not be equal
        assertNotEquals(buffer1.toList(), buffer2.toList())
    }

    @Test
    fun testSecurelyEntropizeByteArray() {
        val size = 128
        val buffer1 = ByteArray(size)
        val buffer2 = ByteArray(size)

        buffer1.securelyEntropize()
        buffer2.securelyEntropize()

        assertEquals(size, buffer1.size)
        assertEquals(size, buffer2.size)
        // With high probability, two random arrays should not be equal
        assertNotEquals(buffer1.toList(), buffer2.toList())
    }

    @Test
    fun testSecurelyRandomizeSmallSizes() {
        repeat(32) {
            val array = ByteArray(it+1).also { it.securelyRandomize() }
            assertNotEquals(array.sum(), 0)
        }
    }

    @Test
    fun testSecurelyRandomizeZeroSize() {
        try {
            ByteArray(0).securelyRandomize()
        } catch (_: Exception) {
            assertTrue(false)
        }
    }
}