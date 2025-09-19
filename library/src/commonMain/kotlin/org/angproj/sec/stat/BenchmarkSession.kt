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

    public fun registerTester(builder: (E) -> BenchmarkTester<B, E>): Boolean {
        check(state == RunState.INITIALIZE) { "Can't register new state after INITIALIZE state." }
        val tester = builder(obj)
        registry[tester.name()] = tester
        return true
    }

    init {
        require(registry.isNotEmpty()) { "Testers registry must not be empty" }
        require(obj.sampleByteSize % subSampleByteSize == 0) { "Sub sample size must be divisible with sample byte size" }
        require(sampleCount > 0) { "Samples taken must be set above one" }
    }

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

    public fun startRun() {
        check(state == RunState.INITIALIZE)
        state = RunState.RUNNING
    }

    public fun stopRun() {
        check(state == RunState.RUNNING)
        state = RunState.FINISHED
    }

    public fun finalizeCollecting(): Map<String, Statistical> {
        return buildMap {
            registry.forEach {
                put(it.value.name(), it.value.collectStats())
            }
        }.toMap()
    }
}














