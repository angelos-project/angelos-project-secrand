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
 * A benchmark article for RandomBits, generating samples by extracting bits from the random bits generator.
 * It produces byte arrays filled with random integers converted to bytes.
 *
 * @property article The RandomBits instance used for generating samples.
 */
public class RandomBitsBenchmark(article: RandomBits): BenchmarkArticle<RandomBits>(article) {

    override fun byteSizeImpl(): Int = 4

    /**
     * Generates the next sample by filling a byte array with random bits from the article.
     * Each sample consists of random integers written as bytes.
     *
     * @return A byte array containing the next random sample.
     */
    override fun nextSample(): ByteArray {
        val sample = allocSampleArray()
        repeat(sampleByteSize / TypeSize.intSize) {
            Octet.write(
                article.nextBits(TypeSize.intBits).toLong(),
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