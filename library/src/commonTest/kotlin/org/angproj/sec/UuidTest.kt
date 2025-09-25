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

import kotlin.math.E
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals

class UuidTest {

    @Test
    fun showSubMilliSecEntropy() {
        val microPart: Double = PI / E / 1_200_000
        repeat(100) {
            val count: Double = microPart * (it + 2)
            //println(count.toBits().toString(16))
        }
    }

    @Test
    fun testUuidNil() {
        assertEquals(Uuid.nil.upper, 0L)
        assertEquals(Uuid.nil.lower, 0L)
        assertEquals(Uuid.nil.toString(), "00000000-0000-0000-0000-000000000000")
    }

    @Test
    fun testUuidOmni() {
        assertEquals(Uuid.omni.upper, ULong.MAX_VALUE.toLong())
        assertEquals(Uuid.omni.lower, ULong.MAX_VALUE.toLong())
        assertEquals(Uuid.omni.toString(), "ffffffff-ffff-ffff-ffff-ffffffffffff")
    }
}