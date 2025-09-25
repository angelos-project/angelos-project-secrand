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

import org.angproj.sec.Uuid
import org.angproj.sec.util.toUnitFraction
import kotlin.math.E
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals


class InitializationVectorTest {

    /**
     * Predefined list of atoms used to generate combinations.
     * Each atom is represented as a 4-bit integer. And they are:
     * 0x3 (0011), 0x5 (0101), 0x6 (0110), 0x9 (1001), 0xA (1010), 0xC (1100)
     * The property of each atom is that it has exactly two bits set to 1, and two bits set to zero.
     * This ensures a balanced distribution of bits in the combinations.
     */
    val atoms = listOf(0x3,0x5,0x6,0x9,0xA,0xC)

    /**
     * Generates all possible combinations of four distinct atoms from the predefined list of atoms.
     * Each combination is represented as a 64-bit long integer, where each atom occupies 4 bits.
     * The 16-bit pattern formed by the four atoms is repeated four times to fill the 64-bit long integer.
     * An atom can only be used once in each combination, and the order of atoms matters.
     *
     * @return A list of all possible 64-bit long integer combinations of four distinct atoms.
     */
     fun allCombinations(): List<Long> {
        val combinations = mutableListOf<Long>()
        for (i in atoms.indices) {
            for (j in atoms.indices) {
                if (j == i) continue
                for (k in atoms.indices) {
                    if (k == i || k == j) continue
                    for (l in atoms.indices) {
                        if (l == i || l == j || l == k) continue
                        val combination = (atoms[i] shl 12) or (atoms[j] shl 8) or (atoms[k] shl 4) or atoms[l]
                        val longCombination = (combination.toLong() shl 48) or
                                (combination.toLong() shl 32) or
                                (combination.toLong() shl 16) or
                                combination.toLong()
                        combinations.add(longCombination)
                    }
                }
            }
        }
        return combinations
    }

    @Test
    fun testAllIv() {
        val combinations = allCombinations()
        repeat(InitializationVector.entries.size) {
            assertEquals(InitializationVector.entries[it].iv, combinations[it])
        }
    }
}
