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

import org.angproj.sec.rand.AbstractSponge512
import kotlin.native.concurrent.ThreadLocal

/**
 * Feed of secure random, revitalized with conditioned secure entropy
 * about every 4th to 12th megabyte for high quality of secure output.
 * */
@ThreadLocal
public object SecureFeed : AbstractSponge512() {
    private val ROUNDS_64K: Int = Short.MAX_VALUE * 2
    private val ROUNDS_128K: Int = Short.MAX_VALUE * 4

    private var next: Int = 0

    init {
        revitalize()
    }

    private fun revitalize() {
        SecureEntropy.read(sponge)
        scramble()
    }

    override fun round() {
        if (counter > next) {
            next = ROUNDS_128K + sponge.first().mod(ROUNDS_64K)
            revitalize()
            counter = 1
        }
        super.round()
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