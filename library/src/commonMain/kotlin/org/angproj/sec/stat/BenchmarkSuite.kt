/**
 * Copyright (c) 2026 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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

public class BenchmarkSuite<B>(
    suiteBuilder: BenchmarkSuiteBuilder<B, BenchmarkObject<B>>) {

    private val benchmarkObject = suiteBuilder.article
    private var benchmarkSession: BenchmarkSession<B, BenchmarkObject<B>>
    private val testersByName: MutableList<String> = mutableListOf()
    private lateinit var results: Map<String, Statistical>

    // TODO
    private val samplesNeeded = suiteBuilder.samples //MonteCarloTester.Mode.MODE_64_BIT.size * suiteBuilder.samples / benchmarkObject.sampleByteSize


    init {
        benchmarkSession = BenchmarkSession(
            samplesNeeded,
            benchmarkObject.sampleByteSize,
            benchmarkObject
        )

        suiteBuilder.testers.forEach { tester ->
            testersByName.add(benchmarkSession.registerTester(tester))
        }
    }

    public suspend fun run() {
        runBlocking()
    }

    public fun runBlocking() {
        benchmarkSession.startRun()
        while(!benchmarkSession.satisfied) {
            benchmarkSession.collectSample()
        }
        benchmarkSession.stopRun()

        results = benchmarkSession.finalizeCollecting()
    }

    public fun collectResults(): Map<String, Statistical> {
        check(this::results.isInitialized) { "Data sampling not finished" }
        return results
    }

    override fun toString(): String {
        if(!this::results.isInitialized) return "No result, still running"
        return buildString {
            testersByName.forEach { tester ->
                append(results[tester]!!.report)
                appendLine()
            }
        }
    }
}