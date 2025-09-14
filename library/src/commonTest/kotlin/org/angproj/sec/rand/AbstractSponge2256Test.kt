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


    class Hash2256 : Hash<AbstractSponge2256>(object : AbstractSponge2256() {})


    val empty = byteArrayOf(
        115, -123, -27, 107, -6, 25, -121, 113, 17, 39, 48, 66, -51, -97, -114, 35, -116, -124,
        18, 10, 111, -92, 104, 18, 76, -68, -9, -116, -63, 99, 114, -113
    )

    @Test
    fun testSqueeze() {
        val hash = Hash2256()
        val digest = hash.digest()

        assertContentEquals(digest, empty)
    }
}