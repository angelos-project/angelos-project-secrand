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
package org.anhproj.sec.rand

import kotlin.test.Test
import kotlin.test.assertContentEquals

class TestAbstractSponge256 {

    val empty = byteArrayOf(
        88, 99, 17, -16, 127, 111, 73, -16, -40, 70, -70, 68, -128, -100, 85, 71, -120, 4,
        -117, -112, 94, -107, -52, 111, -4, -121, 21, -107, -57, -66, 111, -108
    )

    @Test
    fun testSqueeze() {
        val hash = Hash256()
        val digest = hash.digest()

        assertContentEquals(digest, empty)
    }
}