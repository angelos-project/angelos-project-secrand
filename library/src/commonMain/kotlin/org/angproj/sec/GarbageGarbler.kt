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

import org.angproj.aux.io.*
import org.angproj.sec.rand.AbstractSponge1024
import org.angproj.sec.rand.Entropy
import org.angproj.aux.util.floorMod


public class GarbageGarbler(
    public val staleSize: DataSize = DataSize._1G,
): AbstractSponge1024(), PumpWriter, PumpReader {

    private var inputCnt: Long = 0
    private var outputCnt: Long = 0
    private var _count: Int = 0

    init {
        val start = binOf(byteSize)
        Entropy.realTimeGatedEntropy(start)
        repeat(16) { absorb(start.retrieveLong(it * TypeSize.long), it) }
    }

    override val outputCount: Long
        get() = inputCnt + outputCnt

    override val inputCount: Long
        get() =  inputCnt

    override val inputStale: Boolean = false

    override val outputStale: Boolean
        get() = _count >= staleSize.size

    private fun require(length: Int) {
        require(length.floorMod(byteSize) == 0) { "Garble must be divisible by the length of the inner sponge." }
    }

    override fun write(data: Segment<*>): Int {
        require(data.limit)
        var index = 0
        repeat(data.limit / byteSize) {
            repeat(16) {
                absorb(data.getLong(index), it)
                index += TypeSize.long
            }
            scramble()
        }
        inputCnt += data.limit
        _count = 0
        return data.limit
    }

    private fun fill(data: Segment<*>) {
        var index = 0
        repeat(data.limit / byteSize) {
            repeat(visibleSize) {
                data.setLong(index, squeeze(it))
                index += TypeSize.long
            }
            round()
        }
    }

    public override fun read(data: Segment<*>): Int {
        require(data.limit)
        if(data.limit + _count > staleSize.size) return 0
        fill(data)
        outputCnt += data.limit
        _count += data.limit
        return data.limit
    }
}