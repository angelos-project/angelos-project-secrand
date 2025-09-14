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

import org.angproj.sec.rand.AbstractSponge512
import org.angproj.sec.rand.Security
import org.angproj.sec.util.ExportOctetByte
import org.angproj.sec.util.ExportOctetLong
import org.angproj.sec.util.floorMod

/**
 * SecureFeed is a singleton object that provides a secure random number generator
 * based on the AbstractSponge512 algorithm. It uses a secure entropy source to
 * generate random numbers and provides methods to read random bytes into various
 * data structures.
 *
 * The SecureFeed object is designed to be used in cryptographic applications where
 * high-quality randomness is required.
 */
public object SecureFeed : ExportOctetLong, ExportOctetByte {
    private val ROUNDS_64K: Int = Short.MAX_VALUE * 2
    private val ROUNDS_128K: Int = Short.MAX_VALUE * 4

    private val sponge: Security = object : AbstractSponge512(), Security {
        override var position: Int = 0
    }

    private var next: Int = 0
    private var counter: Int = 0

    init {
        revitalize()
    }

    /**
     * Revitalizes the secure random source by reading from the secure entropy.
     * This method is called to fill the sponge with new entropy and reset the counter.
     */
    private fun revitalize() {
        SecureEntropy.exportLongs(sponge, 0, sponge.visibleSize) { index, value ->
            sponge.absorb(value, index)
        }
        sponge.scramble()
    }

    /**
     * Checks if the current counter exceeds the next threshold.
     * If it does, it resets the next threshold and revitalizes the sponge.
     * This is used to ensure that the sponge is periodically refreshed with new entropy.
     */
    private fun round() {
        if (counter >= next) {
            next = ROUNDS_128K + sponge.squeeze(0).toInt().floorMod(ROUNDS_64K)
            revitalize()
            counter = 0
        } else {
            counter++
        }
    }

    private inline fun<reified R: Any> generateEntropy(entropy: Long, loops: Int): Long {
        var data = entropy
        repeat(loops) {
            data = data shl 8 xor sponge.getNextBits(32).toLong()
        }
        return data
    }

    /**
     * Reads random longs into a LongArray from the secure random source.
     * This function fills the LongArray with random numbers starting from a specified offset.
     * The data is read in chunks, and the sponge state is updated accordingly.
     *
     * @param data The LongArray to fill with random numbers.
     */
    override fun <E> exportLongs(data: E, offset: Int, length: Int, writeOctet: E.(Int, Long) -> Unit) {
        if(length <= 0) return

        round()
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
     * Reads bytes into a data structure from the secure random source.
     * This function fills the data structure with random bytes
     * starting from a specified offset and for a specified length.
     *
     * The data structure must provide a way to write bytes at specific indices
     * and the function will write bytes in little-endian order.
     * This is useful for filling buffers, arrays, or any other
     * data structure that can hold bytes.
     *
     * @param data The data structure to fill with random bytes.
     * @param offset The starting index in the data structure to write to.
     * @param length The number of bytes to read. Defaults to 0, meaning the entire data structure.
     * @param writeOctet A function that writes a byte at a specific index in the data structure.
     */
    override fun <E> exportBytes(data: E, offset: Int, length: Int, writeOctet: E.(index: Int, value: Byte) -> Unit) {
        if(length <= 0) return

        round()
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