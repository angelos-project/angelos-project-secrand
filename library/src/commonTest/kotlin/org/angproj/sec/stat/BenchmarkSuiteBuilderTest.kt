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

import org.angproj.sec.Stubs
import kotlin.random.Random

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BenchmarkSuiteBuilderTest {

    @Test
    fun testDslMissingSamples() {
        assertFailsWith<IllegalStateException> {
            BenchmarkSuiteBuilder.build {
                //samples { 1 }
                article { Stubs.stubBenchmarkArticle() }
                register { Stubs.stubBenchmarkTester(samples, article) }
            }
        }
    }

    @Test
    fun testDslSamplesSetTwice() {
        assertFailsWith<IllegalStateException> {
            BenchmarkSuiteBuilder.build {
                samples { 1 }
                samples { 2 }
                article { Stubs.stubBenchmarkArticle() }
                register { Stubs.stubBenchmarkTester(samples, article) }
            }
        }
    }

    @Test
    fun testDslMissingArticle() {
        assertFailsWith<IllegalStateException> {
            BenchmarkSuiteBuilder.build<Random, BenchmarkArticle<Random>> {
                samples { 1 }
                //article { Stubs.stubBenchmarkArticle() }
                register { Stubs.stubBenchmarkTester(samples, article) }
            }
        }
    }

    @Test
    fun testDslArticleTwice() {
        assertFailsWith<IllegalStateException> {
            BenchmarkSuiteBuilder.build {
                samples { 1 }
                article { Stubs.stubBenchmarkArticle() }
                article { Stubs.stubBenchmarkArticle() }
                register { Stubs.stubBenchmarkTester(samples, article) }
            }
        }
    }

    @Test
    fun testDslMissingTesters() {
        val suite = BenchmarkSuiteBuilder.build {
            samples { 1 }
            article { Stubs.stubBenchmarkArticle() }
            //register { Stubs.stubBenchmarkTester(samples, article) }
        }

        assertFailsWith<IllegalStateException> {
            suite.runBlocking()
        }
    }

    @Test
    fun testDslSameTesterTwice() {
        assertFailsWith<IllegalStateException> {
            BenchmarkSuiteBuilder.build {
                samples { 1 }
                article { Stubs.stubBenchmarkArticle() }
                register { Stubs.stubBenchmarkTester(samples, article) }
                register { Stubs.stubBenchmarkTester(samples, article) }
            }
        }
    }

    @Test
    fun testBenchmarkTesterMock() {
        val suite = BenchmarkSuiteBuilder.build {
            samples { 1 }
            article { Stubs.stubBenchmarkArticle() }
            register { Stubs.stubBenchmarkTester(samples, article) }
        }

        suite.runBlocking()

        // The stub is an object, that's why classname "null"
        assertEquals(1, suite.collectResults()["null"]!!.sampleCount)
    }

    @Test
    fun testMonteCarlo32BitModeMock() {
        val suite = BenchmarkSuiteBuilder.build {
            samples { 20 }
            article { Stubs.stubBenchmarkArticle() }
            register { MonteCarloTester(samples, MonteCarloTester.Mode.MODE_32_BIT, article) }
        }

        suite.runBlocking()

        assertEquals(20, suite.collectResults()["MonteCarloTester"]!!.sampleCount)
    }

    @Test
    fun testMonteCarlo64BitModeMock() {
        val suite = BenchmarkSuiteBuilder.build {
            samples { 20 }
            article { Stubs.stubBenchmarkArticle() }
            register { MonteCarloTester(samples, MonteCarloTester.Mode.MODE_64_BIT, article) }
        }

        suite.runBlocking()

        assertEquals(20, suite.collectResults()["MonteCarloTester"]!!.sampleCount)
    }

    @Test
    fun testChiSquareMock() {
        val suite = BenchmarkSuiteBuilder.build {
            samples { 1 }
            article { Stubs.stubBenchmarkArticle() }
            register { ChiSquareTester(samples, article) }
        }

        suite.runBlocking()

        assertEquals(1, suite.collectResults()["ChiSquareTester"]!!.sampleCount)
    }
}