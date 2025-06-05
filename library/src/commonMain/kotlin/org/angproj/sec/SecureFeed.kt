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
import org.angproj.aux.pipe.*
import org.angproj.sec.rand.AbstractSponge512
import org.angproj.aux.util.floorMod
import kotlin.native.concurrent.ThreadLocal

/**
 * Feed of secure random, revitalized with conditioned secure entropy
 * about every 4th to 12th megabyte for high quality of secure output.
 * */
@ThreadLocal
public object SecureFeed : AbstractSponge512(), PumpReader, Reader {
    private val ROUNDS_64K: Int = DataSize._64K.size
    private val ROUNDS_128K: Int = DataSize._128K.size

    private var next: Int = 0

    private var _count: Long = 0
    override val outputCount: Long
        get() = _count

    override val outputStale: Boolean = false

    private val sink: BinarySink = buildSink { pull(SecureEntropy).seg(DataSize._32B).buf(DataSize._32B).bin() }

    /*private val sink: BinarySink = PullPipe(
        Default,
        PumpSource(SecureEntropy),
        DataSize._32B,
        DataSize._32B
    ).getBinSink()*/

    init {
        require(SecureEntropy.byteSize == DataSize._32B.size)
        revitalize()
    }

    private fun revitalize() {
        repeat(SecureEntropy.visibleSize) {
            absorb(sink.readLong(), it) // Maybe only it, is division necessary or a bug?
        }
        scramble()
    }

    private fun cycle() {
        if (counter > next) {
            next = ROUNDS_128K + sponge.first().mod(ROUNDS_64K)
            revitalize()
            counter = 1
        }
        round()
    }

    private fun require(length: Int) {
        require(length.floorMod(byteSize) == 0) { "Length must be divisible by $byteSize." }
        require(length <= DataSize._8K.size) { "Length must not surpass 8 Kilobyte." }
    }

    private fun fill(data: Segment<*>) {
        var index = 0
        repeat(data.limit / byteSize) {
            repeat(visibleSize) {
                data.setLong(index, squeeze(it))
                index += TypeSize.long
            }
            cycle()
        }
    }

    override fun read(data: Segment<*>): Int {
        require(data.limit)
        revitalize()
        fill(data)
        _count += data.limit
        return data.limit
    }

    override fun read(bin: Binary): Int {
        require(bin.limit)
        revitalize()
        var index = 0
        repeat(bin.limit / byteSize) {
            repeat(visibleSize) {
                bin.storeLong(index, squeeze(it))
                index += TypeSize.long
            }
            cycle()
        }
        _count += bin.limit
        return bin.limit
    }
}