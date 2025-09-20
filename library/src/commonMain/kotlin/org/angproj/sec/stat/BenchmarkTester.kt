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
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

/**
 * Abstract base class for running benchmarks on a given object.
 *
 * @param B The type of the object being benchmarked.
 * @param E The type of the BenchmarkObject wrapper.
 * @property samples The number of samples to collect during benchmarking.
 * @property obj The benchmark object instance, created by the provided config function.
 *
 * Extend this class to implement specific benchmarking logic.
 * Use the config function to initialize the object to be benchmarked.
 */
public abstract class BenchmarkTester<B, E: BenchmarkObject<B>>(
    public val samples: Long,
    public val atomicSampleByteSize: Int,
    protected val obj: E
) {

    protected lateinit var startTime: TimeMark
    protected var duration: Duration = Duration.INFINITE

    /**
     * Mandatory name for what is being tested
     * */
    public abstract fun name(): String

    public fun start() {
        startTime = TimeSource.Monotonic.markNow()
    }

    public fun stop() {
        duration = startTime.elapsedNow() // Duration in nanoseconds
    }

    public abstract fun calculateSampleImpl(sample: ByteArray)

    /**
     * Runs the benchmark and collects data.
     */
    public fun calculateSample(sample: ByteArray) {
        check(sample.size % atomicSampleByteSize == 0) { "Sample not divisible by atomic sample size" }
        calculateSampleImpl(sample)
    }

    public abstract fun collectStatsImpl(): Statistical

    public fun collectStats(): Statistical {
        return collectStatsImpl()
    }

    /**
     * Returns a string representation of the benchmark results.
     */
    public abstract override fun toString(): String


    public fun ByteArray.readIntBE(offset: Int): Int = Octet.readLE(this, offset, TypeSize.intSize) { index ->
            this[index]
    }.toInt()

    public fun ByteArray.readLongBE(offset: Int): Long = Octet.readLE(this, offset, TypeSize.longSize) { index ->
        this[index]
    }
}