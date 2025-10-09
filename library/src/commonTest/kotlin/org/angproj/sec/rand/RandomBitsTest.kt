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
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RandomBitsTest {

    val staticRand = 0x11223344

    @Test
    fun testNextBitsToLong() {
        assertEquals(0x1122334411223344, RandomBits.nextBitsToLong { staticRand })
    }

    @Test
    fun testCompactBitEntropy() {
        assertEquals(0x1, RandomBits.compactBitEntropy(4, staticRand.toLong()))
        assertEquals(0x11, RandomBits.compactBitEntropy(8, staticRand.toLong()))
        assertEquals(0x112, RandomBits.compactBitEntropy(12, staticRand.toLong()))
        assertEquals(0x1122, RandomBits.compactBitEntropy(16, staticRand.toLong()))
        assertEquals(0x11223, RandomBits.compactBitEntropy(20, staticRand.toLong()))
        assertEquals(0x112233, RandomBits.compactBitEntropy(24, staticRand.toLong()))
        assertEquals(0x1122334, RandomBits.compactBitEntropy(28, staticRand.toLong()))
        assertEquals(0x11223344, RandomBits.compactBitEntropy(32, staticRand.toLong()))

        // Same random value twice should zero out using XOR
        assertEquals(0, RandomBits.compactBitEntropy(32, RandomBits.nextBitsToLong { staticRand }))
    }

    @Test
    fun testSecurityHealthCheck() {
        assertFailsWith<IllegalStateException> {
            RandomBits.securityHealthCheck { staticRand }
        }
    }
}