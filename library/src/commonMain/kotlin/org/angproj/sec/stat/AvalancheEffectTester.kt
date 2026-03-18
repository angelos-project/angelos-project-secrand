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

import org.angproj.sec.util.Octet
import org.angproj.sec.util.TypeSize
import kotlin.math.abs
import kotlin.math.max

/**
 * An avalanche effect tester for measuring how much a small change in input affects the output.
 * It compares consecutive samples by XORing them and counting the number of differing bits,
 * then averages the bit differences to assess the avalanche effect.
 *
 * @param B The type of the benchmark object.
 * @param E The type of the benchmark article, extending BenchmarkArticle<B>.
 * @property samples The number of samples to take.
 * @property benchmarkArticle The benchmark article providing the random samples.
 */
public class AvalancheEffectTester<B, E: BenchmarkArticle<B>>(
    samples: Long, benchmarkArticle: E
) : BenchmarkTester<B, E>(samples, max(benchmarkArticle.sampleByteSize, TypeSize.longSize), benchmarkArticle) {

    private val stats = IntArray(benchmarkArticle.sampleByteSize * TypeSize.byteBits)
    private var lastSample: ByteArray = byteArrayOf()
    private var currentSample: ByteArray = byteArrayOf()

    private fun accumulateSample() {
        var total = 0
        repeat(benchmarkArticle.sampleByteSize / TypeSize.longSize) { idx ->
            val offset = idx * TypeSize.longSize

            val last = Octet.read(lastSample, offset, TypeSize.longSize) { index ->
                lastSample[index]
            }
            val current = Octet.read(currentSample, offset, TypeSize.longSize) { index ->
                currentSample[index]
            }

            total += (last xor current).countOneBits()
        }
        stats[total]++
        totalTakenSamples++
    }

    override fun calculateSampleImpl(sample: ByteArray) {
        currentSample = sample
        if(lastSample.size == benchmarkArticle.sampleByteSize) {
            accumulateSample()
        }
        lastSample = currentSample
        currentSample = byteArrayOf()
    }

    private fun evaluateSampleData(): Double {
        val sum = stats.sum()
        var total = 0L
        stats.forEachIndexed { index, value -> total += index * value }
        return total / sum.toDouble() / (benchmarkArticle.sampleByteSize * TypeSize.byteBits)
    }

    override fun collectStatsImpl(): Statistical {
        return Statistical(
            totalTakenSamples,
            evaluateSampleData(),
            duration,
            totalTakenSamples * atomicSampleByteSize,
        )
    }
}