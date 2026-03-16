/**
 * Copyright (c) 2026 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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

import org.angproj.sec.stat.BenchmarkArticle
import org.angproj.sec.stat.BenchmarkTester
import org.angproj.sec.stat.Statistical
import kotlin.math.min
import kotlin.random.Random

object Mocks {
    fun<B> mockBenchmarkTester(samplesAsked: Long, atomicSampleByteSize: Int, article: BenchmarkArticle<B>): BenchmarkTester<B, BenchmarkArticle<B>> = object : BenchmarkTester<B, BenchmarkArticle<B>>(
        samplesAsked, atomicSampleByteSize, article) {
        override fun calculateSampleImpl(sample: ByteArray) {
            totalTakenSamples += min(sample.size.toLong() / atomicSampleByteSize, samplesLeft)
        }
        private fun evaluateSampleData(): Double { return totalTakenSamples.toDouble() }
        override fun collectStatsImpl(): Statistical {
            return Statistical(
                totalTakenSamples,
                evaluateSampleData(),
                duration,
                totalTakenSamples * atomicSampleByteSize,
            )
        }

        override fun toString(): String = buildString { append(evaluateSampleData()) }
    }

    fun mockBenchmarkArticle(sampleByteSize: Int): BenchmarkArticle<Random> = object: BenchmarkArticle<Random>(Random) {
        override fun byteSizeImpl(): Int = sampleByteSize
        override fun nextSample(): ByteArray = allocSampleArray().apply { article.nextBytes(this) }
    }
}