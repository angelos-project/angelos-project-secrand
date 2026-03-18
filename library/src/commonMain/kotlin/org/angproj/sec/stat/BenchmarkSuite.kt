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

/**
 * A suite class for running a collection of benchmark tests.
 * It encapsulates the benchmark session and provides methods to execute the suite and collect results.
 *
 * @param B The type of the benchmark object.
 * @property suiteBuilder The builder used to configure the suite.
 */
public class BenchmarkSuite<B>(
    suiteBuilder: BenchmarkSuiteBuilder<B, BenchmarkArticle<B>>) {

    private val benchmarkObject = suiteBuilder.article
    private var benchmarkSession: BenchmarkSession<B, BenchmarkArticle<B>>
    private val testersByName: MutableList<String> = mutableListOf()
    private val samplesNeeded = suiteBuilder.samples
    private lateinit var results: Map<String, Statistical>

    init {
        benchmarkSession = BenchmarkSession(
            samplesNeeded,
            benchmarkObject
        )

        suiteBuilder.testers.forEach { tester ->
            testersByName.add(benchmarkSession.registerTester(tester))
        }
    }

    /**
     * Runs the benchmark suite in a blocking manner until all samples are collected.
     */
    public fun runBlocking() {
        benchmarkSession.startRun()
        while(!benchmarkSession.satisfied) {
            benchmarkSession.collectSample()
        }
        benchmarkSession.stopRun()

        results = benchmarkSession.finalizeCollecting()
    }

    /**
     * Collects the results of the benchmark suite.
     *
     * @return A map of tester names to their statistical results.
     * @throws IllegalStateException if the suite has not been run yet.
     */
    public fun collectResults(): Map<String, Statistical> {
        check(this::results.isInitialized) { "Data sampling not finished" }
        return results
    }
}