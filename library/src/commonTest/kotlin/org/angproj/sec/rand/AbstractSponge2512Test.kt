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

class AbstractSponge2512Test {


    class Hash2512 : Hash<AbstractSponge2512>(object : AbstractSponge2512() {})


    val empty = byteArrayOf(
        -94, 39, -58, 56, -21, 21, 70, -31, 116, -99, -54, -123, -123, -90, 46, 120, -121, 92,
        122, -126, 108, 9, 20, 34, 46, -63, 4, -101, 86, 82, 20, 94, -38, -125, 108, -57, 55, -19,
        73, -124, 111, 62, -110, -108, -12, 54, -66, 52, 30, 112, -115, -81, -57, 79, 3, 68, 121,
        -100, 70, 22, -31, 93, 87, 53
    )

    @Test
    fun testSqueeze() {
        val hash = Hash2512()
        val digest = hash.digest()

        assertContentEquals(digest, empty)
    }
}