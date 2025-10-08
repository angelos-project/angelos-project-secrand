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

import org.angproj.sec.rand.RandomBits
import org.angproj.sec.util.Octet
import org.angproj.sec.util.TypeSize

/**
 * Benchmark object for the RandomBits cryptographic primitive.
 *
 * This class extends the BenchmarkObject class and provides functionality
 * to benchmark a RandomBits instance.
 *
 * @param obj The RandomBits instance to be benchmarked.
 */
public class RandomBitsBenchmark(obj: RandomBits): BenchmarkObject<RandomBits>(obj) {

    override val sampleByteSize: Int
        get() = 8

    override fun nextSample(): ByteArray {
        val sample = allocSampleArray()
        repeat(sampleByteSize / TypeSize.intSize) {
            Octet.writeNet(
                obj.nextBits(TypeSize.intBits).toLong(),
                sample,
                it * TypeSize.intSize,
                TypeSize.intSize
             ) { index, value ->
                sample[index] = value
            }
        }
        return sample
    }
}