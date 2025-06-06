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

import org.angproj.sec.rand.AbstractSponge1024
import org.angproj.sec.rand.AbstractSponge256
import org.angproj.sec.rand.Entropy
import org.angproj.sec.util.ExportOctetByte
import org.angproj.sec.util.ImportOctetByte


public class GarbageGarbler: ExportOctetByte, ImportOctetByte {

    private val sponge: AbstractSponge1024 = object : AbstractSponge1024() {}

    private var _count: Int = 0
    public val count: Int
        get() = _count

    public val depleted: Boolean
        get() = _count >= Int.MAX_VALUE / 2

    init {
        Entropy.exportLongs(sponge, 0, sponge.visibleSize) { index, value ->
            sponge.absorb(value, index)
        }
        sponge.scramble()
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

        var index = 0
        var pos = offset

        val loops = length / 8
        val remaining = length % 8

        repeat(loops) {
            var rand = 0L
            // Little-endian conversion
            repeat(8) {
                rand = rand shl 8 or data.readOctet(pos++).toLong()
            }
            sponge.absorb(rand, index++)

            if (index >= sponge.visibleSize) {
                sponge.round()
                index = 0
            }
        }

        if (remaining > 0) {
            var rand = 0L

            // Little-endian conversion for remaining bytes
            repeat(remaining) {
                rand = rand shl 8 or data.readOctet(pos++).toLong()
            }
            sponge.absorb(rand, index)
        }

        if(pos > 0) {
            sponge.scramble()
        }
        _count = 0
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
        check(depleted) { "Secure amount of random depleted" }
        require(length > 0) { "Zero length data" }

        var index = 0
        var pos = offset

        val loops = length / 8
        val remaining = length % 8

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
        }

        _count += pos
    }
}