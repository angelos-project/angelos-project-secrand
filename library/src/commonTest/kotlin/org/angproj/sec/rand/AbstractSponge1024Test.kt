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

import kotlin.test.Test
import kotlin.test.assertContentEquals

class AbstractSponge1024Test {

    val empty = byteArrayOf(
        19, 21, -7, 126, 126, -49, 127, -103, 119, 63, 84, 54, -108, -46, -8, -24, 116, -108, 91, 89, 88,
        -58, 31, 10, 85, 84, 101, -53, 42, -55, -46, -52, 4, 25, 81, 44, 76, 78, 90, 2, 64, -74, 90, 81,
        -65, 63, -52, -47, 37, -39, -102, -39, 45, 115, -41, 84, 73, -111, -13, -11, -95, 97, 96, 59, 92,
        -103, 33, 103, -83, 80, -41, 74, -42, -5, 84, -30, 24, -94, -9, 26, -60, 70, 120, 66, 90, -123,
        32, 47, -51, 34, -30, -73, 77, 96, -43, -50, -46, 35, 38, -125, 119, 42, -29, -5, 83, -53, -95,
        -100, -102, 15, 2, 117, 81, -83, -77, -37, -52, -118, -9, -60, -57, -52, -71, 79, -76, 2, -65, 73
    )

    @Test
    fun testSqueeze() {
        val hash = Hash1024()
        val digest = hash.digest()

        assertContentEquals(digest, empty)
    }
}