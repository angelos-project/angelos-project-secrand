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

import org.angproj.sec.util.Benchmark
import org.angproj.sec.util.BenchmarkObject
import kotlin.time.Duration
import kotlin.time.TimeSource

public abstract class AvalancheObject<E>(obj: E) : BenchmarkObject<E>(obj) {
    public abstract val bufferSize: Int

    public abstract val digestSize: Int

    public abstract fun digest(data: LongArray)
}

/**
 * MonteCarlo is a class that implements the Monte Carlo method to estimate the value of Pi.
 * It generates random points in a unit square and counts how many fall inside a unit circle.
 * The estimated value of Pi is calculated based on the ratio of points inside the circle to the total number of points.
 */
public class AvalancheEffect<B>(
    samples: Int, config: () -> AvalancheObject<B>
) : Benchmark<B, AvalancheObject<B>>(samples, config) {

    private val stats = IntArray(obj.digestSize * 8)
    private var duration: Duration = Duration.INFINITE

    /**
     * Estimates the value of Pi using the Monte Carlo method.
     * The method generates random points in a unit square and counts how many fall inside a unit circle.
     *
     * @return the estimated value of Pi
     */
    override fun calculateData() {
        val startTime = TimeSource.Monotonic.markNow()

        val digest = LongArray(obj.bufferSize)
        val oldDigest = LongArray(obj.bufferSize)
        obj.digest(oldDigest)

        for (i in 0..<samples) {
            obj.digest(digest)
            var total = 0
            repeat(obj.bufferSize) { idx ->
                total += (digest[idx] xor oldDigest[idx]).countOneBits()
            }
            stats[total]++
            digest.copyInto(oldDigest)
        }
        duration = startTime.elapsedNow() // Duration in nanoseconds
    }

    /**
     * Returns a string representation of the AvalancheEffect object.
     *
     * @return a string containing the class name, estimated Pi, number of samples, and duration
     */
    override fun toString(): String {
        val usage = stats.count { it == 0 }
        val sum = stats.sum()
        var tot = 0L
        stats.forEachIndexed { index, value -> tot += index * value }
        val average = tot / sum.toFloat() / (obj.digestSize * 8)

        return "Samples: " + sum + "\n" +
                "Average: " + average + "\n" +
                "Deviation: " + (.5 - average) + "\n" +
                "Spread: " + (1 - usage / (obj.digestSize * 8).toFloat()) + "\n" +
                "Duration: " + duration + "\n"
    }
}