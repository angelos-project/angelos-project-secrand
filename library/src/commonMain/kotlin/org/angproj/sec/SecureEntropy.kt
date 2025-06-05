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

import org.angproj.sec.rand.AbstractSponge256
import org.angproj.sec.rand.Entropy
import org.angproj.sec.util.ExportOctets
import org.angproj.sec.util.ImportOctets
import org.angproj.sec.util.floorMod

/**
 * SecureEntropy is a singleton object that provides a secure source of entropy
 * using a sponge construction with a size of 256 bits. It revitalizes the sponge
 * with real-time gated entropy and provides methods to read random bytes.
 */
public object SecureEntropy : AbstractSponge256(), ExportOctets {

    init {
        revitalize()
    }

    /**
     * Revitalizes the sponge by reading real-time gated entropy and scrambling the state.
     * This method is called to ensure that the sponge has fresh entropy before reading.
     */
    private fun revitalize() {
        Entropy.realTimeGatedEntropy(sponge)
        scramble()
    }


    internal fun read(data: LongArray) {
        var offset = 0
        revitalize()

        repeat(data.size) {
            data[it] = squeeze(offset)
            offset++
            if (offset >= visibleSize) {
                round()
                offset = 0
            }
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
    override fun <E> export(data: E, offset: Int, length: Int, writeOctet: E.(index: Int, value: Byte) -> Unit) {
        require(length > 0) { "Zero length data" }

        var index = 0
        var pos = offset
        revitalize()

        val loops = length / 8
        val remaining = length % 8

        repeat(loops) {
            var rand = squeeze(index)

            // Little-endian conversion
            repeat(8) {
                data.writeOctet(pos++, (rand and 0xFF).toByte())
                rand = rand ushr 8
            }

            index++
            if (index >= visibleSize) {
                round()
                index = 0
            }
        }

        if (remaining > 0) {
            var rand = squeeze(index)

            // Little-endian conversion for remaining bytes
            repeat(remaining) {
                data.writeOctet(pos++, (rand and 0xFF).toByte())
                rand = rand ushr 8
            }
        }
    }
}