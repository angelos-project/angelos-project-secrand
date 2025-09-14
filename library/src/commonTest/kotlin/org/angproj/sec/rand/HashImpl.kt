/**
 * Copyright (c) 2025 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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

import org.angproj.sec.util.TypeSize


abstract class Hash<E: Sponge>(private val sponge: E) {

    var remainder = byteArrayOf()
    var position = 0

    /**
     * Absorbs the input ByteArray into the sponge state, treating the input as big endian.
     * On a little endian system, bytes are reversed before conversion to Long.
     */
    fun update(input: ByteArray) {
        val data = remainder + input
        var chunks = data.size.floorDiv(TypeSize.longSize)
        var end = 0
        var start = 0
        repeat(chunks) { index ->
            end = index * TypeSize.longSize
            start = end + TypeSize.longSize
            var value: Long = 0
            (start-1 downTo end).forEach { byte ->
                value = (value shl 8) or (remainder[byte].toLong() and 0xff)
            }
            sponge.absorb(value, position++ % sponge.visibleSize)
            if(position == sponge.visibleSize) {
                position = 0
                sponge.round()
            }
        }
        remainder = data.copyOfRange(start, data.size)
    }

    /**
     * Returns the digest as a ByteArray in big endian order.
     * Converts each internal little endian Long to big endian bytes.
     */
    fun digest(): ByteArray {
        if(remainder.isNotEmpty()) {
            update(ByteArray(TypeSize.longSize - remainder.size % TypeSize.longSize))
        }
        if(position != 0) {
            sponge.round()
        }
        sponge.scramble()

        val digestBytes = ByteArray(sponge.visibleSize * TypeSize.longSize)
        var pos = 0
        repeat(sponge.visibleSize) {
            var state = sponge.squeeze(it)
            repeat(8) {
                digestBytes[pos++] = (state and 0xff).toByte()
                state = state ushr 8
            }
        }
        return digestBytes
    }
}