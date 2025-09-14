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

class AbstractSponge2256Test {


    class Hash256 : AbstractSponge2256(), Hash {
        init {
            scramble()
        }
    }

    val empty = byteArrayOf(
        31, 16, 115, 3, -8, 25, -121, 113, -63, 33, 40, 64, -51, -97, -114, 35, 50, 78, 24, 11, 111,
        -92, 104, 18, 61, -62, 10, 113, -64, 99, 114, -113
    )

    @Test
    fun testSqueeze() {
        val hash = Hash256()
        val digest = hash.digest()

        assertContentEquals(digest, empty)
    }
}