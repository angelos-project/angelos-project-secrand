/**
 * Copyright (c) 2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
import kotlin.math.max
import kotlin.time.TimeMark
import kotlin.time.TimeSource


/**
 * Entropy generates real True and conditioned natural entropy.
 * The entropy is taken from nanosecond fluctuations in the system clock,
 * in comparison to the number of clock cycles used which is not binary
 * but analogous. And thereby rounds the nanoseconds in an unpredictable way.
 * Thereby it is true random, however the entropy is conditioned to also be
 * natural, that is clearing Monte Carlo testing closing in on real PI.
 */
public object Entropy: ExportOctetLong, ExportOctetByte {

    /**
     * EntropyState holds the state of the entropy source, including the start time and the current entropy value.
     *
     * @property start The time when the entropy state was initialized.
     * @property entropy The current entropy value, which is updated based on the elapsed time.
     */
    public data class EntropyState(
        val start: TimeMark,
        var entropy: Long
    )

    /**
     * The entropy value is initialized based on a constant IV and the elapsed time since the start.
     *
     * @return An instance of [EntropyState] containing the start time and initial entropy value.
     */
    private fun initializeEntropy(): EntropyState {
        return EntropyState(
            start = TimeSource.Monotonic.markNow(),
            entropy = InitializationVector.IV_3AC5.iv * TimeSource.Monotonic.markNow().elapsedNow().inWholeNanoseconds
        )
    }

    private fun entropyRound(state: EntropyState) {
        state.entropy = ((-state.entropy.inv() * 5) xor state.start.elapsedNow().inWholeNanoseconds).rotateLeft(32)
    }

    private fun readLongEntropy(size: Int, state: EntropyState): Long {
        var data: Long = 0
        repeat(max(size, 8)) { _ ->
            entropyRound(state)
            data = (data shl 8) or (state.entropy and 0xFF)
        }
        return data
    }

    /**
     * Generates real time-gated entropy for a LongArray.
     *
     * @param data The LongArray to be filled with time-gated entropy.
     */
    override fun <E> exportLongs(data: E, offset: Int, length: Int, writeOctet: E.(Int, Long) -> Unit) {
        require(length <= 128) { "To large for time-gated entropy! Max 1Kb." }
        val state = initializeEntropy()

        repeat(length) { index ->
            data.writeOctet(offset + index, readLongEntropy(8, state))
        }
    }

    /**
     * Generates real time-gated entropy for a ByteArray.
     *
     * @param data The ByteArray to be filled with time-gated entropy.
     */
    override fun <E> exportBytes(data: E, offset: Int, length: Int, writeOctet: E.(index: Int, value: Byte) -> Unit) {
        require(length <= 1024) { "To large for time-gated entropy! Max 1Kb." }

        val state = initializeEntropy()

        repeat(length) { index ->
            entropyRound(state)
            data.writeOctet(offset + index, state.entropy.toByte())
        }
    }
}