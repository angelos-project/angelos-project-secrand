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

public interface Octet {
    public fun<E> readBeBinary2LeLong(
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

    public fun<E> writeLeLong2BeBinary(
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
}