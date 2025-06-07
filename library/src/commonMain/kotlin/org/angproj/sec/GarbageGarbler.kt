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
package org.angproj.sec

import org.angproj.sec.rand.AbstractRandom
import org.angproj.sec.rand.AbstractSponge1024
import org.angproj.sec.rand.Entropy
import org.angproj.sec.util.ImportOctetByte


public class GarbageGarbler: AbstractRandom(), ImportOctetByte {

    private val sponge: AbstractSponge1024 = object : AbstractSponge1024() {}

    private val entropy: ByteArray = ByteArray(128)
    private var entropyPos = 0

    private var _count: Int = 0
    public val count: Int
        get() = _count + position

    public val depleted: Boolean
        get() = count >= Int.MAX_VALUE / 2

    init {
        // Seed the sponge
        Entropy.exportLongs(sponge, 0, sponge.visibleSize) { index, value ->
            sponge.absorb(value, index)
        }
        sponge.scramble()

        // Output random to read buffer in AbstractRandom
        revitalize()
    }

    private fun reseed() {
        var position = 0

        // Feed entropy into sponge
        repeat(sponge.visibleSize) { idx ->
            var seed = 0L
            repeat(8) {
                seed = (seed shl 8) or entropy[position++].toLong()
            }
            sponge.absorb(seed, idx)
        }

        sponge.scramble() // Scramble the sponge
        entropy.fill(0) // Reset entropy buffer

        entropyPos = 0 // Reset entropy pos
        _count = 0 // Reset output counter
    }

    override fun refill() {
        check(!depleted) { "GarbageGarbler is depleted" }
        var position = 0

        repeat(buffer.size) {
            buffer[it] = sponge.squeeze(position++)

            if (position >= sponge.visibleSize) {
                sponge.round()
                position = 0
            }
        }
    }

    /**
     * Writes bytes from a data structure to the secure random source.
     * This function absorbs bytes starting from a specified offset and for a specified length.
     * The data structure must provide a way to read bytes at specific indices,
     * and the function will read bytes in little-endian order.
     *
     * @param data The data structure to read bytes from.
     * @param offset The starting index in the data structure to read from.
     * @param length The number of bytes to write. Defaults to 0, meaning the entire data structure.
     * @param readOctet A function that reads a byte at a specific index in the data structure.
     */
    override fun <E> importBytes(data: E, offset: Int, length: Int, readOctet: E.(index: Int) -> Byte) {
        require(length > 0) { "Zero length data" }

        repeat(length) { index ->
            entropy[entropyPos++] = data.readOctet(index + offset)

            if (entropyPos >= entropy.lastIndex) {
                reseed()
                revitalize()
            }
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
}