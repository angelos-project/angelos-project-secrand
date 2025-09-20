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
            total += (lastSample.readLongBE(offset) xor currentSample.readLongBE(offset)).countOneBits()
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

    override fun toString(): String = buildString {
        val average = evaluateSampleData()
        append("Avalanche Effect at ")
        append(totalTakenSamples)
        append(" samples, averages at ")
        append(average)
        append(" with a deviation of ")
        append(average - 0.5)
        append(".")
    }
}