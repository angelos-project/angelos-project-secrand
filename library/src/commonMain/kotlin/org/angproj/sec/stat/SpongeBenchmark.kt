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
package org.angproj.sec.stat

import org.angproj.sec.rand.Sponge
import org.angproj.sec.util.Octet
import org.angproj.sec.util.TypeSize

public class SpongeBenchmark(obj: Sponge): BenchmarkObject<Sponge>(obj) {

    init {
        obj.scramble()
    }

    override val sampleByteSize: Int
        get() = obj.byteSize

    override fun nextSample(): ByteArray {
        val sample = allocSampleArray()
        repeat(sampleByteSize / TypeSize.longSize) {
            Octet.writeLE(
                obj.squeeze(it),
                sample,
                it * TypeSize.longSize,
                TypeSize.longSize
             ) { index, value ->
                sample[index] = value
            }
        }
        obj.round()
        return sample
    }
}