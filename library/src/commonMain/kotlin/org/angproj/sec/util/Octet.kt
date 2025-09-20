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
package org.angproj.sec.util

public object Octet {
    public fun<E> readLE(
        src: E,
        index: Int,
        size: Int,
        readOctet: E.(index: Int) -> Byte
    ): Long {
        require(size in TypeSize.shortSize..TypeSize.longSize)
        var dst: Long = 0
        repeat(size) {
            dst = dst or (src.readOctet(index + it).toLong() shl (8 * ((size - 1) - it)))
        }
        return dst
    }

    public fun<E> writeLE(
        src: Long,
        dst: E,
        index: Int,
        size: Int,
        writeOctet: E.(index: Int, value: Byte) -> Unit
    ) {
        require(size in TypeSize.shortSize..TypeSize.longSize)
        repeat(size) {
            dst.writeOctet(it + index, ((src ushr ((size - 1) - it) * 8) and 0xff).toByte())
        }
    }


    public fun<E> toHex(src: Byte, data: E, index: Int, writeOctet: E.(index: Int, value: Byte) -> Unit): Int {
        data.writeOctet(index, toHexChar<Unit>((src.toInt() shr 4) and 0xf))
        data.writeOctet(index+1, toHexChar<Unit>(src.toInt() and 0xf))
        return index+2
    }

    private inline fun<reified R: Any> toHexChar(n: Int): Byte = when {
        n < 10 -> n + 0x30
        else -> n - 10 + 0x61
    }.toByte()

    public fun asHexSymbolString(data: ByteArray): String = buildString {
        data.forEach {
            toHex(it, data, -1) { _, value ->
                append(value.toInt().toChar())
            }
        }
    }
}