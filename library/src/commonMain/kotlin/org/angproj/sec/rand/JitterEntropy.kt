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
 * Entropy generates real True and conditioned natural entropy.
 * The entropy is taken from nanosecond fluctuations in the system clock,
 * in comparison to the number of clock cycles used which is not binary
 * but analogous. And thereby rounds the nanoseconds in an unpredictable way.
 * Thereby it is true random, however the entropy is conditioned to also be
 * natural, that is clearing Monte Carlo testing closing in on real PI.
 */
public object JitterEntropy: ExportOctetLong, ExportOctetByte {

    public class JitterEntropyState {
        private val start = TimeSource.Monotonic.markNow();

        public fun nextJitter(bits: Int): Int {
            require(bits in 1..32)
            val recent = start.elapsedNow()
            val nano: Double = 1.0 / recent.inWholeNanoseconds
            val micro: Double = 1.0 - (1.0 / recent.inWholeMicroseconds)

            val nanoBits: Long = sin(nano).toRawBits()
            val microBits: Long = cos(micro).toRawBits()
            val comboBits: Long = atan2(nano, micro).toRawBits()

            val trimmedNanoBits: Long = nanoBits shl nanoBits.countLeadingZeroBits()
            val trimmedMicroBits: Long = microBits ushr microBits.countTrailingZeroBits()

            val mixedBits: Long = (trimmedNanoBits xor trimmedMicroBits) xor comboBits.rotateLeft(41)

            return ((mixedBits ushr 32).toInt() xor (mixedBits and 0xffffffff).toInt()) ushr (32 - bits)
        }
    }

    private val state = JitterEntropyState()


    /**
     * Generates real time-gated entropy for a LongArray.
     *
     * @param data The LongArray to be filled with time-gated entropy.
     */
    override fun <E> exportLongs(data: E, offset: Int, length: Int, writeOctet: E.(Int, Long) -> Unit) {
        require(length <= 128) { "To large for time-gated entropy! Max 1Kb." }
        var entropy: Long = 0

        // Warmup
        repeat(16) {
            entropy = entropy shl 8 xor state.nextJitter(32).toLong()
        }

        repeat(length) { index ->
            repeat(8) {
                entropy = entropy shl 8 xor state.nextJitter(32).toLong()
            }
            data.writeOctet(offset + index, entropy)
        }
    }

    /**
     * Generates real time-gated entropy for a ByteArray.
     *
     * @param data The ByteArray to be filled with time-gated entropy.
     */
    override fun <E> exportBytes(data: E, offset: Int, length: Int, writeOctet: E.(index: Int, value: Byte) -> Unit) {
        require(length <= 1024) { "To large for time-gated entropy! Max 1Kb." }
        var entropy: Long = 0

        // Warmup
        repeat(16) {
            entropy = entropy shl 8 xor state.nextJitter(32).toLong()
        }

        repeat(length) { index ->
            entropy = entropy shl 8 xor state.nextJitter(32).toLong()
            data.writeOctet(offset + index, entropy.toByte())
        }
    }
}