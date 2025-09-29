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
        val chiSquare = evaluateSampleData()
        val criticalValue = criticalValue()
        val passed = chiSquare < criticalValue
        append("Chi Square at $totalTakenSamples samples ")
        append("scores $chiSquare with ")
        append("critical value $criticalValue using α=$ALPHA.")
    }

    public companion object {
        private const val ALPHA = 0.05 // Significance level (5%)
        private const val NUM_CATEGORIES = 256 // Number of byte values
        private val DEGREES_OF_FREEDOM = NUM_CATEGORIES - 1 // 256 - 1 = 255

        public fun criticalValue(): Double {
            return inverseChiSquareCDF(1 - ALPHA, DEGREES_OF_FREEDOM.toDouble())
        }

        /**
         * Approximates the inverse CDF of the chi-square distribution.
         * Uses Wilson-Hilferty approximation with Newton-Raphson refinement.
         * @param p Cumulative probability (1 - α)
         * @param df Degrees of freedom
         * @return Chi-square critical value
         */
        private fun inverseChiSquareCDF(p: Double, df: Double): Double {
            // Wilson-Hilferty approximation for initial guess
            val z = normalInverseCDF(p) // Standard normal quantile
            val dfHalf = df / 2.0
            val initialGuess = df * (1 - 1.0 / (9 * df) + z * sqrt(1.0 / (9 * df))).pow(3.0)

            // Newton-Raphson refinement
            var x = initialGuess
            val tolerance = 1e-6
            val maxIterations = 100
            for (i in 0..<maxIterations) {
                val cdf = chiSquareCDF(x, df)
                val pdf = chiSquarePDF(x, df)
                if (pdf == 0.0) break // Avoid division by zero

                val delta = (cdf - p) / pdf
                x -= delta
                if (abs(delta) < tolerance) {
                    break
                }
                if (x < 0) x = initialGuess / 2 // Reset if negative
            }
            return x
        }

        /**
         * Approximates the chi-square CDF using the regularized incomplete gamma function.
         */
        private fun chiSquareCDF(x: Double, df: Double): Double {
            if (x <= 0) return 0.0
            val k = df / 2.0
            val s = x / 2.0
            return regularizedGammaP(k, s)
        }

        /**
         * Approximates the chi-square PDF.
         */
        private fun chiSquarePDF(x: Double, df: Double): Double {
            if (x <= 0) return 0.0
            val k = df / 2.0
            val term = x.pow(k - 1) * exp(-x / 2.0)
            val denom = 2.0.pow(k) * gamma(k)
            return term / denom
        }

        /**
         * Approximates the regularized incomplete gamma function P(a, x).
         */
        private fun regularizedGammaP(a: Double, x: Double): Double {
            if (x <= 0) return 0.0
            var sum = 0.0
            var term = 1.0 / a
            var n = 0.0
            val maxTerms = 1000.0
            while (abs(term) > 1e-10 && n < maxTerms) {
                sum += term
                n++
                term *= x / (a + n)
            }
            return exp(-x + a * ln(x) - logGamma(a)) * sum
        }

        /**
         * Computes the gamma function using Lanczos approximation.
         */
        private fun gamma(z_: Double): Double {
            var z = z_
            val p = doubleArrayOf(
                676.5203681218851, -1259.1392167224028, 771.32342877765313,
                -176.61502916214059, 12.507343278686905, -0.13857109526572012,
                9.9843695780195716e-6, 1.5056327351493116e-7
            )
            val g = 7.0
            if (z < 0.5) {
                return PI / (sin(PI * z) * gamma(1 - z))
            }
            z -= 1.0
            var a = p[0]
            val t = z + g + 0.5
            for (i in 1..<p.size) {
                a += p[i] / (z + i)
            }
            return sqrt(2 * PI) * t.pow(z + 0.5) * exp(-t) * a
        }

        /**
         * Computes the log-gamma function for numerical stability.
         */
        private fun logGamma(z: Double): Double {
            return ln(gamma(z))
        }

        /**
         * Approximates the inverse CDF of the standard normal distribution.
         */
        private fun normalInverseCDF(p: Double): Double {
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