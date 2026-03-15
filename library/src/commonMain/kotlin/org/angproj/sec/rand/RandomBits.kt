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

import org.angproj.sec.util.TypeSize

/**
 * Interface for generating random bits.
 * Implementations provide a way to generate random integers of specified bit lengths.
 */
public fun interface RandomBits {

    /**
     * Generates the next random bits.
     *
     * @param bits the number of bits to generate, between 1 and 32.
     * @return the generated random bits as an integer.
     */
    public fun nextBits(bits: Int): Int

    /**
     * Companion object providing utility functions for RandomBits.
     */
    public companion object {

        /**
         * Generates a random long by combining two random integers.
         *
         * @param randomBits the RandomBits instance to use.
         * @return a random long value.
         */
        public fun nextBitsToLong(randomBits: RandomBits): Long {
            return (randomBits.nextBits(TypeSize.intBits).toLong() shl TypeSize.intBits) or (randomBits.nextBits(
                TypeSize.intBits).toLong() and 0xFFFFFFFFL)
        }

        /**
         * Compacts entropy from a long into a specified number of bits.
         *
         * @param bits the number of bits to compact to.
         * @param entropy the entropy long to compact.
         * @return the compacted entropy as an integer.
         */
        public fun compactBitEntropy(bits: Int, entropy: Long): Int {
            return ((entropy ushr 32).toInt() xor (entropy and 0xffffffff).toInt()) ushr (TypeSize.intBits - bits)
        }
    }
}