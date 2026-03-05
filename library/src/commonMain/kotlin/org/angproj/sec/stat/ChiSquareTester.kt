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
 * Tester for Chi Square of a cryptographic function.

 *
 * @param B The type of the benchmark result.
 * @param E The type of the benchmark object, which must extend BenchmarkObject<B>.
 * @property samples The number of samples to collect for the test.
 * @property benchmarkArticle The benchmark object that provides the cryptographic function to be tested.
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
            toString()
        )
    }

    /**
     * Provides a string representation of the Avalanche Effect Tester results.
     *
     * The string includes the total number of samples taken, the average avalanche effect,
     * and the deviation from the ideal value of 0.5.
     *
     * @return A string summarizing the results of the Avalanche Effect test.
     */
    override fun toString(): String = buildString {
        val chiSquare = evaluateSampleData()
        append("Chi Square at ")
        append(totalTakenSamples)
        append(" samples, with value ")
        append(chiSquare)
        //append(" with a deviation of ")
        //append(abs(average - 0.5))
        append(".")
    }
}