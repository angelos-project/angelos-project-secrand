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

import org.angproj.sec.util.HealthCheck
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class SecureRandomTest {

    val loops: Int = 1

    @Test
    fun testReadByteRange() {
        repeat(loops) {
            val value = SecureRandom.readByte()
            assertTrue(value in Byte.MIN_VALUE..Byte.MAX_VALUE)
        }
    }

    @Test
    fun testReadShortRange() {
        repeat(loops) {
            val value = SecureRandom.readShort()
            assertTrue(value in Short.MIN_VALUE..Short.MAX_VALUE)
        }
    }

    @Test
    fun testReadIntRange() {
        repeat(loops) {
            val value = SecureRandom.readInt()
            assertTrue(value in Int.MIN_VALUE..Int.MAX_VALUE)
        }
    }

    @Test
    fun testReadBytes() {
        val buffer = ByteArray(1024)

        SecureRandom.readBytes(buffer)

        assertTrue { HealthCheck.singleHealthCheckDebug(buffer) }
    }

    @Test
    fun testSecurityHealth() {
        assertTrue{ HealthCheck.doubleHealthCheckDebug { debug -> analyzeBits( { SecureRandom.readInt() }, debug)  } }
        assertFalse{ HealthCheck.doubleHealthCheckDebug { debug -> analyzeBits({ (SecureRandom.readInt() and 0x1f_ff_ff_ff) }, debug) } }
        // Statistically it looks like 3 out of 32 being distorted leads to double failures.
    }
}