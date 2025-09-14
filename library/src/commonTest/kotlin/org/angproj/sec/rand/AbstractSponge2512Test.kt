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


    class Hash512 : AbstractSponge2512(), Hash {
        init {
            scramble()
        }
    }

    val empty = byteArrayOf(
        74, 116, -76, -16, -1, -58, 35, -72, -63, -81, -75, -2, -124, -83, -89, 22, 24, -25, 62,
        -32, -48, -30, -76, -61, -54, -104, 66, -55, 85, 104, -79, -89, 69, 112, -95, 78, 90,
        -106, -30, 35, -83, -48, 44, -16, -110, 61, -84, 119, 8, 104, -82, -105, 14, -84, -100,
        -76, 96, 53, -110, 32, 116, -32, 101, -121
    )

    @Test
    fun testSqueeze() {
        val hash = Hash512()
        val digest = hash.digest()

        assertContentEquals(digest, empty)
    }
}