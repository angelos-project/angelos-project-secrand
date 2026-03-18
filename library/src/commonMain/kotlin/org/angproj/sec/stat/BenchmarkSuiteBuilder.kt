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
 * A builder class for constructing benchmark suites with configurable samples, articles, and testers.
 * It allows setting the number of samples, the benchmark article, and registering multiple testers.
 *
 * @param B The type of the benchmark object.
 * @param E The type of the benchmark article, extending BenchmarkArticle<B>.
 */
public class BenchmarkSuiteBuilder<B, E: BenchmarkArticle<B>> {

    private var samplesToTake: Long = -1L

    /**
     * The number of samples to take, as set by the user.
     */
    public val samples: Long
        get() {
            check(samplesToTake != -1L) { "Samples not set" }
            return samplesToTake
        }

    /**
     * Sets the number of samples to take.
     *
     * @param block A lambda that returns the number of samples as an Int.
     */
    public fun samples(block: () -> Int) {
        check(samplesToTake == -1L) { "Samples already set" }
        samplesToTake = block().toLong()
    }

    private lateinit var benchmarkArticle: BenchmarkArticle<B>

    /**
     * The benchmark article instance.
     */
    public val article: BenchmarkArticle<B>
        get() {
            check(this::benchmarkArticle.isInitialized) { "Article not set" }
            return benchmarkArticle
        }

    /**
     * Sets the benchmark article.
     *
     * @param block A lambda that returns the benchmark article instance.
     */
    public fun article(block: () -> BenchmarkArticle<B>) {
        check(!this::benchmarkArticle.isInitialized) { "Benchmark article already set" }
        benchmarkArticle = block()
    }

    internal val testers: MutableSet<(E) -> BenchmarkTester<B, E>> = mutableSetOf()

    /**
     * Registers a tester builder function.
     *
     * @param builder A function that takes a benchmark article and returns a benchmark tester.
     */
    public fun register(builder: (E) -> BenchmarkTester<B, E>) {
        testers.add(builder)
    }

    public companion object {

        /**
         * Builds a benchmark suite using the provided configuration block.
         *
         * @param B The type of the benchmark object.
         * @param E The type of the benchmark article.
         * @param block A configuration block for the builder.
         * @return A constructed BenchmarkSuite instance.
         */
        public fun<B, E: BenchmarkArticle<B>> build(block: BenchmarkSuiteBuilder<B, BenchmarkArticle<B>>.() -> Unit): BenchmarkSuite<B> {
            val suiteBuilder = BenchmarkSuiteBuilder<B, BenchmarkArticle<B>>().apply { block() }
            return BenchmarkSuite(suiteBuilder)
        }
    }
}