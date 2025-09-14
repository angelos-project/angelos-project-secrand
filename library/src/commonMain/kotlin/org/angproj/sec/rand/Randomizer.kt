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

public interface Randomizer {

    public fun getNextBits(bits: Int): Int

    public companion object {
        public inline fun<reified R: Any> foldBits(value: Long): Int {
            return ((value ushr 32).toInt() xor (value and 0xffffffff).toInt())
        }

        public inline fun<reified R: Any> reduceBits(bits: Int, value: Int): Int {
            return value ushr (32 - bits)
        }
    }
}