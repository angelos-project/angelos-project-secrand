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
import kotlin.math.E
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class UuidTest {

    @Test
    @Suppress("unused")
    fun showSubMilliSecEntropy() {
        val microPart: Double = PI / E / 1_200_000
        repeat(20) {
            val count: Double = microPart * (it + 2)
            assertEquals(64-count.toRawBits().countLeadingZeroBits(), 62)
        }
    }

    @Test
    fun testUuid() {
        repeat(10) {
            val uuid = Uuid.uuid()
            assertNotEquals(0, uuid.lower)
            assertNotEquals(0, uuid.upper)
        }
    }

    @Test
    fun testUuid4() {
        repeat(10) {
            val uuid = Uuid.uuid4()
            assertEquals(4, uuid.version)
            assertEquals(2, uuid.variant)
        }
    }

    @Test
    fun testUuidNil() {
        assertEquals(Uuid.nil.upper, 0L)
        assertEquals(Uuid.nil.lower, 0L)
        assertEquals(Uuid.nil.toString(), "00000000-0000-0000-0000-000000000000")
    }

    @Test
    fun testUuidMax() {
        assertEquals(Uuid.max.upper, ULong.MAX_VALUE.toLong())
        assertEquals(Uuid.max.lower, ULong.MAX_VALUE.toLong())
        assertEquals(Uuid.max.toString(), "ffffffff-ffff-ffff-ffff-ffffffffffff")
    }

    @Test
    fun testRandomBits() {
        val error = RandomBits { 0x11223344 }

        assertEquals("11223344-1122-3344-1122-334411223344",Uuid(error).toString())
    }

    @Test
    fun testDoubleLong() {
        val error = 0x1122334455667788

        assertEquals("11223344-5566-7788-1122-334455667788",Uuid(error, error).toString())
    }
}