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
import org.angproj.sec.util.floorMod
import kotlin.native.concurrent.ThreadLocal

/**
 * Conditioned entropy mixed with pseudo-random but revitalizes with real entropy on every read.
 * Supposed to pass Monte Carlo testing and security requirements of output quality.
 * */
@ThreadLocal
public object SecureEntropy : AbstractSponge256() {

    init {
        revitalize()
    }

    private fun revitalize() {
        Entropy.realTimeGatedEntropy(sponge)
        scramble()
    }

    private fun require(length: Int) {
        require(length.floorMod(byteSize) == 0) { "Length must be divisible by $byteSize." }
        require(length <= 1024) { "Length must not surpass 1 Kilobyte." }
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

    public fun <E> read(data: E, offset: Int = 0, length: Int = 0, writeOctet: E.(index: Int, value: Byte) -> Unit) {
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

    public fun read(data: ByteArray, offset: Int = 0, length: Int = data.size) {
        read(data, offset, length) { index, value ->
            this[index] = value
        }
    }
}