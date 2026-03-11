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


public class BenchmarkSuiteBuilder<B, E: BenchmarkArticle<B>> {

    private var samplesToTake: Long = -1L
    public val samples: Long
        get() {
            check(samplesToTake != -1L) { "Samples not set" }
            return samplesToTake
        }

    public fun samples(block: () -> Int) {
        check(samplesToTake == -1L) { "Samples already set" }
        samplesToTake = block().toLong()
    }

    private lateinit var benchmarkArticle: BenchmarkArticle<B>
    public val article: BenchmarkArticle<B>
        get() {
            check(this::benchmarkArticle.isInitialized) { "Article not set" }
            return benchmarkArticle
        }
    public fun article(block: () -> BenchmarkArticle<B>) {
        check(!this::benchmarkArticle.isInitialized) { "Benchmark article already set" }
        benchmarkArticle = block()
    }

    internal val testers: MutableSet<(E) -> BenchmarkTester<B, E>> = mutableSetOf()

    public fun register(builder: (E) -> BenchmarkTester<B, E>) {
        testers.add(builder)
    }

    public companion object {
        public fun<B, E: BenchmarkArticle<B>> build(block: BenchmarkSuiteBuilder<B, BenchmarkArticle<B>>.() -> Unit): BenchmarkSuite<B> {
            val suiteBuilder = BenchmarkSuiteBuilder<B, BenchmarkArticle<B>>().apply { block() }
            return BenchmarkSuite(suiteBuilder)
        }
    }
}