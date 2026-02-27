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

import org.angproj.sec.SecureRandom
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HealthCheckTest {


    fun<E> leBooleanMask(any: E): Long = 1L shl (TypeSize.bitSize(any) - 1)

    @Test
    fun fixAndTrix() {
        val bitSize = TypeSize.bitSize(0x11223344L)
        val mask = 1L shl bitSize-1
        var mask2 = mask

        repeat(bitSize) {
            val mask1 = (mask ushr it)
            assertEquals(mask1, mask2)
            //println("${it+1} Mask1: ${mask1.toString(2)}, Mask2: ${mask2.toString(2)}")
            mask2 = mask2 ushr 1
        }
    }

    @Test
    fun testHealthCheck() {
        assertTrue{ HealthCheck.doubleHealthCheck({ SecureRandom.readInt() }, true) }
    }
}