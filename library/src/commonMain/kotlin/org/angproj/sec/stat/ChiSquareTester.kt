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

import kotlin.collections.plusAssign
import kotlin.math.*

public class ChiSquareTester<B, E: BenchmarkObject<B>>(
    samples: Long, obj: E
) : BenchmarkTester<B, E>(samples, 16, obj) {

    private var totalTakenSamples: Long = 0
    private val frequencies = IntArray(NUM_CATEGORIES)

    override fun name(): String {
        return "ChiSquare"
    }

    override fun calculateSampleImpl(sample: ByteArray) {
        repeat(sample.size) {
            frequencies[sample[it].toUByte().toInt()]++
            totalTakenSamples++
        }
    }

    private fun evaluateSampleData(): Double {
        val expected = totalTakenSamples.toDouble() / NUM_CATEGORIES
        var chiSquare = 0.0
        for (freq in frequencies) {
            chiSquare += (freq - expected).pow(2.0) / expected
        }
        return chiSquare
    }

    override fun collectStatsImpl(): Statistical {
        return Statistical(
            totalTakenSamples,
            evaluateSampleData(),
            duration,
            toString()
        )
    }

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

    public companion object {
        private const val ALPHA = 0.05 // Significance level (5%)
        private const val NUM_CATEGORIES = 256 // Number of byte values
        private val DEGREES_OF_FREEDOM = NUM_CATEGORIES - 1 // 256 - 1 = 255

        public fun criticalValue(): Double {
            return wilsonHilfertyChiSquareCritical(1 - ALPHA, DEGREES_OF_FREEDOM.toDouble())
        }

        /**
         * Approximates the chi-square critical value using Wilson-Hilferty approximation.
         * @param p Cumulative probability (1 - α)
         * @param df Degrees of freedom
         * @return Chi-square critical value
         */
        public fun wilsonHilfertyChiSquareCritical(p: Double, df: Double): Double {
            // Standard normal quantile for p
            val z = normalInverseCDF(p)
            // Wilson-Hilferty approximation: transforms chi-square to approximate normal
            val dfHalf = df / 2.0
            return df * (1 - 1.0 / (9 * df) + z * sqrt(1.0 / (9 * df))).pow(3.0)
        }

        /**
         * Approximates the inverse CDF of the standard normal distribution.
         */
        private fun normalInverseCDF(p: Double): Double {
            // Abramowitz and Stegun approximation
            val c0 = 2.515517
            val c1 = 0.802853
            val c2 = 0.010328
            val d1 = 1.432788
            val d2 = 0.189269
            val d3 = 0.001308
            val t = sqrt(-2 * ln(min(p, 1 - p)))
            val sign = (if (p < 0.5) -1 else 1).toDouble()
            val result = t - (c0 + c1 * t + c2 * t * t) / (1 + d1 * t + d2 * t * t + d3 * t * t * t)
            return sign * result
        }
    }
}