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
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals

class SecureRandomTest {

    @Test
    fun testReadByteRange() {
        repeat(1000) {
            val value = SecureRandom.readByte()
            assertTrue(value in Byte.MIN_VALUE..Byte.MAX_VALUE)
        }
    }

    @Test
    fun testReadUByteRange() {
        repeat(1000) {
            val value = SecureRandom.readUByte()
            assertTrue(value in UByte.MIN_VALUE..UByte.MAX_VALUE)
        }
    }

    @Test
    fun testReadShortRange() {
        repeat(1000) {
            val value = SecureRandom.readShort()
            assertTrue(value in Short.MIN_VALUE..Short.MAX_VALUE)
        }
    }

    @Test
    fun testReadUShortRange() {
        repeat(1000) {
            val value = SecureRandom.readUShort()
            assertTrue(value in UShort.MIN_VALUE..UShort.MAX_VALUE)
        }
    }

    @Test
    fun testReadIntRange() {
        repeat(1000) {
            val value = SecureRandom.readInt()
            assertTrue(value in Int.MIN_VALUE..Int.MAX_VALUE)
        }
    }

    @Test
    fun testReadUIntRange() {
        repeat(1000) {
            val value = SecureRandom.readUInt()
            assertTrue(value in UInt.MIN_VALUE..UInt.MAX_VALUE)
        }
    }

    @Test
    fun testReadLongRange() {
        repeat(1000) {
            val value = SecureRandom.readLong()
            assertTrue(value in Long.MIN_VALUE..Long.MAX_VALUE)
        }
    }

    @Test
    fun testReadULongRange() {
        repeat(1000) {
            val value = SecureRandom.readULong()
            assertTrue(value in ULong.MIN_VALUE..ULong.MAX_VALUE)
        }
    }

    @Test
    fun testReadFloatRange() {
        repeat(1000) {
            val value = SecureRandom.readFloat()
            assertTrue(value in 0.0f..1.0f)
        }
    }

    @Test
    fun testReadDoubleRange() {
        repeat(1000) {
            val value = SecureRandom.readDouble()
            assertTrue(value in 0.0..1.0)
        }
    }

    @Test
    fun testReadBytesRandomness() {
        val bytes1 = ByteArray(64)
        val bytes2 = ByteArray(64)
        SecureRandom.readBytes(bytes1)
        SecureRandom.readBytes(bytes2)
        // With high probability, two random arrays should not be equal
        assertNotEquals(bytes1.toList(), bytes2.toList())
    }
}