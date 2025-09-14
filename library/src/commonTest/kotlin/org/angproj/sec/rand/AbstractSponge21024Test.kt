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


    class Hash21024 : Hash<AbstractSponge21024>(object : AbstractSponge21024() {})


    val empty = byteArrayOf(
        110, -55, 28, -3, -82, -57, 44, -68, 82, 31, 66, -71, 15, -39, -95, -21, 106, -23, 16, 116, 67,
        -23, -114, -111, 59, 35, -38, 12, -82, 47, -17, 14, 85, -42, -34, 23, -33, 7, -111, -43, 12, 86,
        -19, -102, 17, -27, 94, 28, -94, -14, 49, 39, 88, 71, 106, -36, 82, 85, 4, -56, -87, -111, -87,
        111, 73, -74, -69, -88, -1, 43, -39, -1, -114, 46, 2, -6, -72, 30, -1, 101, 127, 85, 24, 89, -85,
        -41, 100, -126, -36, 110, 44, -104, 34, 5, -79, -65, 54, 97, -25, -96, 97, 109, 109, -42, -41,
        -88, -73, -53, 23, 63, 19, -37, 24, 45, -82, 4, 104, -43, 117, -27, -90, 64, 91, 110, -53, 112, 49, -77
    )

    @Test
    fun testSqueeze() {
        val hash = Hash21024()
        val digest = hash.digest()

        assertContentEquals(digest, empty)
    }
}