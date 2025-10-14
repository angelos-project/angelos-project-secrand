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
 * Tester for estimating the value of Pi using the Monte Carlo method.
 *
 * The Monte Carlo method is a statistical technique that uses random sampling to estimate numerical values.
 * In this tester, random points are generated within a unit square, and the proportion of points that fall
 * within a quarter circle inscribed within that square is used to estimate the value of Pi.
 *
 * @param B The type of the benchmark result.
 * @param E The type of the benchmark object, which must extend BenchmarkObject<B>.
 * @property samples The number of samples to collect for the test.
 * @property mode The mode of operation, determining the size of each sample (32-bit or 64-bit).
 * @property obj The benchmark object that provides the random data for the test.
 */
public class MonteCarloTester<B, E: BenchmarkObject<B>>(
    samples: Long, mode: Mode, obj: E
) : BenchmarkTester<B, E>(samples, mode.size, obj) {

    public enum class Mode(public val size: Int) {
        MODE_32_BIT(TypeSize.intSize * 2), MODE_64_BIT(TypeSize.longSize * 2)
    }

    private var totalTakenSamples: Long = 0
    private var insideCircle: Long = 0

    /**
     * Calculates the distance between two points in a 2D space.
     *
     * @param x1 x-coordinate of the first point
     * @param y1 y-coordinate of the first point
     * @param x2 x-coordinate of the second point
     * @param y2 y-coordinate of the second point
     * @return the distance between the two points
     */
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
        repeat(sample.size / atomicSampleByteSize) {
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
        repeat(sample.size / atomicSampleByteSize) {
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
            toString()
        )
    }

    /**
     * Provides a string representation of the Monte Carlo tester, including the number of samples taken,
     * the estimated value of Pi, and the deviation from the actual value of Pi.
     *
     * @return A string summarizing the results of the Monte Carlo test.
     */
    override fun toString(): String = buildString {
        val piEstimate = evaluateSampleData()
        append("Monte Carlo at ")
        append(totalTakenSamples)
        append(" samples, estimates PI to ")
        append(piEstimate)
        append(" with a deviation of ")
        append(abs(piEstimate - PI))
        append(".")
    }
}