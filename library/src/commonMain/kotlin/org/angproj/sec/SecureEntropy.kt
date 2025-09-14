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
package org.angproj.sec

import org.angproj.sec.rand.AbstractSponge256
import org.angproj.sec.rand.JitterEntropy
import org.angproj.sec.rand.Randomizer
import org.angproj.sec.util.ExportOctetByte
import org.angproj.sec.util.ExportOctetLong

/**
 * SecureEntropy is a singleton object that provides a secure source of entropy
 * using a sponge construction with a size of 256 bits. It revitalizes the sponge
 * with real-time gated entropy and provides methods to read random bytes.
 */
public object SecureEntropy : ExportOctetLong, ExportOctetByte {

    private val sponge = object : AbstractSponge256(), Randomizer {
        private var position: Int = 0

        override fun getNextBits(bits: Int): Int {
            val random = squeeze(position++)

            if(position >= visibleSize) {
                round()
                position = 0
            }
            return Randomizer.reduceBits<Unit>(bits, Randomizer.foldBits<Unit>(random))
        }
    }

    init {
        revitalize()
    }

    /**
     * Revitalizes the sponge by reading real-time gated entropy and scrambling the state.
     * This method is called to ensure that the sponge has fresh entropy before reading.
     */
    private fun revitalize() {
        JitterEntropy.exportLongs(sponge, 0, sponge.visibleSize) { index, value ->
            sponge.absorb(value, index)
        }
        sponge.scramble()
    }

    private inline fun<reified R: Any> generateEntropy(entropy: Long, loops: Int): Long {
        var data = entropy
        repeat(loops) {
            data = data shl 8 xor sponge.getNextBits(32).toLong()
        }
        return data
    }

    override fun <E> exportLongs(data: E, offset: Int, length: Int, writeOctet: E.(Int, Long) -> Unit) {
        if(length <= 0) return

        revitalize()
        var entropy: Long = 0

        // Warmup phase to stabilize the entropy pool
        entropy = generateEntropy<Unit>(entropy, 16)

        // Generate and write random Long values
        repeat(length) { index ->
            entropy = generateEntropy<Unit>(entropy, 8)
            data.writeOctet(offset + index, entropy)
        }

        /*var index = 0
        var pos = offset
        revitalize()

        repeat(length) {
            data.writeOctet(pos++, sponge.squeeze(index))

            index++
            if (index >= sponge.visibleSize) {
                sponge.round()
                index = 0
            }
        }*/
    }

    /**
     * Reads random bytes into the provided data structure.
     *
     * @param data The data structure to write the random bytes into.
     * @param offset The starting position in the data structure.
     * @param length The number of bytes to read.
     * @param writeOctet A function that writes a byte at a specific index in the data structure.
     */
    override fun <E> exportBytes(data: E, offset: Int, length: Int, writeOctet: E.(index: Int, value: Byte) -> Unit) {
        if(length <= 0) return

        revitalize()
        var entropy: Long = 0

        // Warmup phase to stabilize the entropy pool
        entropy = generateEntropy<Unit>(entropy, 16)

        // Generate and write random Byte values
        repeat(length) { index ->
            entropy = entropy shl 8 xor sponge.getNextBits(32).toLong()
            data.writeOctet(offset + index, entropy.toByte())
        }

        /*var index = 0
        var pos = offset

        val loops = length / 8
        val remaining = length % 8
        revitalize()

        repeat(loops) {
            var rand = sponge.squeeze(index)

            // Little-endian conversion
            repeat(8) {
                data.writeOctet(pos++, (rand and 0xFF).toByte())
                rand = rand ushr 8
            }

            index++
            if (index >= sponge.visibleSize) {
                sponge.round()
                index = 0
            }
        }

        if (remaining > 0) {
            var rand = sponge.squeeze(index)

            // Little-endian conversion for remaining bytes
            repeat(remaining) {
                data.writeOctet(pos++, (rand and 0xFF).toByte())
                rand = rand ushr 8
            }
        }*/
    }
}