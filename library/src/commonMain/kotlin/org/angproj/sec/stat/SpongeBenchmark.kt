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

/**
 * Benchmark object for the Sponge cryptographic primitive.
 *
 * This class extends the BenchmarkObject class and provides functionality
 * to benchmark a Sponge instance. It initializes the Sponge by scrambling it
 * and provides methods to retrieve sample byte size and generate the next sample.
 *
 * @param obj The Sponge instance to be benchmarked.
 */
public class SpongeBenchmark(obj: Sponge): BenchmarkArticle<Sponge>(obj) {

    init {
        obj.scramble()
    }

    override val sampleByteSize: Int
        get() = article.byteSize

    override fun nextSample(): ByteArray {
        val sample = allocSampleArray()
        repeat(sampleByteSize / TypeSize.longSize) {
            Octet.write(
                article.squeeze(it),
                sample,
                it * TypeSize.longSize,
                TypeSize.longSize
             ) { index, value ->
                sample[index] = value
            }
        }
        article.round()
        return sample
    }
}