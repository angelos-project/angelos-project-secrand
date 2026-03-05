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

import kotlin.time.Duration
import org.angproj.sec.Mocks
import org.angproj.sec.Stubs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class BenchmarkTesterTest {

    @Test
    fun testSamplesAsked() {
        assertFailsWith<IllegalArgumentException> {
            Mocks.mockBenchmarkTester(0, 16, Stubs.stubBenchmarkArticle())
        }
    }

    @Test
    fun testAtomicSampleByteSize() {
        assertFailsWith<IllegalArgumentException> {
            Mocks.mockBenchmarkTester(1, 0, Stubs.stubBenchmarkArticle())
        }
    }

    @Test
    fun testCalculateSampleSamplesTaken() {
        val tester = Mocks.mockBenchmarkTester(1, 16, Stubs.stubBenchmarkArticle())

        tester.calculateSample(ByteArray(32))

        assertEquals(1L, tester.samplesTaken)
    }

    @Test
    fun testCalculateSampleFail() {
        val tester = Mocks.mockBenchmarkTester(1, 16, Stubs.stubBenchmarkArticle())

        assertFailsWith<IllegalStateException>{
            tester.calculateSample(ByteArray(24))
        }
    }

    @Test
    fun testNeededSampleDataByteSize() {
        val tester = Mocks.mockBenchmarkTester(10, 16, Stubs.stubBenchmarkArticle())

        assertEquals(16L * 10L, tester.neededSampleDataByteSize)
    }

    @Test
    fun testCollectStatsOversized() {
        val tester = Mocks.mockBenchmarkTester(2, 16, Stubs.stubBenchmarkArticle())

        tester.calculateSample(ByteArray(32))
        val stats = tester.collectStats()

        assertEquals(2, stats.sampleCount)
        assertEquals(2.0, stats.keyValue)
        assertEquals(Duration.INFINITE, stats.duration)
        assertEquals(32, stats.dataSize)
    }

    @Test
    fun testCollectStatsOversizedArbitrary() {
        val tester = Mocks.mockBenchmarkTester(1, 16, Stubs.stubBenchmarkArticle())

        tester.calculateSample(ByteArray(32))
        val stats = tester.collectStats()

        assertEquals(1, stats.sampleCount)
        assertEquals(1.0, stats.keyValue)
        assertEquals(Duration.INFINITE, stats.duration)
        assertEquals(16, stats.dataSize)
    }
}