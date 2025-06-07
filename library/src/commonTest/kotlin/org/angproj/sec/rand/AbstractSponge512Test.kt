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

class AbstractSponge512Test {

    val empty = byteArrayOf(
        -84, -55, -78, 54, -92, 72, -31, 79, 20, -75, -112, 16, -75, -108, 39, -44, -26, 106,
        -103, -125, -123, -68, -7, -17, -14, 99, 66, -77, -80, -116, -105, 70, -108, 107, -83,
        55, -89, -48, 58, 71, -106, -44, -50, 88, -24, -2, -65, -24, 79, -85, -52, 33, -63,
        -90, -54, 33, -10, 77, -53, -107, -80, 20, 122, 88
    )

    @Test
    fun testSqueeze() {
        val hash = Hash512()
        val digest = hash.digest()

        assertContentEquals(digest, empty)
    }
}