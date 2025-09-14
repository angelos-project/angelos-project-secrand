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

import org.angproj.sec.util.ExportOctetByte
import org.angproj.sec.util.ExportOctetLong
import kotlin.math.*
import kotlin.time.TimeSource


/**
 * A singleton object for generating pseudo-random numbers based on timing jitter in a Kotlin Multiplatform (KMP) environment.
 * It implements [ExportOctetLong] and [ExportOctetByte] to export random data as [Long] or [Byte] values.
 * The entropy is derived from system monotonic clock measurements, processed through trigonometric and bitwise operations.
 */
public object JitterEntropy : ExportOctetLong, ExportOctetByte {

    /**
     * Internal state for tracking timing measurements and generating jitter-based entropy.
     */
    public class JitterEntropyState : Randomizer {
        // Monotonic clock mark to measure elapsed time for entropy generation
        private val start = TimeSource.Monotonic.markNow()

        /**
         * Generates a pseudo-random [Int] with the specified number of bits (1 to 32) based on timing jitter.
         *
         * @param bits The number of random bits to generate (1–32).
         * @return A pseudo-random [Int] containing the requested number of bits.
         * @throws IllegalArgumentException If [bits] is not in the range 1–32.
         */
        override fun getNextBits(bits: Int): Int {
            require(bits in 1..32) { "Bits must be between 1 and 32" }

            // Measure elapsed time since the start mark
            val recent = start.elapsedNow()
            // Derive entropy from nanosecond and microsecond timing jitter
            val nano: Double = 1.0 / recent.inWholeNanoseconds
            val micro: Double = 1.0 - (1.0 / recent.inWholeMicroseconds)

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
            return Randomizer.reduceBits<Unit>(bits, Randomizer.foldBits<Unit>(mixedBits))
        }
    }

    // Single instance of the entropy state
    private val state = JitterEntropyState()

    private inline fun<reified R: Any> generateEntropy(entropy: Long, loops: Int): Long {
        var data = entropy
        repeat(loops) {
            data = data shl 8 xor state.getNextBits(32).toLong()
        }
        return data
    }

    /**
     * Exports pseudo-random [Long] values to the provided data structure.
     * Generates up to 128 [Long] values (1024 bytes) to prevent excessive computation.
     *
     * @param data The target data structure to write to.
     * @param offset The starting index in the data structure.
     * @param length The number of [Long] values to generate (max 128).
     * @param writeOctet The function to write a [Long] value at a specific index.
     * @throws IllegalArgumentException If [length] exceeds 128.
     */
    override fun <E> exportLongs(data: E, offset: Int, length: Int, writeOctet: E.(Int, Long) -> Unit) {
        require(length <= 128) { "Too large for time-gated entropy! Max 1Kb." }
        var entropy: Long = 0

        // Warmup phase to stabilize the entropy pool
        entropy = generateEntropy<Unit>(entropy, 16)

        // Generate and write random Long values
        repeat(length) { index ->
            entropy = generateEntropy<Unit>(entropy, 8)
            data.writeOctet(offset + index, entropy)
        }
    }

    /**
     * Exports pseudo-random [Byte] values to the provided data structure.
     * Generates up to 1024 [Byte] values to prevent excessive computation.
     *
     * @param data The target data structure to write to.
     * @param offset The starting index in the data structure.
     * @param length The number of [Byte] values to generate (max 1024).
     * @param writeOctet The function to write a [Byte] value at a specific index.
     * @throws IllegalArgumentException If [length] exceeds 1024.
     */
    override fun <E> exportBytes(data: E, offset: Int, length: Int, writeOctet: E.(index: Int, value: Byte) -> Unit) {
        require(length <= 1024) { "Too large for time-gated entropy! Max 1Kb." }
        var entropy: Long = 0

        // Warmup phase to stabilize the entropy pool
        entropy = generateEntropy<Unit>(entropy, 16)

        // Generate and write random Byte values
        repeat(length) { index ->
            entropy = entropy shl 8 xor state.getNextBits(32).toLong()
            data.writeOctet(offset + index, entropy.toByte())
        }
    }
}