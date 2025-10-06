package org.angproj.sec.rand

import org.angproj.sec.util.TypeSize

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
public fun interface RandomBits {

    /**
     * Returns an `Int` containing the specified number of random bits.
     *
     * @param bits Number of bits to retrieve (up to 32).
     * @return Random bits as an `Int`.
     */
    public fun nextBits(bits: Int): Int

    public companion object {
        public fun nextBitsToLong(randomBits: RandomBits): Long {
            return (randomBits.nextBits(TypeSize.intBits) shl TypeSize.intBits).toLong() or (randomBits.nextBits(
                TypeSize.intBits).toLong() and 0xFFFFFFFFL)
        }

        public fun compactBitEntropy(bits: Int, entropy: Long): Int {
            return ((entropy ushr 32).toInt() xor (entropy and 0xffffffff).toInt()) ushr (TypeSize.intBits - bits)
        }
    }
}