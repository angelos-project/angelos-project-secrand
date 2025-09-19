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

import org.angproj.sec.util.TypeSize
import org.angproj.sec.util.toUnitFraction
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.PI
import kotlin.time.Duration
import kotlin.time.TimeSource

public class MonteCarloTester<B, E: BenchmarkObject<B>>(
    samples: Int, mode: Mode, obj: E
) : BenchmarkTester<B, E>(samples, mode.size, obj) {

    public enum class Mode(public val size: Int) {
        MODE_32_BIT(TypeSize.intSize * 2), MODE_64_BIT(TypeSize.longSize * 2)
    }

    private var totalTakenSamples: Long = 0
    private var insideCircle: Long = 0

    private val startTime = TimeSource.Monotonic.markNow()
    private var duration: Duration = Duration.INFINITE

    override fun name(): String {
        return "MonteCarlo"
    }

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
            val index = it * atomicSampleByteSize
            val x = sample.readIntBE(index).toUnitFraction()
            val y = sample.readIntBE(index + TypeSize.intSize).toUnitFraction()
            accumulateSample(x.toDouble(), y.toDouble())
        }
    }

    private fun atomicMode64(sample: ByteArray) {
        repeat(sample.size / atomicSampleByteSize) {
            val index = it * atomicSampleByteSize
            val x = sample.readLongBE(index).toUnitFraction()
            val y = sample.readLongBE(index + TypeSize.longSize).toUnitFraction()
            accumulateSample(x, y)
        }
    }

    override fun calculateSampleImpl(sample: ByteArray) {
        when(atomicSampleByteSize) {
            8 -> atomicMode32(sample)
            16 -> atomicMode64(sample)
            else -> error("Unknown sample size")
        }
    }

    override fun collectStatsImpl(): Statistical {
        val piEstimate = 4.0 * insideCircle / totalTakenSamples

        return Statistical(
            totalTakenSamples,
            piEstimate,
            startTime.elapsedNow(),
            toString()
        )
    }

    override fun toString(): String = buildString {
        val piEstimate = 4.0 * insideCircle / totalTakenSamples
        append("Monte Carlo at ")
        append(totalTakenSamples)
        append(" samples, estimates PI at ")
        append(piEstimate)
        append(" with a deviation of ")
        append(piEstimate - PI)
        append(".")
    }
}