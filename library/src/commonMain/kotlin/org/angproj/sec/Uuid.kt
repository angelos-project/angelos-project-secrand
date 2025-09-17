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
package org.angproj.sec

import org.angproj.sec.util.Octet

public data class Uuid(
    private val upper: Long = 0,
    private val lower: Long = 0
): Octet {

    public constructor() : this(
        (SecureFeed.getNextBits(32).toLong() shl 32) or SecureFeed.getNextBits(32).toLong(),
        (SecureFeed.getNextBits(32).toLong() shl 32) or SecureFeed.getNextBits(32).toLong()
    )

    public override fun toString(): String {
        val sb = StringBuilder()
        printStr<Unit>(lower, sb, printStr<Unit>(upper, sb, 0))
        return sb.toString()
    }

    private inline fun<reified E: Any> printStr(src: Long, sb: StringBuilder, counter: Int): Int {
        var cnt = counter
        writeLeLong2BeBinary(src, sb, -1, 8) { _, value ->
            if(cnt in hyphens) {
                sb.append('-')
                cnt += 1
            }
            Octet.toHex(value, sb, -1) { _, v ->
                sb.append(v.toInt().toChar())
                cnt += 1
            }
        }
        return cnt
    }

    private companion object Companion {
        private val hyphens = listOf(8,13,18,23)
    }
}