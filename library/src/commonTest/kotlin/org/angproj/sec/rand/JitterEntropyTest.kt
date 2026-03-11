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

import org.angproj.sec.util.HealthCheck
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class JitterEntropyTest {

    @Test
    fun testNextBitsToMuch() {
        val state = JitterEntropy.JitterEntropyState()

        assertFailsWith<IllegalArgumentException>{
            state.nextBits(33)
        }
        assertFailsWith<IllegalArgumentException>{
            state.nextBits(-1)
        }
    }

    @Test
    fun testExportLongsToLarge() {
        assertFailsWith<IllegalArgumentException>{
            JitterEntropy.exportLongs(longArrayOf(), 0, 200) { _, _ -> }
        }
    }

    @Test
    fun testExportBytesToLarge() {
        assertFailsWith<IllegalArgumentException>{
            JitterEntropy.exportLongs(byteArrayOf(), 0, 2000) { _, _ -> }
        }
    }

    @Test
    fun testInternalRandomBits() {
        assertTrue{ HealthCheck.doubleHealthCheckWithSample{ debug -> analyzeBits(JitterEntropy.JitterEntropyState(), debug) } }
    }

    @Test
    fun testExportBytes() {
        assertTrue{ HealthCheck.doubleHealthCheckWithSample{ debug -> analyzeBytes(JitterEntropy::exportBytes, debug) } }
    }

    @Test
    fun testExportLongs() {
        assertTrue{ HealthCheck.doubleHealthCheckWithSample{ debug -> analyzeLongs(JitterEntropy::exportLongs, debug) } }
    }
}