/**
 * Copyright (c) 2026 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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

import org.angproj.sec.hash.squeezerOf
import org.angproj.sec.util.Octet
import org.angproj.sec.util.TypeSize

object Sampler {
    fun healthySample() = ByteArray(1024).apply {
        val failer = Stubs.stubSucceedSqueezeSponge().squeezerOf()
        repeat(128) {
            Octet.write(failer(), this, it * TypeSize.longSize, TypeSize.longSize) { index, value ->
                this.set(index, value)
            }
        }
    }

    fun failedSample() = ByteArray(1024).apply {
        val failer = Stubs.stubFailSqueezeSponge().squeezerOf()
        repeat(128) {
            Octet.write(failer(), this, it * TypeSize.longSize, TypeSize.longSize) { index, value ->
                this.set(index, value)
            }
        }
    }

    fun emptySample(): ByteArray = byteArrayOf()

    fun singleASample(): ByteArray = "a".encodeToByteArray()

    fun abcSample(): ByteArray = "abc".encodeToByteArray()

    fun messageDigestSample(): ByteArray = "message digest".encodeToByteArray()

    fun aToZSample(): ByteArray = aToZGenerator().encodeToByteArray()

    fun nopqSample(): ByteArray = nopqGenerator().encodeToByteArray()

    fun alphaNumSample(): ByteArray = (aToZGenerator().uppercase() + aToZGenerator() + numGenerator()).encodeToByteArray()

    fun eightNumSample(): ByteArray {
        val num = numGenerator().encodeToByteArray()
        return num + num + num + num +
                num + num + num + num
    }

    fun millionASample(): ByteArray = "a".repeat(1_000_000).encodeToByteArray()

    internal fun numGenerator(): String {
        val sb = StringBuilder()
        (48 .. 57).forEach { sb.append(it.toChar()) }
        return sb.toString()
    }

    internal fun aToZGenerator(): String {
        val sb = StringBuilder()
        (97 .. 122).forEach { sb.append(it.toChar()) }
        return sb.toString()
    }

    internal fun nopqGenerator(): String {
        val sb = StringBuilder()
        val alphabet = aToZGenerator()
        (0 .. (110 - 97)).forEach {
            sb.append(alphabet.substring(it, it + 4))
        }
        return sb.toString()
    }
}