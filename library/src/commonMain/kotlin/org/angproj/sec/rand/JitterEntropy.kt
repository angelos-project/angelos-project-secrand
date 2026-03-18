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

import org.angproj.sec.SecureRandomException
import org.angproj.sec.util.Octet
import org.angproj.sec.util.TypeSize
import org.angproj.sec.util.WriteOctet
import org.angproj.sec.util.ceilDiv
import org.angproj.sec.util.ensure
import kotlin.math.*
import kotlin.time.TimeSource

/**
 * JitterEntropy is an entropy source that generates random bits based on timing jitter from the system's monotonic clock.
 * It measures elapsed time and applies trigonometric functions to derive high-quality entropy, ensuring unpredictability.
 */
public object JitterEntropy: Octet.Producer {
    
    internal class JitterEntropyState : RandomBits {
        // Monotonic clock mark to measure elapsed time for entropy generation
        private val start = TimeSource.Monotonic.markNow()
        private var count = 0

        init {
            ensure<SecureRandomException>(start.hasPassedNow()) { SecureRandomException("Time source is not ticking fast enough!!!") }
        }
        
        /**
         * Generates the next random bits using timing jitter from the monotonic clock.
         * Applies trigonometric transformations to elapsed time measurements for entropy derivation.
         *
         * @param bits the number of bits to generate, between 1 and 32.
         * @return the generated random bits as an integer.
         */
        override fun nextBits(bits: Int): Int {
            require(bits in 1..32) { "Bits must be between 1 and 32" }

            // Measure elapsed time since the start mark
            val recent = start.elapsedNow()
            // Derive entropy from nanosecond and microsecond timing jitter
            val nano: Double = 1.0 / recent.inWholeNanoseconds
            val micro: Double = 1.0 - ((++count).toDouble() / recent.inWholeMicroseconds)

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
     * Exports longs to the destination using jitter entropy.
     * Generates random longs based on timing jitter and writes them to the specified destination.
     *
     * @param dst the destination object to write to.
     * @param offset the starting offset in the destination.
     * @param length the number of longs to export (max 128).
     * @param writeOctet the function to write each long.
     */
    public override fun <E> exportLongs(dst: E, offset: Int, length: Int, writeOctet: WriteOctet<E, Long>) {
        require(length <= 128) { "Too large for time-gated entropy! Max 1Kb." }

        val state = JitterEntropyState()

        repeat(length) { index ->
            dst.writeOctet(offset + index, RandomBits.nextBitsToLong { state.nextBits(it) })
        }
    }

    /**
     * Exports bytes to the destination using jitter entropy.
     * Generates random bytes based on timing jitter and writes them to the specified destination.
     *
     * @param dst the destination object to write to.
     * @param offset the starting offset in the destination.
     * @param length the number of bytes to export (max 1024).
     * @param writeOctet the function to write each byte.
     */
    public override fun <E> exportBytes(dst: E, offset: Int, length: Int, writeOctet: WriteOctet<E, Byte>) {
        require(length <= 1024) { "Too large for time-gated entropy! Max 1Kb." }

        val state = JitterEntropyState()

        var pos = 0
        repeat(length.ceilDiv(TypeSize.longSize)) { _ ->
            val bytes = min(TypeSize.longSize, length - pos)
            var entropy = RandomBits.nextBitsToLong { state.nextBits(it) }
            repeat(bytes) {
                dst.writeOctet(offset + pos++, entropy.toByte())
                entropy = entropy ushr TypeSize.byteBits
            }
        }
    }
}