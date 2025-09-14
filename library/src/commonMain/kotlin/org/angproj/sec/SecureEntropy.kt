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
import org.angproj.sec.rand.Security
import org.angproj.sec.util.ExportOctetByte
import org.angproj.sec.util.ExportOctetLong

/**
 * SecureEntropy is a singleton object that provides a secure source of entropy
 * using a sponge construction with a size of 256 bits. It revitalizes the sponge
 * with real-time gated entropy and provides methods to read random bytes.
 */
public object SecureEntropy : ExportOctetLong, ExportOctetByte {

    private val sponge: Security = object : AbstractSponge256(), Security {
        override var position: Int = 0
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
    }
}