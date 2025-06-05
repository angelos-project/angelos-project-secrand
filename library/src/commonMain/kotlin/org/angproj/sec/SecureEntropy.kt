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
import org.angproj.sec.rand.AbstractSponge256
import org.angproj.sec.rand.Entropy
import org.angproj.aux.util.floorMod
import org.angproj.aux.util.useWith
import kotlin.native.concurrent.ThreadLocal

/**
 * Conditioned entropy mixed with pseudo-random but revitalizes with real entropy on every read.
 * Supposed to pass Monte Carlo testing and security requirements of output quality.
 * */
@ThreadLocal
public object SecureEntropy : AbstractSponge256(), PumpReader, Reader {

    private var _count: Long = 0
    override val outputCount: Long
        get() = _count

    override val outputStale: Boolean = false

    init {
        revitalize()
    }

    private fun revitalize() {
        binOf(visibleSize * TypeSize.long).useWith { bin ->
            Entropy.realTimeGatedEntropy(bin)
            (0 until visibleSize).forEach {
                absorb(bin.retrieveLong(it * TypeSize.long), it)
            }
        }
        scramble()
    }

    private fun require(length: Int) {
        require(length.floorMod(byteSize) == 0) { "Length must be divisible by $byteSize." }
        require(length <= DataSize._1K.size) { "Length must not surpass 1 Kilobyte." }
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
            round()
        }
        _count += bin.limit
        return bin.limit
    }
}