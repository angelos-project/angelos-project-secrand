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

/**
 * Manages a benchmarking session for a given benchmark object and its testers.
 *
 * @param B The type of the object being benchmarked.
 * @param E The type of the BenchmarkObject wrapper.
 * @property sampleCount The total number of samples to be collected during the session.
 * @property subSampleByteSize The size of each sub-sample in bytes.
 * @property obj The benchmark object instance to be tested.
 *
 * This class allows registering multiple testers, starting and stopping the benchmarking run,
 * collecting samples, and finalizing the collection to retrieve statistical results.
 */
public class BenchmarkSession<B, E: BenchmarkObject<B>>(
    public val sampleCount: Long,
    public val subSampleByteSize: Int,
    private val obj: E
) {

    private enum class RunState {
        INITIALIZE, RUNNING, FINISHED
    }

    private val numSubSamples: Int = obj.sampleByteSize / subSampleByteSize

    private var state: RunState = RunState.INITIALIZE

    private val registry: MutableMap<String, BenchmarkTester<B, E>> = mutableMapOf()

    /**
     * Registers a new tester for the benchmarking session.
     *
     * @param builder A function that takes the benchmark object and returns a BenchmarkTester instance.
     * @return A unique token identifying the registered tester.
     * @throws IllegalStateException if called after the session has started running.
     */
    public fun registerTester(builder: (E) -> BenchmarkTester<B, E>): String {
        check(state == RunState.INITIALIZE) { "Can't register new state after INITIALIZE state." }
        val tester = builder(obj)
        val token  = tester::class.toString()
        registry[token] = tester
        return token
    }

    init {
        require(obj.sampleByteSize % subSampleByteSize == 0) { "Sub sample size must be divisible with sample byte size" }
        require(sampleCount > 0) { "Samples taken must be set above one" }
    }

    /**
     * Collects a sample from the benchmark object and processes it with all registered testers.
     *
     * @throws IllegalStateException if called when the session is not in the RUNNING state.
     */
    public fun collectSample() {
        check(state == RunState.RUNNING) { "Benchmarking must be in RUNNING state." }
        val sample = obj.nextSample()
        if (numSubSamples > 1) repeat(numSubSamples) {
            val startIdx = it * subSampleByteSize
            val subSample = sample.copyOfRange(startIdx, startIdx + subSampleByteSize)
            registry.forEach { t -> t.value.calculateSample(subSample) }
        } else {
            registry.forEach { it.value.calculateSample(sample) }
        }
    }

    /**
     * Starts the benchmarking run by initializing all registered testers.
     *
     * @throws IllegalStateException if called when the session is not in the INITIALIZE state
     *                               or if no testers have been registered.
     */
    public fun startRun() {
        check(state == RunState.INITIALIZE)
        check(registry.isNotEmpty()) { "Testers registry must not be empty" }
        state = RunState.RUNNING
        registry.forEach { it.value.start() }
    }

    /**
     * Stops the benchmarking run by finalizing all registered testers.
     *
     * @throws IllegalStateException if called when the session is not in the RUNNING state.
     */
    public fun stopRun() {
        check(state == RunState.RUNNING)
        state = RunState.FINISHED
        registry.forEach { it.value.stop() }
    }

    /**
     * Finalizes the collection of statistics from all registered testers.
     *
     * @return A map of tester names to their collected statistical results.
     * @throws IllegalStateException if called when the session is not in the FINISHED state.
     */
    public fun finalizeCollecting(): Map<String, Statistical> {
        return buildMap {
            registry.forEach {
                put(it.key, it.value.collectStats())
            }
        }.toMap()
    }
}














