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
import org.angproj.sec.util.toUnitFraction
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.PI
import kotlin.math.abs

/**
 * A Monte Carlo tester for estimating the value of PI using random sampling.
 * It receives random points within a unit square and counts how many fall inside the unit circle
 * to approximate PI using the Monte Carlo method.
 *
 * @param B The type of the benchmark object.
 * @param E The type of the benchmark article, extending BenchmarkArticle<B>.
 * @property samples The number of samples to take.
 * @property mode The mode of operation, determining the bit size for random number generation.
 * @property benchmarkArticle The benchmark article providing the random samples.
 */
public class MonteCarloTester<B, E: BenchmarkArticle<B>>(
    samples: Long, mode: Mode, benchmarkArticle: E
) : BenchmarkTester<B, E>(samples, mode.size, benchmarkArticle) {

    /**
     *
     */
    public enum class Mode(public val size: Int) {
        MODE_32_BIT(TypeSize.intSize * 2), MODE_64_BIT(TypeSize.longSize * 2)
    }

    private var insideCircle: Long = 0

    private fun getDistance(x1: Double, y1: Double, x2: Double, y2: Double): Double {
        return sqrt((x2 - x1).pow(2.0) + (y2 - y1).pow(2.0))
    }

    private fun accumulateSample(x: Double, y: Double) {
        if (getDistance(x, y, 0.0, 0.0) <= 1) {
            insideCircle++
        }
        totalTakenSamples++
    }

    private fun atomicMode32(sample: ByteArray) {
        repeat(maxLoops(sample.size)) {
            val offset = it * atomicSampleByteSize

            val x = Octet.read(sample, offset, TypeSize.intSize) { index ->
                sample[index]
            }.toInt()
            val y = Octet.read(sample, offset + TypeSize.intSize, TypeSize.intSize) { index ->
                sample[index]
            }.toInt()

            accumulateSample(x.toUnitFraction().toDouble(), y.toUnitFraction().toDouble())
        }
    }

    private fun atomicMode64(sample: ByteArray) {
        repeat(maxLoops(sample.size)) {
            val offset = it * atomicSampleByteSize

            val x = Octet.read(sample, offset, TypeSize.longSize) { index ->
                sample[index]
            }
            val y = Octet.read(sample, offset + TypeSize.longSize, TypeSize.longSize) { index ->
                sample[index]
            }

            accumulateSample(x.toUnitFraction(), y.toUnitFraction())
        }
    }

    override fun calculateSampleImpl(sample: ByteArray) {
        when(atomicSampleByteSize) {
            8 -> atomicMode32(sample)
            16 -> atomicMode64(sample)
            else -> error("Unknown sample size")
        }
    }

    private fun evaluateSampleData(): Double {
        return 4.0 * insideCircle / totalTakenSamples
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