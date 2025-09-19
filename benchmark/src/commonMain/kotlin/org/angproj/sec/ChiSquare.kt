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

import org.angproj.sec.stat.Benchmark
import org.angproj.sec.stat.BenchmarkObject
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.TimeSource

public abstract class ChiObject<E>(obj: E) : BenchmarkObject<E>(obj) {
    public abstract val byteSize: Int

    public abstract fun digest(data: ByteArray)
}

public class ChiSquare<B>(
    samples: Int, config: () -> ChiObject<B>
) : Benchmark<B, ChiObject<B>>(samples, config) {

    private val frequencies = IntArray(256)
    private var duration: Duration = Duration.INFINITE

    override fun calculateData() {
        val startTime = TimeSource.Monotonic.markNow()

        val digest = ByteArray(obj.byteSize)

        var total = 0
        while (samples >= total) {
            obj.digest(digest)
            repeat(digest.size) {
                frequencies[digest[it].toUByte().toInt()]++
                total++
            }
        }
        duration = startTime.elapsedNow() // Duration in nanoseconds
    }

    override fun toString(): String {
        val expected: Double = samples / 256.0
        var chiSquare = 0.0
        for (freq in frequencies) {
            chiSquare += (freq - expected).pow(2.0) / expected
        }

        return "Samples: " + samples + "\n" +
                "Chi square: " + chiSquare + "\n" +
                "Critical: 293.25" + "\n" +
                "Duration: " + duration + "\n"
    }
}