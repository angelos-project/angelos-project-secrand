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
package org.angproj.sec

import org.angproj.sec.rand.AbstractSponge1024
import org.angproj.sec.rand.AbstractSponge21024
import org.angproj.sec.rand.AbstractSponge2256
import org.angproj.sec.rand.AbstractSponge2512
import org.angproj.sec.rand.AbstractSponge256
import org.angproj.sec.rand.AbstractSponge512
import org.angproj.sec.rand.JitterEntropy
import org.angproj.sec.rand.Reseeder
import org.angproj.sec.rand.Sponge
import org.angproj.sec.stat.AvalancheEffectTester
import org.angproj.sec.stat.BenchmarkSuiteBuilder
import org.angproj.sec.stat.ChiSquareTester
import org.angproj.sec.stat.MonteCarloTester
import org.angproj.sec.stat.SpongeBenchmark
import org.angproj.sec.stat.Statistical
import kotlin.jvm.JvmStatic
import kotlin.math.PI
import kotlin.math.sqrt


public object BenchmarkSpongesKt {

    /**
     * Computes the theoretical Monte Carlo standard error (std dev) for the π estimator
     * used in the BenchmarkSponges.kt health check.
     *
     * Formula (derived from binomial variance):
     * σ = √[π·(4 − π) / N]
     *
     * For the exact N used in MonteCarloTester (10 000 000):
     * σ ≈ 0.000519303977769
     */
    public fun monteCarloStandardError(samples: Long = 10_000_000): Double {
        return sqrt(PI * (4.0 - PI) / samples)
    }

    /**
     * Returns the scientifically grounded acceptance range for the π estimate.
     * Default: 5σ → >99.99994 % of runs from a perfect RNG will fall inside
     * (normal approximation via central limit theorem).
     */
    public fun monteCarloPiRange(
        samples: Long = 10_000_000,
        numSigma: Double = 5.0
    ): ClosedFloatingPointRange<Double> {
        val sigma = monteCarloStandardError(samples)
        val lower = PI - numSigma * sigma
        val upper = PI + numSigma * sigma
        return lower..upper
    }

    public fun monteCarloReport(monteCarlo: Statistical) {
        val sigma = 5.0
        val standardError = monteCarloPiRange(monteCarlo.sampleCount, sigma)
        val isInside = monteCarlo.keyValue in standardError
        val report = buildString {
            appendLine("Monte Carlo Benchmark Results")
            appendLine("Number of samples: ${monteCarlo.sampleCount}")
            appendLine("Expected PI: $PI")
            appendLine("Estimated PI: ${monteCarlo.keyValue}")
            appendLine("Standard error between: ${standardError.start} and ${standardError.endInclusive} with sigma $sigma")
            appendLine("In between: ${if(isInside) "Yes" else "No"}")
        }
        println(report)
    }

    /**
     * Theoretical standard deviation for the avalanche effect test.
     * D ~ Binomial(N, 0.5) → σ = √(N/4)
     *
     * N = output bit length of the sponge (e.g. 256, 512, or 1024 from visible state;
     * bit_mode is only for Monte Carlo testing and is discarded here).
     */
    public fun avalancheStandardDeviation(outputBits: Int): Double =
        sqrt(outputBits / 4.0)

    /**
     * Recommended acceptance range for average Hamming distance (5σ → >99.99994 % coverage).
     */
    public fun avalancheHammingRange(
        outputBits: Int,
        trials: Long = 10_000_000,   // matches benchmark `samples`
        numSigma: Double = 5.0
    ):  ClosedFloatingPointRange<Double> {
        val sigmaPerTrial = avalancheStandardDeviation(outputBits)
        val sigmaMean = sigmaPerTrial / sqrt(trials.toDouble())
        val mean = outputBits / 2.0
        val lower = mean - numSigma * sigmaMean
        val upper = mean + numSigma * sigmaMean
        return lower..upper
    }

    public fun avalancheEffectReport(avalanche: Statistical, sponge: Sponge) {
        val sigma = 5.0
        val standardDeviation = avalancheHammingRange(sponge.bitSize, avalanche.sampleCount, sigma)
        val p = sponge.bitSize / 2
        val isInside = (avalanche.keyValue * sponge.bitSize) in standardDeviation
        val report = buildString {
            appendLine("Avalanche Effect Benchmark Results")
            appendLine("Number of samples: ${avalanche.sampleCount}")
            appendLine("Expected p: $p")
            appendLine("Estimated p: ${avalanche.keyValue * sponge.bitSize}")
            appendLine("Standard deviation between: ${standardDeviation.start} and ${standardDeviation.endInclusive} with sigma $sigma")
            appendLine("In between: ${if(isInside) "Yes" else "No"}")
        }
        println(report)
    }

    public fun chiSquareReport(chiSquare: Statistical) {
        val isInside = chiSquare.keyValue < 293.2478
        val report = buildString {
            appendLine("Chi-Square Benchmark Results")
            appendLine("Number of samples: ${chiSquare.sampleCount}")
            appendLine("Critical value p: 0.05")
            appendLine("Degrees of freedom v: 255")
            appendLine("Estimated chiSquare: ${chiSquare.keyValue}")
            appendLine("Chi-Square x2: 293.2478")
            appendLine("Within: ${if(isInside) "Yes" else "No"}")
        }
        println(report)
    }


    public fun healthCheck(sponge: Sponge) {

        val suite = BenchmarkSuiteBuilder.build {
            samples { 10_000_000 }
            article { SpongeBenchmark(sponge) }
            register { MonteCarloTester(samples, MonteCarloTester.Mode.MODE_64_BIT, article) }
            register { AvalancheEffectTester(samples, article) }
            register { ChiSquareTester(samples, article) }
        }
        suite.runBlocking()

        val results = suite.collectResults()

        monteCarloReport(results["MonteCarloTester"]!!)
        avalancheEffectReport(results["AvalancheEffectTester"]!!, sponge)
        chiSquareReport(results["ChiSquareTester"]!!)
    }

    @JvmStatic
    public fun main(args: Array<String>) {
        if(args.isNotEmpty()) {
            println("Arguments provided, skipping benchmarks. Arguments: " + args.joinToString(", "))
        }

        mapOf(
            "AbstractSponge256" to object : AbstractSponge256() {},
            "AbstractSponge512" to object : AbstractSponge512() {},
            "AbstractSponge1024" to object : AbstractSponge1024() {},
            "AbstractSponge2256" to object : AbstractSponge2256() {},
            "AbstractSponge2512" to object : AbstractSponge2512() {},
            "AbstractSponge21024" to object : AbstractSponge21024() {}
        ).forEach {
            println("Benchmarking: " + it.key)
            healthCheck(it.value)
            println()
        }
    }
}
