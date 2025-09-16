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
package org.angproj.sec.util

import kotlin.test.Test
import kotlin.test.assertTrue

// Mock BenchmarkObject
class MockBenchmarkObject : BenchmarkObject<String>("mock") {
    var called = false
}

// Mock Benchmark using MockBenchmarkObject
class MockBenchmark(
    samples: Int,
    config: () -> MockBenchmarkObject
) : Benchmark<String, MockBenchmarkObject>(samples, config) {
    var calculated = false

    override fun calculateData() {
        obj.called = true
        calculated = true
    }

    override fun toString(): String = "MockBenchmark: samples=$samples, called=${obj.called}"
}

class BenchmarkTest {
    @Test
    fun testBenchmarkCalculateDataAndToString() {
        val benchmark = MockBenchmark(10) { MockBenchmarkObject() }
        benchmark.calculateData()
        val result = benchmark.toString()
        assertTrue(benchmark.calculated)
        assertTrue(result.contains("samples=10"))
        assertTrue(result.contains("called=true"))
    }
}