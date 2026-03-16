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

import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

/**
 * An abstract base class for benchmark testers that perform statistical tests on random samples.
 * It manages the sampling process, timing, and provides methods for calculating and collecting statistics.
 *
 * @param B The type of the benchmark object.
 * @param E The type of the benchmark article, extending BenchmarkArticle<B>.
 * @property samplesAsked The number of samples requested.
 * @property atomicSampleByteSize The byte size of each atomic sample unit.
 * @property benchmarkArticle The benchmark article providing the samples.
 */
public abstract class BenchmarkTester<B, E: BenchmarkArticle<B>>(
    public val samplesAsked: Long,
    public val atomicSampleByteSize: Int,
    protected val benchmarkArticle: E
) {

    init {
        require(samplesAsked > 0) { "At least one sample is asked for" }
        require(atomicSampleByteSize > 0) { "Atomic size has to be above 0" }
    }

    protected lateinit var startTime: TimeMark
    protected var duration: Duration = Duration.INFINITE

    protected var totalTakenSamples: Long = 0

    /**
     * The total number of samples taken so far.
     */
    public val samplesTaken: Long
        get() = totalTakenSamples

    /**
     * The total byte size of data needed for the requested samples.
     */
    public val neededSampleDataByteSize: Long
        get() = samplesAsked * atomicSampleByteSize

    /**
     * The number of samples still left to take.
     */
    public val samplesLeft: Long
        get() = samplesAsked - samplesTaken

    protected fun maxLoops(sampleSize: Int): Int = min(sampleSize.toLong() / atomicSampleByteSize, samplesLeft).toInt()

    /**
     * Starts the timing for the benchmark.
     */
    public fun start() {
        startTime = TimeSource.Monotonic.markNow()
    }

    /**
     * Stops the timing for the benchmark.
     */
    public fun stop() {
        duration = startTime.elapsedNow() // Duration in nanoseconds
    }

    /**
     * Abstract method to calculate statistics from a sample.
     *
     * @param sample The byte array sample to process.
     */
    protected abstract fun calculateSampleImpl(sample: ByteArray)

    /**
     * Calculates statistics from the provided sample.
     *
     * @param sample The byte array sample to process.
     * @throws IllegalArgumentException if the sample size is not divisible by atomicSampleByteSize.
     */
    public fun calculateSample(sample: ByteArray) {
        check(sample.size % atomicSampleByteSize == 0) { "Sample not divisible by atomic sample size" }
        calculateSampleImpl(sample)
    }

    protected abstract fun collectStatsImpl(): Statistical

    /**
     * Collects the final statistics after testing.
     *
     * @return A Statistical object containing the results.
     */
    public fun collectStats(): Statistical {
        return collectStatsImpl()
    }
}