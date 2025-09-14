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

/**
 * Interface for generating random bits and performing bit manipulations.
 *
 * Implementations should provide a method to retrieve a specified number of random bits.
 * The companion object includes utility functions for folding and reducing bit values,
 * which are useful for extracting and compressing randomness from larger values.
 *
 * Methods:
 * - `getNextBits(bits)`: Returns an `Int` containing the requested number of random bits.
 *
 * Companion object utilities:
 * - `foldBits(value)`: Folds a `Long` value into an `Int` by XOR-ing its upper and lower 32 bits.
 * - `reduceBits(bits, value)`: Reduces an `Int` to the specified number of bits by shifting.
 */
public interface Randomizer {

    /**
     * Returns an `Int` containing the specified number of random bits.
     *
     * @param bits Number of bits to retrieve (up to 32).
     * @return Random bits as an `Int`.
     */
    public fun getNextBits(bits: Int): Int

    public companion object {
        /**
         * Folds a `Long` value into an `Int` by XOR-ing its upper and lower 32 bits.
         *
         * @param value The `Long` value to fold.
         * @return Folded `Int` value.
         */
        public inline fun<reified R: Any> foldBits(value: Long): Int {
            return ((value ushr 32).toInt() xor (value and 0xffffffff).toInt())
        }

        /**
         * Reduces an `Int` to the specified number of bits by shifting.
         *
         * @param bits Number of bits to keep.
         * @param value The `Int` value to reduce.
         * @return Reduced `Int` value.
         */
        public inline fun<reified R: Any> reduceBits(bits: Int, value: Int): Int {
            return value ushr (32 - bits)
        }
    }
}