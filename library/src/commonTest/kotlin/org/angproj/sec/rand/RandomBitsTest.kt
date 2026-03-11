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


class RandomBitsTest {

    val staticDescending: Long = 0xFEDCBA9876543210uL.toLong()

    val staticAscending: Long = 0x0123456789ABCDEFuL.toLong()

    val staticRandAscending = 0x12345678

    val staticRandDescending = 0xFEDCBA98u

    @Test
    fun testAscendingConvert() {
        val ascending: ULong = 0x0123456789ABCDEFuL

        val longVersion = ascending.toLong()

        assertEquals(ascending, longVersion.toULong())
        assertEquals(longVersion, staticAscending)
    }

    @Test
    fun testDescendingConvert() {
        val descending: ULong = 0xFEDCBA9876543210uL

        val longVersion = descending.toLong()

        assertEquals(descending, longVersion.toULong())
        assertEquals(longVersion, staticDescending)
    }

    @Test
    fun testNextBitsToLong() {
        assertEquals(0xFFFF_FFFF_FFFF_FFFFuL.toLong(), RandomBits.nextBitsToLong { 0xFFFF_FFFF.toInt() })
        assertEquals(0x0, RandomBits.nextBitsToLong { 0 })
        assertEquals(0x1234567812345678, RandomBits.nextBitsToLong { staticRandAscending })
    }

    @Test
    fun testCompactBitEntropySelfNullifying() {
        // Same random value twice should zero out using XOR
        assertEquals(0, RandomBits.compactBitEntropy(32, RandomBits.nextBitsToLong { staticRandAscending }))
        assertEquals(0, RandomBits.compactBitEntropy(32, RandomBits.nextBitsToLong { staticRandDescending.toInt() }))
    }

    @Test
    fun testCompactBitEntropyAscending() {
        assertEquals(0x1, RandomBits.compactBitEntropy(4, staticRandAscending.toLong()))
        assertEquals(0x12, RandomBits.compactBitEntropy(8, staticRandAscending.toLong()))
        assertEquals(0x123, RandomBits.compactBitEntropy(12, staticRandAscending.toLong()))
        assertEquals(0x1234, RandomBits.compactBitEntropy(16, staticRandAscending.toLong()))
        assertEquals(0x12345, RandomBits.compactBitEntropy(20, staticRandAscending.toLong()))
        assertEquals(0x123456, RandomBits.compactBitEntropy(24, staticRandAscending.toLong()))
        assertEquals(0x1234567, RandomBits.compactBitEntropy(28, staticRandAscending.toLong()))
        assertEquals(0x12345678, RandomBits.compactBitEntropy(32, staticRandAscending.toLong()))
    }

    @Test
    fun testCompactBitEntropyDescending() {
        assertEquals(0xF, RandomBits.compactBitEntropy(4, staticRandDescending.toLong()))
        assertEquals(0xFE, RandomBits.compactBitEntropy(8, staticRandDescending.toLong()))
        assertEquals(0xFED, RandomBits.compactBitEntropy(12, staticRandDescending.toLong()))
        assertEquals(0xFEDC, RandomBits.compactBitEntropy(16, staticRandDescending.toLong()))
        assertEquals(0xFEDCB, RandomBits.compactBitEntropy(20, staticRandDescending.toLong()))
        assertEquals(0xFEDCBA, RandomBits.compactBitEntropy(24, staticRandDescending.toLong()))
        assertEquals(0xFEDCBA9, RandomBits.compactBitEntropy(28, staticRandDescending.toLong()))
        assertEquals(0xFEDCBA98u, RandomBits.compactBitEntropy(32, staticRandDescending.toLong()).toUInt())
    }
}