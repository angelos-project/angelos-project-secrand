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
import org.angproj.sec.util.TypeSize
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertEquals


class GarbageGarblerTest {

    @Test
    fun testNotInitialized() {
        val garbler = GarbageGarbler()

        assertFalse { garbler.isInitialized }
    }

    @Test
    fun testInitialized() {
        val garbler = GarbageGarbler()

        garbler.reseed(Fakes.safeSecRand())

        assertTrue { garbler.isInitialized }
    }

    @Test
    fun testRemainingBytes() {
        val garbler = GarbageGarbler()

        garbler.reseed(Fakes.safeSecRand())
        val remaining = garbler.remainingBytes
        garbler.readLong()

        assertEquals(remaining - TypeSize.longSize, garbler.remainingBytes)
    }

    @Test
    fun testReadByteRange() {
        val garbler = GarbageGarbler()
        garbler.reseed(Fakes.safeSecRand())

        val value = garbler.readByte()

        assertTrue(value in Byte.MIN_VALUE..Byte.MAX_VALUE)
    }

    @Test
    fun testReadShortRange() {
        val garbler = GarbageGarbler()
        garbler.reseed(Fakes.safeSecRand())

        val value = garbler.readShort()

        assertTrue(value in Short.MIN_VALUE..Short.MAX_VALUE)
    }

    @Test
    fun testReadIntRange() {
        val garbler = GarbageGarbler()
        garbler.reseed(Fakes.safeSecRand())

        val value = garbler.readInt()

        assertTrue(value in Int.MIN_VALUE..Int.MAX_VALUE)
    }

    @Test
    fun testReadBytes() {
        val buffer = ByteArray(1024)
        val garbler = GarbageGarbler()
        garbler.reseed(Fakes.safeSecRand())

        garbler.readBytes(buffer)

        assertTrue { HealthCheck.healthCheck { analyzeByteArray(buffer) } }
    }
}