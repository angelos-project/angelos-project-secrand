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

/**
 * Tester for the Avalanche Effect of a cryptographic function.
 *
 * The Avalanche Effect is a desirable property of cryptographic algorithms,
 * where a small change in input (e.g., flipping a single bit) results in a significant
 * change in output (e.g., approximately half of the output bits change).
 *
 * This tester collects samples of outputs from the cryptographic function and analyzes
 * how many bits differ between successive samples. It maintains statistics on the number
 * of differing bits and calculates an average to evaluate the strength of the Avalanche Effect.
 *
 * @param B The type of the benchmark result.
 * @param E The type of the benchmark object, which must extend BenchmarkObject<B>.
 * @property samples The number of samples to collect for the test.
 * @property obj The benchmark object that provides the cryptographic function to be tested.
 */
public class AvalancheEffectTester<B, E: BenchmarkObject<B>>(
    samples: Long, obj: E
) : BenchmarkTester<B, E>(samples, 16, obj) {

    private var totalTakenSamples: Long = 0
    private val stats = IntArray(obj.sampleByteSize * TypeSize.byteBits)
    private var lastSample: ByteArray = byteArrayOf()
    private var currentSample: ByteArray = byteArrayOf()

    override fun name(): String {
        return "AvalancheEffect"
    }

    private fun accumulateSample() {
        var total = 0
        repeat(obj.sampleByteSize / TypeSize.longSize) { idx ->
            val offset = idx * TypeSize.longSize

            val last = Octet.readLE(lastSample, offset, TypeSize.longSize) { index ->
                lastSample[index]
            }
            val current = Octet.readLE(currentSample, offset, TypeSize.longSize) { index ->
                currentSample[index]
            }

            total += (last xor current).countOneBits()
        }
        stats[total]++
        totalTakenSamples++
    }

    override fun calculateSampleImpl(sample: ByteArray) {
        if(currentSample.size + sample.size == obj.sampleByteSize) {
            currentSample += sample
            if(lastSample.size == obj.sampleByteSize) {
                accumulateSample()
            }
            lastSample = currentSample
            currentSample = byteArrayOf()
        } else {
            currentSample += sample
        }
    }

    private fun evaluateSampleData(): Double {
        val sum = stats.sum()
        var total = 0L
        stats.forEachIndexed { index, value -> total += index * value }
        return total / sum.toDouble() / (obj.sampleByteSize * TypeSize.byteBits)
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
     * Provides a string representation of the Avalanche Effect Tester results.
     *
     * The string includes the total number of samples taken, the average avalanche effect,
     * and the deviation from the ideal value of 0.5.
     *
     * @return A string summarizing the results of the Avalanche Effect test.
     */
    override fun toString(): String = buildString {
        val average = evaluateSampleData()
        append("Avalanche Effect at ")
        append(totalTakenSamples)
        append(" samples, averages at ")
        append(average)
        append(" with a deviation of ")
        append(abs(average - 0.5))
        append(".")
    }
}