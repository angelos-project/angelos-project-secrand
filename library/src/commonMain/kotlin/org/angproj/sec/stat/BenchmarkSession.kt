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

import org.angproj.sec.util.RunState

/**
 * A session class for managing the execution of benchmark tests.
 * It handles registering testers, collecting samples, and controlling the run state.
 *
 * @param B The type of the benchmark object.
 * @param E The type of the benchmark article, extending BenchmarkArticle<B>.
 * @property samplesAsked The number of samples requested for the session.
 * @property benchmarkArticle The benchmark article providing the samples.
 */
public class BenchmarkSession<B, E: BenchmarkArticle<B>>(
    public val samplesAsked: Long,
    private val benchmarkArticle: E
) {

    private var state = RunState.INITIALIZE

    private val registry: MutableMap<String, BenchmarkTester<B, E>> = mutableMapOf()

    /**
     * Indicates whether all registered testers have collected the required number of samples.
     */
    public val satisfied: Boolean
        get() = registry.entries.all { it.value.samplesLeft <= 0 }

    /**
     * Registers a new tester in the session.
     *
     * @param builder A function that builds and returns a benchmark tester.
     * @return A unique token identifying the registered tester.
     * @throws IllegalStateException if called after initialization.
     * @throws IllegalArgumentException if a tester with the same name is already registered.
     */
    public fun registerTester(builder: (E) -> BenchmarkTester<B, E>): String {
        check(state == RunState.INITIALIZE) { "Can't register new state after INITIALIZE state." }
        val tester = builder(benchmarkArticle)
        val token  = tester::class.simpleName.toString()
        check(!registry.contains(token)) { "Tester already registered" }
        registry[token] = tester
        return token
    }

    init {
        require(samplesAsked > 0) { "Samples taken must be set above one" }
    }

    /**
     * Collects a sample and distributes it to all registered testers that still need samples.
     *
     * @throws IllegalStateException if the session is not in RUNNING state.
     */
    public fun collectSample() {
        check(state == RunState.RUNNING) { "Benchmarking must be in RUNNING state." }
        val sample = benchmarkArticle.nextSample()
        registry.forEach {
            if(it.value.samplesLeft > 0) it.value.calculateSample(sample)
        }
    }

    /**
     * Starts the benchmark run, initializing all testers.
     *
     * @throws IllegalStateException if not in INITIALIZE state or no testers are registered.
     */
    public fun startRun() {
        check(state == RunState.INITIALIZE)
        check(registry.isNotEmpty()) { "Testers registry must not be empty" }

        state = RunState.RUNNING
        registry.forEach { it.value.start() }
    }

    /**
     * Stops the benchmark run, finalizing timing for all testers.
     *
     * @throws IllegalStateException if not in RUNNING state.
     */
    public fun stopRun() {
        check(state == RunState.RUNNING)
        state = RunState.FINISHED
        registry.forEach { it.value.stop() }
    }

    /**
     * Finalizes the session and collects statistics from all testers.
     *
     * @return A map of tester names to their statistical results.
     */
    public fun finalizeCollecting(): Map<String, Statistical> {
        return buildMap {
            registry.forEach {
                put(it.key, it.value.collectStats())
            }
        }.toMap()
    }
}














