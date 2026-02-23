/**
 * Copyright (c) 2024-2025 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
import org.angproj.sec.util.WriteOctet
import org.angproj.sec.util.ceilDiv
import kotlin.math.*
import kotlin.time.TimeSource


/**
 * JitterEntropy provides true random number generation based on timing jitter.
 *
 * This implementation leverages the inherent unpredictability of timing variations in system operations
 * to generate entropy. It uses a monotonic clock to measure elapsed time and derives randomness from
 * the nanosecond and microsecond components of these measurements. The entropy is further processed using
 * trigonometric functions and bitwise operations to enhance randomness.
 *
 * Usage:
 * - Call [readLongs] to fill a data structure with random [Long] values.
 * - Call [readBytes] to fill a data structure with random [Byte] values.
 *
 * Limitations:
 * - The maximum length for [readLongs] is 128 (1KB) to prevent excessive computation time.
 * - The maximum length for [readBytes] is 1024 (1KB) for the same reason.
 */
public object JitterEntropy {

    /**
     * Internal state for tracking timing measurements and generating jitter-based entropy.
     */
    public class JitterEntropyState : RandomBits {
        // Monotonic clock mark to measure elapsed time for entropy generation
        private val start = TimeSource.Monotonic.markNow()
        private var count = 0

        /**
         * Generates a true random [Int] with the specified number of bits (1 to 32) based on timing jitter.
         *
         * @param bits The number of random bits to generate (1–32).
         * @return A pseudo-random [Int] containing the requested number of bits.
         * @throws IllegalArgumentException If [bits] is not in the range 1–32.
         */
        override fun nextBits(bits: Int): Int {
            require(bits in 1..32) { "Bits must be between 1 and 32" }

            // Measure elapsed time since the start mark
            val recent = start.elapsedNow()
            // Derive entropy from nanosecond and microsecond timing jitter
            val nano: Double = 1.0 / recent.inWholeNanoseconds
            val micro: Double = 1.0 - ((count++).toDouble() / recent.inWholeMicroseconds)

            // Apply trigonometric functions to introduce non-linearity
            val nanoBits: Long = sin(nano).toRawBits()
            val microBits: Long = cos(micro).toRawBits()
            val comboBits: Long = atan2(nano, micro).toRawBits()

            // Trim bits to reduce bias and increase entropy
            val trimmedNanoBits: Long = nanoBits shl nanoBits.countLeadingZeroBits()
            val trimmedMicroBits: Long = microBits ushr microBits.countTrailingZeroBits()

            // Mix bits using XOR and a left rotation for better distribution
            val mixedBits: Long = (trimmedNanoBits xor trimmedMicroBits) xor comboBits.rotateLeft(41)

            // Combine high and low 32 bits and extract the requested number of bits
            return RandomBits.compactBitEntropy(bits, mixedBits)
        }
    }

    /**
     * Exports true random [Long] values to the provided data structure.
     * Generates up to 128 [Long] values (1024 bytes) to prevent excessive computation.
     *
     * @param data The target data structure to write to.
     * @param offset The starting index in the data structure.
     * @param length The number of [Long] values to generate (max 128).
     * @param writeOctet The function to write a [Long] value at a specific index.
     * @throws IllegalArgumentException If [length] exceeds 128.
     */
    public fun <E> readLongs(data: E, offset: Int, length: Int, writeOctet: WriteOctet<E, Long>) {
        require(length <= 128) { "Too large for time-gated entropy! Max 1Kb." }

        val state = JitterEntropyState()

        repeat(length) { index ->
            data.writeOctet(offset + index, RandomBits.nextBitsToLong { state.nextBits(it) })
        }
    }

    /**
     * Exports true random [Byte] values to the provided data structure.
     * Generates up to 1024 [Byte] values to prevent excessive computation.
     *
     * @param data The target data structure to write to.
     * @param offset The starting index in the data structure.
     * @param length The number of [Byte] values to generate (max 1024).
     * @param writeOctet The function to write a [Byte] value at a specific index.
     * @throws IllegalArgumentException If [length] exceeds 1024.
     */
    public fun <E> readBytes(data: E, offset: Int, length: Int, writeOctet: WriteOctet<E, Byte>) {
        require(length <= 1024) { "Too large for time-gated entropy! Max 1Kb." }

        val state = JitterEntropyState()

        var pos = 0
        repeat(length.ceilDiv(TypeSize.longSize)) { _ ->
            val bytes = min(TypeSize.longSize, length - pos)
            var entropy = RandomBits.nextBitsToLong { state.nextBits(it) }
            repeat(bytes) {
                data.writeOctet(offset + pos++, entropy.toByte())
                entropy = entropy ushr TypeSize.byteBits
            }
        }
    }
}