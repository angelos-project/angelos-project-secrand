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


inline fun<reified R: Any> writeLeLong2BeBinary(src: Long, dst: ByteArray, index: Int, size: Int) {
    repeat(size) {
        dst[it + index] = ((src ushr ((size - 1) - it) * 8) and 0xff).toByte()
    }
}


inline fun<reified R: Any> readBeBinary2LeLong(src: ByteArray, index: Int, size: Int): Long {
    var dst: Long = 0
    repeat(size) {
        dst = dst or (src[index + it].toLong() shl (8 * ((size - 1) - it)))
    }
    return dst
}


abstract class Hash<E: Sponge>(private val sponge: E, public val debug: Boolean = false) {

    var remainder = byteArrayOf()
    var offset = 0

    protected fun printDebug(output: String) {
        if(debug) println(output)
    }

    /**
     * Absorbs the input ByteArray into the sponge state, treating the input as big endian.
     * On a little endian system, bytes are reversed before conversion to Long.
     */
    fun update(input: ByteArray) {
        printDebug("UPDATE - " + sponge::class.toString() + " : " + input.size)
        val data = remainder + input
        val loops = (data.size - data.size.mod(TypeSize.longSize)).div(TypeSize.longSize)
        repeat(loops){
            sponge.absorb(
                readBeBinary2LeLong<Unit>(data, it * TypeSize.longSize, TypeSize.longSize),
                offset++ % sponge.visibleSize
            )
            printDebug("ABSORB")
            if(offset == sponge.visibleSize) {
                offset = 0
                sponge.round()
            }
        }
        remainder = data.copyOfRange(data.size - (data.size.mod(TypeSize.longSize)), data.size)
    }

    /**
     * Returns the digest as a ByteArray in big endian order.
     * Converts each internal little endian Long to big endian bytes.
     */
    fun digest(): ByteArray {
        printDebug("DIGEST - " + sponge::class.toString())
        if(remainder.isNotEmpty()) {
            update(ByteArray(TypeSize.longSize - remainder.size.mod(TypeSize.longSize)))
        }
        if(offset != 0) {
            sponge.round()
        }
        sponge.scramble()

        val digestBytes = ByteArray(sponge.byteSize)
        repeat(sponge.visibleSize) {
            writeLeLong2BeBinary<Unit>(
                sponge.squeeze(it),
                digestBytes,
                it * TypeSize.longSize,
                TypeSize.longSize
            )
        }
        return digestBytes
    }
}