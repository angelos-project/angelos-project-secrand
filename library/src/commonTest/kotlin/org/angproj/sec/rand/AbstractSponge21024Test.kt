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

class AbstractSponge21024Test {


    class Hash1024 : AbstractSponge21024(), Hash {
        init {
            scramble()
        }
    }

    val empty = byteArrayOf(
        -101, 6, 34, -51, 120, -90, 115, 11, -97, 120, 97, -75, 83, -31, -47, 43, -59, 85, 70,
        21, 117, -24, -50, 96, 47, -21, 48, -70, -22, 6, -71, -114, 93, -20, -72, -2, 34, 72,
        -77, 55, -40, 78, 127, -123, -116, 83, -5, -102, 87, 51, -28, 55, 29, -16, -100, -33,
        -123, 64, 26, 42, 34, -45, -34, -13, 45, 32, 49, 127, -110, 67, 64, -74, -124, -118, -23,
        -8, 71, 86, -80, -71, 41, 12, 70, 95, -118, 40, 8, -58, -65, -20, 102, -50, 97, -98, 64,
        -60, -90, -35, 55, 10, 58, 107, 84, 66, 15, -118, 63, -29, 28, -54, -13, 71, -31, -73, -61,
        -5, 85, -20, 22, -71, -106, 62, 76, 84, -7, -103, -1, -27
    )

    @Test
    fun testSqueeze() {
        val hash = Hash1024()
        val digest = hash.digest()

        assertContentEquals(digest, empty)
    }
}