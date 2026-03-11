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
        try {
            repeat(loops) {
                val value = SecureRandom.readByte()
                assertTrue(value in Byte.MIN_VALUE..Byte.MAX_VALUE)
            }
        } catch (_: SecureRandomException) {
            HealthCheck.doubleHealthCheckWithSample { debug -> analyzeBits({ SecureRandom.readInt() }, debug) }
        }
    }

    @Test
    fun testReadShortRange() {
        try {
            repeat(loops) {
                val value = SecureRandom.readShort()
                assertTrue(value in Short.MIN_VALUE..Short.MAX_VALUE)
            }
        } catch (_: SecureRandomException) {
            HealthCheck.doubleHealthCheckWithSample { debug -> analyzeBits({ SecureRandom.readInt() }, debug) }
        }
    }

    @Test
    fun testReadIntRange() {
        try {
            repeat(loops) {
                val value = SecureRandom.readInt()
                assertTrue(value in Int.MIN_VALUE..Int.MAX_VALUE)
            }
        } catch (_: SecureRandomException) {
            HealthCheck.doubleHealthCheckWithSample { debug -> analyzeBits({ SecureRandom.readInt() }, debug) }
        }
    }

    @Test
    fun testReadBytes() {
        try {
            val buffer = ByteArray(1024)

            SecureRandom.readBytes(buffer)

            assertTrue { HealthCheck.healthCheck { analyzeByteArray(buffer) } }
        } catch (_: SecureRandomException) {
            HealthCheck.doubleHealthCheckWithSample { debug -> analyzeBits({ SecureRandom.readInt() }, debug) }
        }
    }

    @Test
    fun testSecurityHealth() {
        assertTrue{ HealthCheck.doubleHealthCheckWithSample { debug -> analyzeBits( { SecureRandom.readInt() }, debug)  } }
        assertFalse{ HealthCheck.doubleHealthCheckWithSample { debug -> analyzeBits({ (SecureRandom.readInt() and 0x1f_ff_ff_ff) }, debug) } }
        // Statistically it looks like 3 out of 32 being distorted leads to double failures.
    }
}