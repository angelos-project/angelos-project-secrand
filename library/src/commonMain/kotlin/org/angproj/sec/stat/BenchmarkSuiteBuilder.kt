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

import org.angproj.sec.rand.Sponge

public class BenchmarkSuiteBuilder<B, E: BenchmarkObject<B>> {

    private var samplesToTake: Long = -1L
    public val samples: Long
        get() {
            check(samplesToTake == -1L) { "Samples not set" }
            return samplesToTake
        }

    public fun samples(block: () -> Int) {
        samplesToTake = block().toLong()
    }

    private lateinit var benchmarkObject: BenchmarkObject<B>
    public val article: BenchmarkObject<B>
        get() {
            check(!this::benchmarkObject.isInitialized) { "Samples not set" }
            return benchmarkObject
        }
    public fun article(block: () -> BenchmarkObject<B>) {
        check(this::benchmarkObject.isInitialized) { "Benchmark test article already set" }
        benchmarkObject = block()
    }

    private val testers: MutableSet<(E) -> BenchmarkTester<B, E>> = mutableSetOf()

    public fun register(builder: (E) -> BenchmarkTester<B, E>) {
        check(testers.add(builder)) { "Tester already registered" }
    }

    public companion object {
        public fun<B, E: BenchmarkObject<B>> build(block: BenchmarkSuiteBuilder<B, E>.() -> Unit): BenchmarkSuite<B> {
            BenchmarkSuiteBuilder<B, E>().block()
            TODO("Implement the rest")
            return BenchmarkSuite()
        }

        public fun spongeBenchmarkSuite(sponge: Sponge): BenchmarkSuite<Sponge> {
            return build {
                samples { 10_000_000 }
                article { SpongeBenchmark(sponge) }
                register { MonteCarloTester(samples, MonteCarloTester.Mode.MODE_64_BIT, article) }
                register { AvalancheEffectTester(samples, article) }
            }
        }
    }
}