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

import org.angproj.sec.Stubs
import kotlin.test.Test
import kotlin.test.assertEquals

class MonteCarloTesterTest {

    @Test
    fun testMonteCarloTester32BitMode() {
        val suite = BenchmarkSuiteBuilder.build {
            samples { 1 }
            article { Stubs.stubBenchmarkArticle() }
            register { MonteCarloTester(samples, MonteCarloTester.Mode.MODE_32_BIT, article) }
        }

        suite.runBlocking()

        assertEquals(1, suite.collectResults()[MonteCarloTester::class.simpleName]!!.sampleCount)
    }

    @Test
    fun testMonteCarloTester64BitMode() {
        val suite = BenchmarkSuiteBuilder.build {
            samples { 1 }
            article { Stubs.stubBenchmarkArticle() }
            register { MonteCarloTester(samples, MonteCarloTester.Mode.MODE_64_BIT, article) }
        }

        suite.runBlocking()

        assertEquals(1, suite.collectResults()[MonteCarloTester::class.simpleName]!!.sampleCount)
    }
}