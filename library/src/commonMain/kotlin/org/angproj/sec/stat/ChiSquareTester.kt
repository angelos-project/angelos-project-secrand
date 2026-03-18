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
package org.angproj.sec.stat

import org.angproj.sec.util.TypeSize
import kotlin.math.max

/**
 * A Chi-Square tester for evaluating the uniformity of random byte distributions.
 * It counts the frequency of each byte value (0-255) in the samples and computes the Chi-Square statistic
 * to measure how well the observed frequencies match the expected uniform distribution.
 *
 * @param B The type of the benchmark object.
 * @param E The type of the benchmark article, extending BenchmarkArticle<B>.
 * @property samples The number of samples to take.
 * @property benchmarkArticle The benchmark article providing the random samples.
 */
public class ChiSquareTester<B, E: BenchmarkArticle<B>>(
    samples: Long, benchmarkArticle: E
) : BenchmarkTester<B, E>(samples, max(benchmarkArticle.sampleByteSize, TypeSize.longSize), benchmarkArticle) {

    private val observed = DoubleArray(256)

    override fun calculateSampleImpl(sample: ByteArray) {
        sample.forEach { observed[it.toUByte().toInt()] += 1.0 }
        totalTakenSamples++
    }

    private fun evaluateSampleData(): Double {
        val sum = observed.sum()
        val expectedAverage = sum / observed.size.toDouble()
        var chiSquare = 0.0
        observed.forEach {
            val difference: Double = it - expectedAverage
            chiSquare += (difference * difference) / expectedAverage
        }
        return chiSquare
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