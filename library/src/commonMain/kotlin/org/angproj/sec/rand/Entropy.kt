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
package org.angproj.sec.rand

import kotlin.time.TimeSource


public object Entropy {

    init {
        val garble = GarbageGarbler()
        source = buildSource { push(garble).seg(DataSize._128B).buf(DataSize._128B).bin() }
        sink = buildSink { pull(garble).seg(DataSize._1K).buf(DataSize._1K).bin() }
    }

    private val moment = TimeSource.Monotonic.markNow()
    private var entropy: Long = IV_3AC5.iv * moment.elapsedNow().inWholeNanoseconds

    /**
     * Generates one byte of true random by snapping the nanosecond timestamp when
     * something happened this could be a user generated event for example.
     * */
    public fun <E> snapTime(action: () -> E): E {
        return catchMoment(action).also { source.writeByte(entropy.toByte()) }
    }

    private fun <E> catchMoment(action: () -> E): E {
        entropy = (moment.elapsedNow().inWholeNanoseconds * entropy).rotateLeft(32)
        return action()
    }

    /**
     * Natural random based on fluctuations on nanosecond intervals which produces byte level entropy.
     * Actually costs precious processing time to generate, use sparsely.
     * Also comes close to Monte Carlo but not perfect!
     * */
    public fun realTimeGatedEntropy(data: Binary) {
        require(data.limit <= DataSize._256B.size) { "To large for time-gated entropy! Max 256 bytes." }

        (0 until data.limit).forEach {
            catchMoment { it.floorMod(16) }
            data.storeByte(it, entropy.toByte())
        }
    }

    /**
     * Allows reading up to 1GB of extraordinarily secure random data at a time based on presumably true entropy.
     * */
    public override fun read(bin: Binary): Int {
        var index = 0
        repeat(bin.limit / TypeSize.long) {
            bin.storeLong(index, sink.readLong())
            index += TypeSize.long
        }
        repeat(bin.limit % TypeSize.long) {
            bin.storeByte(index, sink.readByte())
            index++
        }
        return index
    }

}